package org.molgenis.palga.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.elasticsearch.common.collect.Maps;
import org.molgenis.data.DataService;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.elasticsearch.ElasticsearchRepository;
import org.molgenis.elasticsearch.ElasticSearchService;
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

	private final Map<String, String> materialMapping = Maps.newHashMap();
	private final Map<String, String> geslachtMapping = Maps.newHashMap();
	private final Map<String, String> ageGroupMapping = Maps.newHashMap();

	private final String dbUser;
	private final String dbPassword;

	@Autowired
	private DataService dataService;

	@Autowired
	private ElasticSearchService elasticSearchService;

	@Autowired
	public PalgaSampleImporter(@Value("${db_user:@null}") String dbUser,
			@Value("${db_password:@null}") String dbPassword)
	{
		materialMapping.put("C", "Cytologie");
		materialMapping.put("T", "Histologie");
		geslachtMapping.put("m", "Man");
		geslachtMapping.put("v", "Vrouw");
		ageGroupMapping.put("<18", "0-18");
		ageGroupMapping.put("18-50", "18-50");
		ageGroupMapping.put(">50", "50+");
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
	}

	@Async
	@RunAsSystem
	public void importFile(final File file) throws InvalidFormatException, IOException
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

		final Map<String, RetrievalTerm> retrievalTerms = getRetrievalTerms(entityManager);
		final Map<String, Diagnosis> diagnosis = getDiagnosis(entityManager);
		final Map<String, Material> materials = getMaterials(entityManager);
		final Map<String, Gender> genders = getGenders(entityManager);
		final Map<String, Agegroup> agegroups = getAgeGroups(entityManager);

		elasticSearchService.createMappings(entityMetaData, false, false, false);

		ElasticsearchRepository elasticsearchRepository = new ElasticsearchRepository(entityMetaData,
				elasticSearchService);

		try
		{
			long t0 = System.currentTimeMillis();
			Integer count = elasticsearchRepository.add(new Iterable<PalgaSample>()
			{
				@Override
				public Iterator<PalgaSample> iterator()
				{
					try
					{
						return new PalgaSampleIterator(file, diagnosis, retrievalTerms, materials, materialMapping,
								agegroups, ageGroupMapping, genders, geslachtMapping);
					}
					catch (IOException e)
					{
						throw new RuntimeException(e);
					}
				}
			});
			long t = (System.currentTimeMillis() - t0) / 1000;
			logger.info("Import of palga sample file [" + fileName + "] completed in " + t + " sec. Added [" + count
					+ "] rows.");
		}
		catch (Throwable t)
		{
			logger.error("Exception importing palga sample file [" + fileName + "] ", t);
			entityManager.getTransaction().rollback();
		}

		dataService.addRepository(elasticsearchRepository);

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

	private static class PalgaSampleIterator implements Iterator<PalgaSample>
	{
		private final Map<String, Diagnosis> diagnosis;
		private final Map<String, RetrievalTerm> retrievalTerms;
		private final Map<String, Material> materials;
		private final Map<String, String> materialMapping;
		private final Map<String, Agegroup> agegroups;
		private final Map<String, String> ageGroupMapping;
		private final Map<String, Gender> genders;
		private final Map<String, String> geslachtMapping;
		private final CSVReader reader;
		private String[] tokens;
		private int row;
		private int id = 0;

		public PalgaSampleIterator(File file, Map<String, Diagnosis> diagnosis,
				Map<String, RetrievalTerm> retrievalTerms, Map<String, Material> materials,
				Map<String, String> materialMapping, Map<String, Agegroup> agegroups,
				Map<String, String> ageGroupMapping, Map<String, Gender> genders, Map<String, String> geslachtMapping)
				throws IOException
		{
			this.diagnosis = diagnosis;
			this.retrievalTerms = retrievalTerms;
			this.materials = materials;
			this.materialMapping = materialMapping;
			this.agegroups = agegroups;
			this.ageGroupMapping = ageGroupMapping;
			this.genders = genders;
			this.geslachtMapping = geslachtMapping;
			reader = new CSVReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
					SEPARATOR);
			reader.readNext();// header
			row = 1;
		}

		@Override
		public boolean hasNext()
		{
			if (tokens == null)
			{
				try
				{
					tokens = reader.readNext();
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
				if (tokens == null) IOUtils.closeQuietly(reader);
				++row;
			}
			return tokens != null;
		}

		@Override
		public PalgaSample next()
		{
			if (tokens == null) throw new RuntimeException("Iterator does not have any more items");
			PalgaSample palgaSample = new PalgaSample();
			palgaSample.setId(id++);
			palgaSample.setExcerptNr(Integer.valueOf(tokens[EXCERPT_NUM]));
			palgaSample.setDiagnose(toDiagnosis(tokens[DIAGNOSE_COLUMN], diagnosis, row));
			palgaSample.setRetrievalTerm(toRetrievalTerm(tokens[RETRIEVAL_TERM_COLUMN], retrievalTerms, row));
			palgaSample.setMateriaal(toMateriaal(tokens[MATERIAAL_COLUMN], materialMapping, materials, row));
			palgaSample.setGeslacht(toGender(tokens[GESLACHT_COLUMN], geslachtMapping, genders, row));
			palgaSample.setLeeftijd(toAgeGroup(tokens[LEEFTIJD_COLUMN], ageGroupMapping, agegroups, row));
			palgaSample.setJaar(toYear(tokens[JAAR_COLUMN], row));
			tokens = null;
			return palgaSample;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		private List<Diagnosis> toDiagnosis(String token, Map<String, Diagnosis> diagnosis, int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("Palga-code column of row [" + row + "] is empty");
			}
			String[] diagnoseArray = token.split(IN_COLUMN_SEPARATOR);
			List<Diagnosis> diagnosisList = new ArrayList<Diagnosis>(diagnoseArray.length);
			for (String code : diagnoseArray)
			{
				if (StringUtils.isNotBlank(code))
				{
					Diagnosis d = diagnosis.get(code);
					if (d != null)
					{
						// note: no dedup
						diagnosisList.add(d);
					}
				}
			}
			return diagnosisList;
		}

		private List<RetrievalTerm> toRetrievalTerm(String token, Map<String, RetrievalTerm> retrievalTerms, int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("Palga-code column of row [" + row + "] is empty");
			}
			String[] retrievalTermArray = token.split(IN_COLUMN_SEPARATOR);
			List<RetrievalTerm> retrievalTermList = new ArrayList<RetrievalTerm>(retrievalTermArray.length);
			for (String code : retrievalTermArray)
			{
				if (StringUtils.isNotBlank(code))
				{
					RetrievalTerm retrievalTerm = retrievalTerms.get(code);
					if (retrievalTerm != null)
					{
						// note: no dedup
						retrievalTermList.add(retrievalTerm);
					}
				}
			}
			return retrievalTermList;
		}

		private Material toMateriaal(String token, Map<String, String> materialMapping,
				Map<String, Material> materials, int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("'Soort onderzoek' column of row [" + row + "] is empty");
				return null;
			}
			String mappedToken = materialMapping.get(token);
			if (mappedToken == null)
			{
				logger.warn("Unknown material [" + token + "] on row [" + row + "]");
				return null;
			}
			Material material = materials.get(mappedToken);
			if (material == null)
			{
				logger.warn("Unknown material [" + mappedToken + "] on row [" + row + "]");
				return null;
			}
			return material;
		}

		private Gender toGender(String token, Map<String, String> geslachtMapping, Map<String, Gender> genders, int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("Geslacht column of row [" + row + "] is empty");
				return null;
			}
			String mappedToken = geslachtMapping.get(token);
			if (mappedToken == null)
			{
				logger.warn("Unknown gender [" + token + "] on row [" + row + "]");
				return null;
			}
			Gender gender = genders.get(mappedToken);
			if (gender == null)
			{
				logger.warn("Unkown gender [" + mappedToken + "] on row [" + row + "]");
				return null;
			}
			return gender;
		}

		private Agegroup toAgeGroup(String token, Map<String, String> ageGroupMapping, Map<String, Agegroup> agegroups,
				int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("Leeftijdscategorie column of row [" + row + "] is empty");
				return null;
			}
			String mappedToken = ageGroupMapping.get(token);
			if (mappedToken == null)
			{
				logger.warn("Unknown gender [" + token + "] on row [" + row + "]");
				return null;
			}
			Agegroup agegroup = agegroups.get(mappedToken);
			if (agegroup == null)
			{
				logger.warn("Unknown agegroup [" + agegroup + "] on row [" + row + "]");
				return null;
			}
			return agegroup;
		}

		private Integer toYear(String token, int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("'Jaar onderzoek' column of row [" + row + "] is empty");
			}
			if (!StringUtils.isNumeric(token))
			{
				logger.warn("Invalid year [" + token + "] on row [" + row + "]");
				return null;
			}
			return Integer.valueOf(token);
		}
	}
}
