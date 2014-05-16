package org.molgenis.palga.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.elasticsearch.common.collect.Maps;
import org.molgenis.data.Entity;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.csv.CsvRepositoryCollection;
import org.molgenis.palga.Agegroup;
import org.molgenis.palga.Diagnosis;
import org.molgenis.palga.Gender;
import org.molgenis.palga.Material;
import org.molgenis.palga.PalgaSample;
import org.molgenis.palga.RetrievalTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
	private static final String DIAGNOSE_COLUMN = "PALGA-code";
	private static final String RETRIEVAL_TERM_COLUMN = "Retrievalterm";
	private static final String MATERIAAL_COLUMN = "Soort onderzoek";
	private static final String JAAR_COLUMN = "Jaar onderzoek";
	private static final String GESLACHT_COLUMN = "Geslacht";
	private static final String LEEFTIJD_COLUMN = "Leeftijdscategorie";
	private static final String IN_COLUMN_SEPARATOR = "\\*";
	private static int BATCH_SIZE = 100000;

	private final Map<String, String> materialMapping = Maps.newHashMap();
	private final Map<String, String> geslachtMapping = Maps.newHashMap();

	private final String dbUser;
	private final String dbPassword;

	@Autowired
	public PalgaSampleImporter(@Value("${db_user:@null}") String dbUser,
			@Value("${db_password:@null}") String dbPassword)
	{
		// TODO
		materialMapping.put("T", "Cytologie");
		materialMapping.put("M", "Histologie");
		geslachtMapping.put("m", "Man");
		geslachtMapping.put("v", "Vrouw");

		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

	public static void main(String[] args)
	{
		File f = new File(args[0]);
		String dbUser = args[1];
		String dbPassword = args[2];

		try
		{
			new PalgaSampleImporter(dbUser, dbPassword).importFile(f);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Async
	public void importFile(File file) throws InvalidFormatException, IOException
	{
		Map<String, String> props = Maps.newHashMap();
		props.put("javax.persistence.jdbc.user", dbUser);
		props.put("javax.persistence.jdbc.password", dbPassword);

		EntityManager entityManager = Persistence.createEntityManagerFactory("palga-import", props)
				.createEntityManager();

		String fileName = file.getAbsolutePath();
		logger.info("Going to import palga sample file [" + fileName + "]");

		RepositoryCollection source = new CsvRepositoryCollection(file);
		String repoName = source.getEntityNames().iterator().next();
		Repository sampleRepo = source.getRepositoryByEntityName(repoName);

		Map<String, RetrievalTerm> retrievalTerms = getRetrievalTerms(entityManager);
		Map<String, Diagnosis> diagnosis = getDiagnosis(entityManager);
		Map<String, Material> materials = getMaterials(entityManager);
		Map<String, Gender> genders = getGenders(entityManager);
		Map<String, Agegroup> agegroups = getAgeGroups(entityManager);

		long t0 = System.currentTimeMillis();
		long row = 0;
		try
		{
			long start = 0;
			for (Entity entity : sampleRepo)
			{
				if (row % BATCH_SIZE == 0)
				{
					start = row;
					entityManager.getTransaction().begin();
				}

				PalgaSample sample = createPalgaSample(entity, row, diagnosis, retrievalTerms, materials, genders,
						agegroups);
				if (sample != null)
				{
					entityManager.persist(sample);
					row++;
				}

				// Commit if BATCH_SIZE is reached
				if (row == (start + BATCH_SIZE))
				{
					entityManager.getTransaction().commit();
					entityManager.flush();
					entityManager.clear();
					long t = System.currentTimeMillis() - t0;

					logger.info("Inserted [" + row + "] rows in [" + t + "] msec.");
				}
			}

			// Commit the rest
			if (entityManager.getTransaction().isActive())
			{
				entityManager.getTransaction().commit();
			}

			long t = System.currentTimeMillis() - t0;
			logger.info("Import of palga sample file [" + fileName + "] completed in " + t + " msec. Added [" + row
					+ "] rows.");

		}
		catch (Exception e)
		{
			logger.error("Exception importing palga sample file [" + fileName + "] ", e);
			entityManager.getTransaction().rollback();
		}
		finally
		{
			IOUtils.closeQuietly(sampleRepo);
			entityManager.close();
		}
	}

	private PalgaSample createPalgaSample(Entity csvRow, long row, Map<String, Diagnosis> diagnosis,
			Map<String, RetrievalTerm> retrievalTerms, Map<String, Material> materials, Map<String, Gender> genders,
			Map<String, Agegroup> agegroups)
	{
		PalgaSample sample = new PalgaSample();

		// Diagnosis
		String diagnose = csvRow.getString(DIAGNOSE_COLUMN);
		if (StringUtils.isBlank(diagnose))
		{
			logger.warn(DIAGNOSE_COLUMN + " column of row [" + row + "] is empty");
			return null;
		}
		for (String code : diagnose.split(IN_COLUMN_SEPARATOR))
		{
			Diagnosis d = diagnosis.get(code);
			if (d == null)
			{
				logger.warn("Unknown PALGA-code [" + code + "] on row [" + row + "]");
				return null;
			}
			sample.getDiagnose().add(d);
		}
		if (sample.getDiagnose().isEmpty())
		{
			logger.warn("Missing PALGA-code on row [" + row + "]");
			return null;
		}

		// RetrievalTerms
		String termIdentifiers = csvRow.getString(RETRIEVAL_TERM_COLUMN);
		if (StringUtils.isBlank(termIdentifiers))
		{
			logger.warn(RETRIEVAL_TERM_COLUMN + " column of row [" + row + "] is empty");
			return null;
		}
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
		if (sample.getRetrievalTerm().isEmpty())
		{
			logger.warn("Missing Retrievalterm on row [" + row + "]");
			return null;
		}

		// Materials
		String materialTypeCode = csvRow.getString(MATERIAAL_COLUMN);
		if (StringUtils.isBlank(materialTypeCode))
		{
			logger.warn(MATERIAAL_COLUMN + " column of row [" + row + "] is empty");
			return null;
		}
		String materialType = materialMapping.get(materialTypeCode);
		if (materialType == null)
		{
			logger.warn("Unknown material [" + materialTypeCode + "] on row [" + row + "]");
			return null;
		}
		sample.setMateriaal(materials.get(materialType));

		// Year
		String year = csvRow.getString(JAAR_COLUMN);
		if (StringUtils.isBlank(year))
		{
			logger.warn(JAAR_COLUMN + " column of row [" + row + "] is empty");
		}
		if (!StringUtils.isNumeric(year))
		{
			logger.warn("Invalid year [" + year + "] on row [" + row + "]");
			return null;
		}
		sample.setJaar(Integer.valueOf(year));

		// Gender
		String genderCode = csvRow.getString(GESLACHT_COLUMN);
		if (StringUtils.isBlank(genderCode))
		{
			logger.warn(GESLACHT_COLUMN + " column of row [" + row + "] is empty");
			return null;
		}
		String gender = geslachtMapping.get(genderCode);
		if (gender == null)
		{
			logger.warn("Unknown gender [" + genderCode + "] on row [" + row + "]");
			return null;
		}
		sample.setGeslacht(genders.get(gender));

		// Agegroups
		String agegroupCode = csvRow.getString(LEEFTIJD_COLUMN);
		if (StringUtils.isBlank(agegroupCode))
		{
			logger.warn(LEEFTIJD_COLUMN + " column of row [" + row + "] is empty");
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
