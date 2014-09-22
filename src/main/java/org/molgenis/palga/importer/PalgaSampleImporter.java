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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.elasticsearch.common.collect.Maps;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.elasticsearch.ElasticsearchRepository;
import org.molgenis.data.elasticsearch.SearchService;
import org.molgenis.data.support.MapEntity;
import org.molgenis.security.runas.RunAsSystem;
import org.springframework.beans.factory.annotation.Autowired;
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

	public static final String ENTITY_NAME_PALGA_SAMPLE = "PalgaSample";
	public static final String ENTITY_NAME_AGE_GROUP = "AgeGroup";
	public static final String ENTITY_NAME_RETRIEVAL_TERM = "RetrievalTerm";
	public static final String ENTITY_NAME_GENDER = "Gender";
	public static final String ENTITY_NAME_DIAGNOSIS = "Diagnosis";
	public static final String ENTITY_NAME_MATERIAL = "Material";

	private static final String ATTR_ID = "id";
	public static final String ATTR_EXCERPT_NR = "excerptNr";
	private static final String ATTR_DIAGNOSE = "diagnose";
	private static final String ATTR_RETRIEVAL_TERM = "retrievalTerm";
	private static final String ATTR_MATERIAAL = "materiaal";
	private static final String ATTR_GESLACHT = "geslacht";
	private static final String ATTR_LEEFTIJD = "leeftijd";
	private static final String ATTR_JAAR = "jaar";

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

	@Autowired
	private DataService dataService;

	@Autowired
	private SearchService elasticSearchService;

	public PalgaSampleImporter()
	{
		materialMapping.put("C", "Cytologie");
		materialMapping.put("T", "Histologie");
		geslachtMapping.put("m", "Man");
		geslachtMapping.put("v", "Vrouw");
		ageGroupMapping.put("<18", "0-18");
		ageGroupMapping.put("18-50", "18-50");
		ageGroupMapping.put(">50", "50+");
	}

	@Async
	@RunAsSystem
	public void importFile(final File file) throws InvalidFormatException, IOException
	{
		if (!dataService.hasRepository(ENTITY_NAME_PALGA_SAMPLE))
		{
			throw new RuntimeException("The repository " + ENTITY_NAME_PALGA_SAMPLE + " does not exist!");
		}

		EntityMetaData entityMetaData = dataService.getEntityMetaData(ENTITY_NAME_PALGA_SAMPLE);

		String fileName = file.getAbsolutePath();
		logger.info("Import of palga sample file [" + fileName + "] started");

		final Map<String, Entity> retrievalTerms = getRetrievalTerms();
		final Map<String, Entity> diagnosis = getDiagnosis();
		final Map<String, Entity> materials = getMaterials();
		final Map<String, Entity> genders = getGenders();
		final Map<String, Entity> agegroups = getAgeGroups();

		elasticSearchService.delete(entityMetaData);
		// for performance reasons: do not store palga sample entities in index, only store them
		elasticSearchService.createMappings(entityMetaData, false, false, false);

		ElasticsearchRepository elasticsearchRepository = new ElasticsearchRepository(entityMetaData,
				elasticSearchService);

		try
		{
			long t0 = System.currentTimeMillis();
			Integer count = elasticsearchRepository.add(new Iterable<Entity>()
			{
				@Override
				public Iterator<Entity> iterator()
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
		}

		if (dataService.hasRepository(ENTITY_NAME_PALGA_SAMPLE))
		{
			dataService.removeRepository(ENTITY_NAME_PALGA_SAMPLE);
		}

		dataService.addRepository(elasticsearchRepository);

	}

	private Map<String, Entity> getRetrievalTerms()
	{
		Map<String, Entity> entityMap = Maps.newHashMap();
		for (Entity entity : dataService.findAll(ENTITY_NAME_RETRIEVAL_TERM))
		{
			entityMap.put(entity.getIdValue().toString(), entity);
		}
		return entityMap;
	}

	private Map<String, Entity> getDiagnosis()
	{
		Map<String, Entity> entityMap = Maps.newHashMap();
		for (Entity entity : dataService.findAll(ENTITY_NAME_DIAGNOSIS))
		{
			entityMap.put(entity.getIdValue().toString(), entity);
		}
		return entityMap;
	}

	private Map<String, Entity> getMaterials()
	{
		Map<String, Entity> entityMap = Maps.newHashMap();
		for (Entity entity : dataService.findAll(ENTITY_NAME_MATERIAL))
		{
			entityMap.put(entity.getIdValue().toString(), entity);
		}
		return entityMap;
	}

	private Map<String, Entity> getGenders()
	{
		Map<String, Entity> entityMap = Maps.newHashMap();
		for (Entity entity : dataService.findAll(ENTITY_NAME_GENDER))
		{
			entityMap.put(entity.getIdValue().toString(), entity);
		}
		return entityMap;
	}

	private Map<String, Entity> getAgeGroups()
	{
		Map<String, Entity> entityMap = Maps.newHashMap();
		for (Entity entity : dataService.findAll(ENTITY_NAME_AGE_GROUP))
		{
			entityMap.put(entity.getIdValue().toString(), entity);
		}
		return entityMap;
	}

	private static class PalgaSampleIterator implements Iterator<Entity>
	{
		private final Map<String, Entity> diagnosis;
		private final Map<String, Entity> retrievalTerms;
		private final Map<String, Entity> materials;
		private final Map<String, String> materialMapping;
		private final Map<String, Entity> agegroups;
		private final Map<String, String> ageGroupMapping;
		private final Map<String, Entity> genders;
		private final Map<String, String> geslachtMapping;
		private final CSVReader reader;
		private String[] tokens;
		private int row;
		private int id = 0;

		public PalgaSampleIterator(File file, Map<String, Entity> diagnosis, Map<String, Entity> retrievalTerms,
				Map<String, Entity> materials, Map<String, String> materialMapping, Map<String, Entity> agegroups,
				Map<String, String> ageGroupMapping, Map<String, Entity> genders, Map<String, String> geslachtMapping)
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
		public Entity next()
		{
			if (tokens == null) throw new RuntimeException("Iterator does not have any more items");
			MapEntity palgaSample = new MapEntity();
			palgaSample.set(ATTR_ID, id++);
			palgaSample.set(ATTR_EXCERPT_NR, Integer.valueOf(tokens[EXCERPT_NUM]));
			palgaSample.set(ATTR_DIAGNOSE, toDiagnosis(tokens[DIAGNOSE_COLUMN], diagnosis, row));
			palgaSample.set(ATTR_RETRIEVAL_TERM, toRetrievalTerm(tokens[RETRIEVAL_TERM_COLUMN], retrievalTerms, row));
			palgaSample.set(ATTR_MATERIAAL, toMateriaal(tokens[MATERIAAL_COLUMN], materialMapping, materials, row));
			palgaSample.set(ATTR_GESLACHT, toGender(tokens[GESLACHT_COLUMN], geslachtMapping, genders, row));
			palgaSample.set(ATTR_LEEFTIJD, toAgeGroup(tokens[LEEFTIJD_COLUMN], ageGroupMapping, agegroups, row));
			palgaSample.set(ATTR_JAAR, toYear(tokens[JAAR_COLUMN], row));
			tokens = null;
			return palgaSample;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		private List<Entity> toDiagnosis(String token, Map<String, Entity> diagnosis, int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("Palga-code column of row [" + row + "] is empty");
			}
			String[] diagnoseArray = token.split(IN_COLUMN_SEPARATOR);
			List<Entity> diagnosisList = new ArrayList<Entity>(diagnoseArray.length);
			for (String code : diagnoseArray)
			{
				if (StringUtils.isNotBlank(code))
				{
					Entity d = diagnosis.get(code);
					if (d != null)
					{
						// note: no dedup
						diagnosisList.add(d);
					}
				}
			}
			return diagnosisList;
		}

		private List<Entity> toRetrievalTerm(String token, Map<String, Entity> retrievalTerms, int row)
		{
			if (StringUtils.isBlank(token))
			{
				logger.warn("Palga-code column of row [" + row + "] is empty");
			}
			String[] retrievalTermArray = token.split(IN_COLUMN_SEPARATOR);
			List<Entity> retrievalTermList = new ArrayList<Entity>(retrievalTermArray.length);
			for (String code : retrievalTermArray)
			{
				if (StringUtils.isNotBlank(code))
				{
					Entity retrievalTerm = retrievalTerms.get(code);
					if (retrievalTerm != null)
					{
						// note: no dedup
						retrievalTermList.add(retrievalTerm);
					}
				}
			}
			return retrievalTermList;
		}

		private Entity toMateriaal(String token, Map<String, String> materialMapping, Map<String, Entity> materials,
				int row)
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
			Entity material = materials.get(mappedToken);
			if (material == null)
			{
				logger.warn("Unknown material [" + mappedToken + "] on row [" + row + "]");
				return null;
			}
			return material;
		}

		private Entity toGender(String token, Map<String, String> geslachtMapping, Map<String, Entity> genders, int row)
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
			Entity gender = genders.get(mappedToken);
			if (gender == null)
			{
				logger.warn("Unkown gender [" + mappedToken + "] on row [" + row + "]");
				return null;
			}
			return gender;
		}

		private Entity toAgeGroup(String token, Map<String, String> ageGroupMapping, Map<String, Entity> agegroups,
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
			Entity agegroup = agegroups.get(mappedToken);
			if (agegroup == null)
			{
				logger.warn("Unknown agegroup [" + mappedToken + "] on row [" + row + "]");
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
