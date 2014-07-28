package org.molgenis.palga.importer;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.molgenis.MolgenisFieldTypes.MREF;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.molgenis.data.AttributeMetaData;
import org.molgenis.data.DataService;
import org.molgenis.data.EntityMetaData;
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

	private static final int EXCERPT_NUM = 0;
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
	private DataService dataService;

	@Autowired
	public PalgaSampleImporter(@Value("${db_user:@null}")
	String dbUser, @Value("${db_password:@null}")
	String dbPassword)
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
		if (!dataService.hasRepository(PalgaSample.ENTITY_NAME))
		{
			throw new RuntimeException("The repository " + PalgaSample.ENTITY_NAME + " does not exist!");
		}

		EntityMetaData entityMetaData = dataService.getEntityMetaData(PalgaSample.ENTITY_NAME);
		Map<String, String> props = Maps.newHashMap();
		props.put("javax.persistence.jdbc.user", dbUser);
		props.put("javax.persistence.jdbc.password", dbPassword);

		// Create a entityManager that could talk to the mysql database directly
		EntityManager entityManager = Persistence.createEntityManagerFactory("palga-import", props)
				.createEntityManager();

		String fileName = file.getAbsolutePath();
		logger.info("Import of palga sample file [" + fileName + "] started");

		Map<String, RetrievalTerm> retrievalTerms = getRetrievalTerms(entityManager);
		Map<String, Diagnosis> diagnosis = getDiagnosis(entityManager);
		Map<String, Material> materials = getMaterials(entityManager);
		Map<String, Gender> genders = getGenders(entityManager);
		Map<String, Agegroup> agegroups = getAgeGroups(entityManager);

		// load property file for the ElasticSearch
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
			String prevExcerptNum = null;
			BulkRequestBuilder bulkRequest = null;
			long start = 0;
			String[] line = reader.readNext();
			List<String[]> multipleLines = new ArrayList<String[]>();
			while (line != null)
			{
				// Remember the previous Excerpt number
				if (prevExcerptNum == null)
				{
					prevExcerptNum = line[EXCERPT_NUM];
				}

				if (prevExcerptNum.equals(line[EXCERPT_NUM]))
				{
					multipleLines.add(line);
				}
				else
				{
					// Remember the new Excerpt number
					prevExcerptNum = line[EXCERPT_NUM];

					if (count % BATCH_SIZE == 0)
					{
						start = count;
						bulkRequest = client.prepareBulk();
					}

					Map<String, Object> sample = createPalgaSample(multipleLines, row++, diagnosis, retrievalTerms,
							materials, genders, agegroups, entityMetaData);

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

					// Clear the old lines and add new line
					multipleLines.clear();
					multipleLines.add(line);
				}
				line = reader.readNext();
			}

			if (multipleLines.size() > 0)
			{
				Map<String, Object> sample = createPalgaSample(multipleLines, row++, diagnosis, retrievalTerms,
						materials, genders, agegroups, entityMetaData);
				if (sample != null)
				{
					bulkRequest.add(client.prepareIndex("molgenis", PalgaSample.ENTITY_NAME).setSource(sample));
					count++;
				}
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

	private Map<String, Object> createPalgaSample(List<String[]> csvRows, long row, Map<String, Diagnosis> diagnosis,
			Map<String, RetrievalTerm> retrievalTerms, Map<String, Material> materials, Map<String, Gender> genders,
			Map<String, Agegroup> agegroups, EntityMetaData entityMetaData)
	{
		Map<String, Object> sample = Maps.newHashMapWithExpectedSize(10);

		// For diagnose, we need to gather information from multiple lines
		// because one patient could have multiple diagnoses. However for
		// other columns, the information is the same, we could just take any
		// one of
		// the rows to get information from
		String[] csvRow = csvRows.get(0);

		// Diagnosis
		List<Map<String, Object>> diagnoses = new ArrayList<Map<String, Object>>();
		for (String[] eachRow : csvRows)
		{
			String diagnose = eachRow[DIAGNOSE_COLUMN];
			if (StringUtils.isBlank(diagnose))
			{
				logger.warn("Palga-code column of row [" + row + "] is empty");
				return null;
			}
			String[] diagnoseArray = diagnose.split(IN_COLUMN_SEPARATOR);
			for (String code : diagnoseArray)
			{
				if (StringUtils.isNotBlank(code))
				{
					Diagnosis d = diagnosis.get(code);
					if (d != null)
					{
						Map<String, Object> disgnosisInfo = new HashMap<String, Object>();
						for (String attributeName : d.getAttributeNames())
						{
							disgnosisInfo.put(attributeName, d.get(attributeName));
						}
						diagnoses.add(disgnosisInfo);
					}
				}
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
		List<Map<String, Object>> retrivalTerms = new ArrayList<Map<String, Object>>();
		for (String[] eachRow : csvRows)
		{
			String termIdentifiers = eachRow[RETRIEVAL_TERM_COLUMN];
			String[] termIdentifiersArray = termIdentifiers.split(IN_COLUMN_SEPARATOR);
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
					Map<String, Object> retrivalTermInfo = new HashMap<String, Object>();
					for (String attributeName : term.getAttributeNames())
					{
						retrivalTermInfo.put(attributeName, term.get(attributeName));
					}
					retrivalTerms.add(retrivalTermInfo);
				}
			}
		}
		if (!retrivalTerms.isEmpty())
		{
			sample.put("retrievalTerm", retrivalTerms);
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
		XContentBuilder jsonBuilder = XContentFactory.jsonBuilder().startObject().startObject(PalgaSample.ENTITY_NAME);

		jsonBuilder.startObject("properties");

		if (dataService.hasRepository(PalgaSample.ENTITY_NAME))
		{
			EntityMetaData entityMetaData = dataService.getEntityMetaData(PalgaSample.ENTITY_NAME);
			for (AttributeMetaData attributeMetaData : entityMetaData.getAttributes())
			{
				if (attributeMetaData.getDataType().getEnumType().toString().equalsIgnoreCase(MREF.toString()))
				{
					jsonBuilder.startObject(attributeMetaData.getName()).field("type", "nested")
							.startObject("properties");
					// TODO : what if the attributes in refEntity is also an
					// MREF
					// field?
					for (AttributeMetaData refEntityAttr : attributeMetaData.getRefEntity().getAttributes())
					{
						if (refEntityAttr.isLabelAttribute())
						{
							jsonBuilder.startObject(refEntityAttr.getName()).field("type", "multi_field")
									.startObject("fields").startObject(refEntityAttr.getName()).field("type", "string")
									.endObject().startObject("sort").field("type", "string")
									.field("index", "not_analyzed").endObject().endObject().endObject();
						}
						else
						{
							jsonBuilder.startObject(refEntityAttr.getName()).field("type", "string").endObject();
						}
					}
					jsonBuilder.endObject().endObject();
				}
				else
				{
					jsonBuilder.startObject(attributeMetaData.getName()).field("type", "multi_field")
							.startObject("fields").startObject(attributeMetaData.getName()).field("type", "string")
							.endObject().startObject("sort").field("type", "string").field("index", "not_analyzed")
							.endObject().endObject().endObject();
				}
			}
		}
		else
		{
			throw new RuntimeException("The repository " + PalgaSample.ENTITY_NAME + " does not exist!");
		}

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
