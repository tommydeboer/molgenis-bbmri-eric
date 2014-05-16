package org.molgenis.palga.importer;

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
import org.elasticsearch.common.collect.Maps;
import org.molgenis.data.DataService;
import org.molgenis.data.elasticsearch.ElasticSearchRepository;
import org.molgenis.palga.Agegroup;
import org.molgenis.palga.Diagnosis;
import org.molgenis.palga.Gender;
import org.molgenis.palga.Material;
import org.molgenis.palga.PalgaSample;
import org.molgenis.palga.RetrievalTerm;
import org.molgenis.search.SearchService;
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

	private final DataService dataService;
	private final SearchService searchService;

	@Autowired
	public PalgaSampleImporter(@Value("${db_user:@null}") String dbUser,
			@Value("${db_password:@null}") String dbPassword, DataService dataService, SearchService searchService)
	{
		materialMapping.put("C", "Cytologie");
		materialMapping.put("T", "Histologie");
		geslachtMapping.put("m", "Man");
		geslachtMapping.put("v", "Vrouw");

		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.dataService = dataService;
		this.searchService = searchService;
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

		CSVReader reader = new CSVReader(new FileReader(file), SEPARATOR);
		reader.readNext();// header

		long t0 = System.currentTimeMillis();
		long count = 0;
		long row = 2;
		try
		{
			long start = 0;
			String[] line = reader.readNext();
			while (line != null)
			{
				if ((count % BATCH_SIZE == 0) && !entityManager.getTransaction().isActive())
				{
					start = count;
					entityManager.getTransaction().begin();
				}

				PalgaSample sample = createPalgaSample(line, row++, diagnosis, retrievalTerms, materials, genders,
						agegroups);
				if (sample != null)
				{
					entityManager.persist(sample);
					count++;
				}

				// Commit if BATCH_SIZE is reached
				if (count == (start + BATCH_SIZE))
				{
					entityManager.getTransaction().commit();

					long t = (System.currentTimeMillis() - t0) / 1000;
					logger.info("Imported [" + count + "] rows in [" + t + "] sec.");
				}
				line = reader.readNext();
			}

			// Commit the rest
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().commit();
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
			entityManager.close();
		}

		// Index the samples
		long t = System.currentTimeMillis();

		ElasticSearchRepository esRepository = (ElasticSearchRepository) dataService
				.getRepositoryByEntityName(PalgaSample.ENTITY_NAME);
		searchService.indexRepository(esRepository.getRepository());

		long t1 = (System.currentTimeMillis() - t) / 1000;
		logger.info("Palga samples indexed in [" + t1 + "] sec");

		long tTotal = (System.currentTimeMillis() - t0) / 1000;
		logger.info("Import finished in [" + tTotal + "] sec");
	}

	private PalgaSample createPalgaSample(String[] csvRow, long row, Map<String, Diagnosis> diagnosis,
			Map<String, RetrievalTerm> retrievalTerms, Map<String, Material> materials, Map<String, Gender> genders,
			Map<String, Agegroup> agegroups)
	{
		PalgaSample sample = new PalgaSample();

		// Diagnosis
		String diagnose = csvRow[DIAGNOSE_COLUMN];
		if (StringUtils.isBlank(diagnose))
		{
			logger.warn("Palga-code column of row [" + row + "] is empty");
			return null;
		}
		String[] diagnoseArray = diagnose.split(IN_COLUMN_SEPARATOR);
		Set<Diagnosis> diagnoses = Sets.newHashSetWithExpectedSize(diagnoseArray.length);
		for (String code : diagnoseArray)
		{
			Diagnosis d = diagnosis.get(code);
			if (d != null)
			{
				diagnoses.add(d);
			}
		}
		if (!diagnoses.isEmpty())
		{
			sample.getDiagnose().addAll(diagnoses);
		}
		else
		{
			logger.warn("Missing PALGA codes on row [" + row + "]");
		}

		// RetrievalTerms
		String termIdentifiers = csvRow[RETRIEVAL_TERM_COLUMN];
		{
			for (String termIdentifier : termIdentifiers.split(IN_COLUMN_SEPARATOR))
			{
				if (StringUtils.isNotBlank(termIdentifier))
				{
					RetrievalTerm term = retrievalTerms.get(termIdentifier);
					if (term == null)
					{
						logger.warn("Unknown Retrievalterm [" + termIdentifier + "] on row [" + row + "]");
						return null;
					}
					sample.getRetrievalTerm().add(term);
				}
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
			sample.setMateriaal(material);
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
		sample.setJaar(Integer.valueOf(year));

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
			sample.setGeslacht(gender);
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
		sample.setLeeftijd(agegroup);

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
}
