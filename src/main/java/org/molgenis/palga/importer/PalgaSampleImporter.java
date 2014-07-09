package org.molgenis.palga.importer;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.collect.Maps;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.ImmutableSettings.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.molgenis.palga.Agegroup;
import org.molgenis.palga.Diagnosis;
import org.molgenis.palga.Gender;
import org.molgenis.palga.Material;
import org.molgenis.palga.PalgaSample;
import org.molgenis.palga.RetrievalTerm;
import org.molgenis.security.runas.RunAsSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Sets;

/**
 * Palga sample file importer.
 * 
 * Can be used stanalone and from the importer ui.
 * 
 * Appends all data to the PalgaSample table
 */
@Service
public class PalgaSampleImporter
{
	private static final Logger logger = Logger.getLogger(PalgaSampleImporter.class);

	private static final int DIAGNOSE_COLUMN = 2;
	private static final int RETRIEVAL_TERM_COLUMN = 3;
	private static final int MATERIAAL_COLUMN = 4;
	private static final int JAAR_COLUMN = 5;
	private static final int GESLACHT_COLUMN = 6;
	private static final int LEEFTIJD_COLUMN = 7;
	private static final char SEPARATOR = '|';
	private static final String IN_COLUMN_SEPARATOR = "\\*";
	private static int BATCH_SIZE = 10000;

	private final Map<String, String> materialMapping = Maps.newHashMap();
	private final Map<String, String> geslachtMapping = Maps.newHashMap();

	private final String dbUser;
	private final String dbPassword;

	@Autowired
	public PalgaSampleImporter(@Value("${db_user:@null}") String dbUser,
			@Value("${db_password:@null}") String dbPassword)
	{
		materialMapping.put("C", "Cytologie");
		materialMapping.put("T", "Histologie");
		geslachtMapping.put("m", "Man");
		geslachtMapping.put("v", "Vrouw");

		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

	@Async
	@RunAsSystem
	public void importFile(File file) throws InvalidFormatException, IOException
	{
		Map<String, String> props = Maps.newHashMap();
		props.put("javax.persistence.jdbc.user", dbUser);
		props.put("javax.persistence.jdbc.password", dbPassword);

		EntityManager entityManager = Persistence.createEntityManagerFactory("palga-import", props)
				.createEntityManager();

		String fileName = file.getAbsolutePath();
		logger.info("Import of palga sample file [" + fileName + "] started");

		Map<String, RetrievalTerm> retrievalTerms = getRetrievalTerms(entityManager);
		Map<String, Diagnosis> diagnosis = getDiagnosis(entityManager);
		Map<String, Material> materials = getMaterials(entityManager);
		Map<String, Gender> genders = getGenders(entityManager);
		Map<String, Agegroup> agegroups = getAgeGroups(entityManager);

		Builder builder = ImmutableSettings.settingsBuilder().loadFromClasspath("elasticsearch.yml");
		Settings settings = builder.build();
		Node node = nodeBuilder().settings(settings).local(true).node();
		Client client = node.client();

		CSVReader reader = new CSVReader(new FileReader(file), SEPARATOR);
		reader.readNext();// header

		long t0 = System.currentTimeMillis();
		long count = 0;
		long row = 2;
		try
		{
			createMappings(client);
			BulkRequestBuilder bulkRequest = null;
			long start = 0;
			String[] line = reader.readNext();
			while (line != null)
			{
				if (count % BATCH_SIZE == 0)
				{
					start = count;
					bulkRequest = client.prepareBulk();
				}

				Map<String, Object> sample = createPalgaSample(line, row++, diagnosis, retrievalTerms, materials,
						genders, agegroups);
				if (sample != null)
				{
					bulkRequest.add(client.prepareIndex("molgenis", PalgaSample.ENTITY_NAME).setSource(sample));
					count++;
				}

				// Commit if BATCH_SIZE is reached
				if (count == (start + BATCH_SIZE))
				{
					BulkResponse bulkResponse = bulkRequest.execute().actionGet();
					if (bulkResponse.hasFailures())
					{
						throw new RuntimeException("error while indexing row [" + count + "]: " + bulkResponse);
					}

					long t = (System.currentTimeMillis() - t0) / 1000;
					logger.info("Imported [" + count + "] rows in [" + t + "] sec.");
				}
				line = reader.readNext();
			}

			// Commit the rest
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures())
			{
				throw new RuntimeException("error while indexing row [" + count + "]: " + bulkResponse);
			}

			long t = (System.currentTimeMillis() - t0) / 1000;
			logger.info("Import of palga sample file [" + fileName + "] completed in " + t + " sec. Added [" + count
					+ "] rows.");
		}
		catch (Exception e)
		{
			logger.error("Exception importing palga sample file [" + fileName + "] ", e);
			entityManager.getTransaction().rollback();
		}
		finally
		{
			IOUtils.closeQuietly(reader);
		}
	}

	private Map<String, Object> createPalgaSample(String[] csvRow, long row, Map<String, Diagnosis> diagnosis,
			Map<String, RetrievalTerm> retrievalTerms, Map<String, Material> materials, Map<String, Gender> genders,
			Map<String, Agegroup> agegroups)
	{
		Map<String, Object> sample = Maps.newHashMapWithExpectedSize(10);

		// Diagnosis
		String diagnose = csvRow[DIAGNOSE_COLUMN];
		if (StringUtils.isBlank(diagnose))
		{
			logger.warn("Palga-code column of row [" + row + "] is empty");
			return null;
		}
		String[] diagnoseArray = diagnose.split(IN_COLUMN_SEPARATOR);
		Set<String> diagnoses = Sets.newHashSetWithExpectedSize(diagnoseArray.length);
		for (String code : diagnoseArray)
		{
			Diagnosis d = diagnosis.get(code);
			if (d != null)
			{
				diagnoses.add(d.getIdentifier());
			}
		}
		if (!diagnoses.isEmpty())
		{
			sample.put("diagnose", diagnoses);
		}
		else
		{
			logger.warn("Missing PALGA codes on row [" + row + "]");
		}

		// RetrievalTerms
		String termIdentifiers = csvRow[RETRIEVAL_TERM_COLUMN];
		{
			String[] termIdentifiersArray = termIdentifiers.split(IN_COLUMN_SEPARATOR);
			Set<String> retrievalTermDescriptions = Sets.newHashSetWithExpectedSize(termIdentifiersArray.length);
			for (String termIdentifier : termIdentifiersArray)
			{
				if (StringUtils.isNotBlank(termIdentifier))
				{
					RetrievalTerm term = retrievalTerms.get(termIdentifier);
					if (term == null)
					{
						logger.warn("Unknown Retrievalterm [" + termIdentifier + "] on row [" + row + "]");
						return null;
					}

					retrievalTermDescriptions.add(term.getIdentifier());
				}
			}
			if (!retrievalTermDescriptions.isEmpty())
			{
				sample.put("retrievalTerm", retrievalTermDescriptions);
			}
		}

		// Materials
		String materialTypeCode = csvRow[MATERIAAL_COLUMN];
		if (StringUtils.isBlank(materialTypeCode))
		{
			logger.warn("'Soort onderzoek' column of row [" + row + "] is empty");
			return null;
		}
		String materialType = materialMapping.get(materialTypeCode);
		if (materialType == null)
		{
			logger.warn("Unknown material [" + materialTypeCode + "] on row [" + row + "]");
			return null;
		}
		Material material = materials.get(materialType);
		if (material == null)
		{
			logger.warn("Unknown material [" + materialType + "] on row [" + row + "]");
			return null;
		}
		else
		{
			sample.put("materiaal", material.getType());
		}

		// Year
		String year = csvRow[JAAR_COLUMN];
		if (StringUtils.isBlank(year))
		{
			logger.warn("'Jaar onderzoek' column of row [" + row + "] is empty");
		}
		if (!StringUtils.isNumeric(year))
		{
			logger.warn("Invalid year [" + year + "] on row [" + row + "]");
			return null;
		}
		sample.put("jaar", Integer.valueOf(year));

		// Gender
		String genderCode = csvRow[GESLACHT_COLUMN];
		if (StringUtils.isBlank(genderCode))
		{
			logger.warn("Geslacht column of row [" + row + "] is empty");
			return null;
		}
		String genderStr = geslachtMapping.get(genderCode);
		if (genderStr == null)
		{
			logger.warn("Unknown gender [" + genderCode + "] on row [" + row + "]");
			return null;
		}
		Gender gender = genders.get(genderStr);
		if (gender == null)
		{
			logger.warn("Unkown gender [" + genderStr + "] on row [" + row + "]");
			return null;
		}
		else
		{
			sample.put("geslacht", gender.getGender());
		}

		// Agegroups
		String agegroupCode = csvRow[LEEFTIJD_COLUMN];
		if (StringUtils.isBlank(agegroupCode))
		{
			logger.warn("Leeftijdscategorie column of row [" + row + "] is empty");
			return null;
		}

		Agegroup agegroup = agegroups.get(agegroupCode);
		if (agegroup == null)
		{
			logger.warn("Unknown agegroup [" + agegroup + "] on row [" + row + "]");
			return null;
		}
		sample.put("leeftijd", agegroup.getAgegroup());

		return sample;
	}

	private Map<String, RetrievalTerm> getRetrievalTerms(EntityManager entityManager)
	{
		Map<String, RetrievalTerm> result = Maps.newHashMap();
		List<RetrievalTerm> terms = entityManager.createQuery("SELECT r FROM RetrievalTerm r", RetrievalTerm.class)
				.getResultList();

		for (RetrievalTerm term : terms)
		{
			result.put(term.getIdentifier(), term);
		}

		return result;
	}

	private Map<String, Diagnosis> getDiagnosis(EntityManager entityManager)
	{
		Map<String, Diagnosis> result = Maps.newHashMap();
		List<Diagnosis> diagnosis = entityManager.createQuery("SELECT d FROM Diagnosis d", Diagnosis.class)
				.getResultList();

		for (Diagnosis d : diagnosis)
		{
			result.put(d.getIdentifier(), d);
		}

		return result;
	}

	private Map<String, Material> getMaterials(EntityManager entityManager)
	{
		Map<String, Material> results = Maps.newHashMap();
		List<Material> materials = entityManager.createQuery("SELECT m FROM Material m", Material.class)
				.getResultList();

		for (Material material : materials)
		{
			results.put(material.getType(), material);
		}

		return results;
	}

	private Map<String, Gender> getGenders(EntityManager entityManager)
	{
		Map<String, Gender> result = Maps.newHashMap();
		List<Gender> genders = entityManager.createQuery("SELECT g FROM Gender g", Gender.class).getResultList();

		for (Gender gender : genders)
		{
			result.put(gender.getGender(), gender);
		}

		return result;
	}

	private Map<String, Agegroup> getAgeGroups(EntityManager entityManager)
	{
		Map<String, Agegroup> result = Maps.newHashMap();
		List<Agegroup> agegroups = entityManager.createQuery("SELECT a FROM Agegroup a", Agegroup.class)
				.getResultList();

		for (Agegroup agegroup : agegroups)
		{
			result.put(agegroup.getAgegroup(), agegroup);
		}

		return result;
	}

	private void createMappings(Client client) throws IOException
	{
		XContentBuilder jsonBuilder = XContentFactory.jsonBuilder().startObject().startObject("PalgaSample");

//		jsonBuilder.startObject("_source").field("enabled", false).endObject();
		jsonBuilder.startObject("properties");
		jsonBuilder.startObject("diagnose").field("type", "string").field("store", "no").field("index", "not_analyzed")
				.endObject();
		jsonBuilder.startObject("retrievalTerm").field("type", "string").field("store", "no")
				.field("index", "not_analyzed").endObject();
		jsonBuilder.startObject("materiaal").field("type", "string").field("store", "no")
				.field("index", "not_analyzed").endObject();
		jsonBuilder.startObject("jaar").field("type", "string").field("store", "no").field("index", "not_analyzed")
				.endObject();
		jsonBuilder.startObject("geslacht").field("type", "string").field("store", "no").field("index", "not_analyzed")
				.endObject();
		jsonBuilder.startObject("leeftijd").field("type", "string").field("store", "no").field("index", "not_analyzed")
				.endObject();
		jsonBuilder.endObject(); // properties
		jsonBuilder.endObject(); // documentType
		jsonBuilder.endObject(); // PalgaSample

		logger.info("Going to create mapping [" + jsonBuilder.string() + "]");

		PutMappingResponse response = client.admin().indices().preparePutMapping("molgenis").setType("PalgaSample")
				.setSource(jsonBuilder).execute().actionGet();

		if (!response.isAcknowledged())
		{
			throw new ElasticsearchException("Creation of mapping for documentType [PalgaSample] failed. Response="
					+ response);
		}

		logger.info("Mapping for documentType [PalgaSample] created");
	}
}
