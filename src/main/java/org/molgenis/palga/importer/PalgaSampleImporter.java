package org.molgenis.palga.importer;

import static java.util.stream.StreamSupport.stream;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.molgenis.data.CrudRepository;
import org.molgenis.data.DataService;
import org.molgenis.data.DatabaseAction;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.elasticsearch.SearchService;
import org.molgenis.data.elasticsearch.factory.EmbeddedElasticSearchServiceFactory;
import org.molgenis.data.elasticsearch.index.EntityToSourceConverter;
import org.molgenis.data.importer.EntitiesValidationReportImpl;
import org.molgenis.data.importer.ImportService;
import org.molgenis.framework.db.EntitiesValidationReport;
import org.molgenis.framework.db.EntityImportReport;
import org.molgenis.palga.meta.AgegroupMetaData;
import org.molgenis.palga.meta.DiagnosisMetaData;
import org.molgenis.palga.meta.GenderMetaData;
import org.molgenis.palga.meta.MaterialMetaData;
import org.molgenis.palga.meta.PalgaSampleMetaData;
import org.molgenis.palga.meta.RetrievaltermMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

@Service
public class PalgaSampleImporter implements ImportService
{
	private static final Logger LOG = LoggerFactory.getLogger(PalgaSampleImporter.class);

	private static final char SEPARATOR = '|';

	private static final String EXCERPT_NUM = "PALGAexcerptnr";
	private static final String REGELNR_COLUMN = "Regelnummer";
	private static final String DIAGNOSE_COLUMN = "PALGA-code";
	private static final String RETRIEVAL_TERM_COLUMN = "Retrievalterm";
	private static final String MATERIAAL_COLUMN = "Soort onderzoek";
	private static final String JAAR_COLUMN = "Jaar onderzoek";
	private static final String GESLACHT_COLUMN = "Geslacht";
	private static final String LEEFTIJD_COLUMN = "Leeftijdscategorie";
	private static final String IN_COLUMN_SEPARATOR = "\\*";
	private static final List<String> COLUMNS = Arrays.asList(EXCERPT_NUM, REGELNR_COLUMN, DIAGNOSE_COLUMN,
			RETRIEVAL_TERM_COLUMN, MATERIAAL_COLUMN, JAAR_COLUMN, GESLACHT_COLUMN, LEEFTIJD_COLUMN);

	private static final int EXCERPT_NUM_INDEX = 0;
	private static final int REGELNR_COLUMN_INDEX = 1;
	private static final int DIAGNOSE_COLUMN_INDEX = 2;
	private static final int RETRIEVAL_TERM_COLUMN_INDEX = 3;
	private static final int MATERIAAL_COLUMN_INDEX = 4;
	private static final int JAAR_COLUMN_INDEX = 5;
	private static final int GESLACHT_COLUMN_INDEX = 6;
	private static final int LEEFTIJD_COLUMN_INDEX = 7;

	private static final int BATCH_SIZE = 50;
	private static final int CONCURRENT_REQUESTS = 4;

	private final DataService dataService;
	private final EntityToSourceConverter entityToSourceConverter;
	private final EmbeddedElasticSearchServiceFactory embeddedElasticSearchServiceFactory;
	private final SearchService elasticSearchService;

	@Autowired
	public PalgaSampleImporter(DataService dataService, EntityToSourceConverter entityToSourceConverter,
			EmbeddedElasticSearchServiceFactory embeddedElasticSearchServiceFactory, SearchService elasticSearchService)
	{
		this.dataService = dataService;
		this.entityToSourceConverter = entityToSourceConverter;
		this.embeddedElasticSearchServiceFactory = embeddedElasticSearchServiceFactory;
		this.elasticSearchService = elasticSearchService;
	}

	@Override
	public int getOrder()
	{
		return Integer.MIN_VALUE;
	}

	@Override
	public EntityImportReport doImport(RepositoryCollection source, DatabaseAction databaseAction)
	{
		LOG.info("Importing palga samples....");

		File f = ((PalgaSampleRepositoryCollection) source).getFile();
		EntityImportReport report = new EntityImportReport();

		Map<Object, Map<String, Object>> retrievalTerms = findAll(RetrievaltermMetaData.INSTANCE);
		Map<Object, Map<String, Object>> diagnosis = findAll(DiagnosisMetaData.INSTANCE);
		Map<Object, Map<String, Object>> materials = findAll(MaterialMetaData.INSTANCE);
		Map<Object, Map<String, Object>> genders = findAll(GenderMetaData.INSTANCE);
		Map<Object, Map<String, Object>> agegroups = findAll(AgegroupMetaData.INSTANCE);

		AtomicInteger count = new AtomicInteger();
		long t0 = System.currentTimeMillis();

		BulkProcessor bulkProcessor = BulkProcessor
				.builder(embeddedElasticSearchServiceFactory.getClient(), new BulkProcessor.Listener()
				{
					@Override
					public void beforeBulk(long executionId, BulkRequest request)
					{
					}

					@Override
					public void afterBulk(long executionId, BulkRequest request, BulkResponse response)
					{
						int total = count.addAndGet(request.numberOfActions());
						if (total % 10000 == 0)
						{
							LOG.info("Imported {} samples in {} sec", total, (System.currentTimeMillis() - t0) / 1000);
						}
					}

					@Override
					public void afterBulk(long executionId, BulkRequest request, Throwable failure)
					{
						LOG.error("Error executing bulk sample import", failure);
					}
				}).setConcurrentRequests(CONCURRENT_REQUESTS).setBulkActions(BATCH_SIZE)
				.setBulkSize(new ByteSizeValue(1, ByteSizeUnit.MB)).build();

		File inputFile = getUnpackedFile(f);

		try (CSVReader csvReader = new CSVReader(new FileReader(inputFile), SEPARATOR))
		{
			String[] row = csvReader.readNext();// Header
			while ((row = csvReader.readNext()) != null)
			{
				Map<String, Object> doc = new HashMap<>();
				Integer excerptNr = Integer.valueOf(row[EXCERPT_NUM_INDEX]);
				String regelNr = row[REGELNR_COLUMN_INDEX];
				String id = String.format("%d_%s", excerptNr, regelNr);

				doc.put(PalgaSampleMetaData.ATTR_ID, id);
				doc.put(PalgaSampleMetaData.ATTR_EXCERPT_NR, excerptNr);
				doc.put(PalgaSampleMetaData.ATTR_DIAGNOSIS, toDiagnosis(row[DIAGNOSE_COLUMN_INDEX], diagnosis));
				doc.put(PalgaSampleMetaData.ATTR_RETRIEVAL_TERM,
						toRetrievalTerms(row[RETRIEVAL_TERM_COLUMN_INDEX], retrievalTerms));
				doc.put(PalgaSampleMetaData.ATTR_MATERIAL, toMateriaal(row[MATERIAAL_COLUMN_INDEX], materials));
				doc.put(PalgaSampleMetaData.ATTR_GENDER, toGender(row[GESLACHT_COLUMN_INDEX], genders));
				doc.put(PalgaSampleMetaData.ATTR_AGE, toAgeGroup(row[LEEFTIJD_COLUMN_INDEX], agegroups));
				doc.put(PalgaSampleMetaData.ATTR_YEAR, toYear(row[JAAR_COLUMN_INDEX]));

				bulkProcessor.add(new IndexRequest(embeddedElasticSearchServiceFactory.getIndexName(),
						PalgaSampleMetaData.INSTANCE.getName()).source(doc));
			}
		}
		catch (IOException e)
		{
			throw new UncheckedIOException(e);
		}
		finally
		{
			try
			{
				if (!bulkProcessor.awaitClose(Long.MAX_VALUE, TimeUnit.NANOSECONDS))
				{
					LOG.warn("Not all ES bulk requests are completed");
				}
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}

			if (!inputFile.delete()) LOG.warn("Could not delete {}", inputFile.getAbsolutePath());
		}

		// Flush it, else we can get a corrupt index when we restart
		elasticSearchService.flush();

		report.addEntityCount(PalgaSampleMetaData.INSTANCE.getName(), count.get());

		// Invalidate cache
		invalidateAggregateCache();

		long t = (System.currentTimeMillis() - t0) / 1000;
		LOG.info("Imported {} samples in {} sec.", count.get(), t);

		return report;
	}

	private void invalidateAggregateCache()
	{
		CrudRepository sampleRepo = (CrudRepository) dataService.getRepositoryByEntityName(PalgaSampleMetaData.INSTANCE
				.getName());
		sampleRepo.clearCache();
		LOG.info("Invalidated aggregate cache");
	}

	// Unpacks and returns the packed file if it is a zip file. Deletes the original zip file
	private File getUnpackedFile(File sourceFile)
	{
		try
		{
			if (isZipFile(sourceFile))
			{
				ZipFile zip = new ZipFile(sourceFile);
				FileHeader fileHeader = (FileHeader) zip.getFileHeaders().get(0);
				zip.extractAll(sourceFile.getParent());
				if (!sourceFile.delete()) LOG.warn("Could not delete zip file {}", sourceFile.getAbsolutePath());

				return new File(sourceFile.getParent(), fileHeader.getFileName());
			}

			return sourceFile;
		}
		catch (ZipException e)
		{
			throw new RuntimeException(e);
		}
	}

	private boolean isZipFile(File f) throws ZipException
	{
		return new ZipFile(f).isValidZipFile();
	}

	@Override
	public EntitiesValidationReport validateImport(File file, RepositoryCollection source)
	{
		EntitiesValidationReport report = new EntitiesValidationReportImpl();

		report.getSheetsImportable().put(PalgaSampleMetaData.INSTANCE.getName(), true);
		report.getFieldsAvailable().put(PalgaSampleMetaData.INSTANCE.getName(), Collections.emptyList());
		report.getFieldsImportable().put(PalgaSampleMetaData.INSTANCE.getName(), COLUMNS);
		report.getFieldsUnknown().put(
				PalgaSampleMetaData.INSTANCE.getName(),
				stream(source.getEntityNames().spliterator(), false).filter(s -> !COLUMNS.contains(s)).collect(
						Collectors.toList()));

		return report;
	}

	@Override
	public boolean canImport(File file, RepositoryCollection source)
	{
		return getSourceRepo(source) != null;
	}

	private Repository getSourceRepo(RepositoryCollection source)
	{
		for (String name : source.getEntityNames())
		{
			Repository repo = source.getRepositoryByEntityName(name);
			List<String> attributes = stream(repo.getEntityMetaData().getAtomicAttributes().spliterator(), false).map(
					attr -> attr.getName()).collect(Collectors.toList());

			for (String col : COLUMNS)
			{
				if (!attributes.contains(col)) return null;
			}

			return repo;
		}

		return null;
	}

	private List<Map<String, Object>> toDiagnosis(String token, Map<Object, Map<String, Object>> diagnosis)
	{
		if (StringUtils.isBlank(token)) return Collections.emptyList();

		String[] diagnoseArray = token.split(IN_COLUMN_SEPARATOR);
		List<Map<String, Object>> diagnosisList = new ArrayList<>(diagnoseArray.length);
		for (String code : diagnoseArray)
		{
			if (StringUtils.isNotBlank(code))
			{
				Map<String, Object> d = diagnosis.get(code);
				if (d != null)
				{
					// note: no dedup
					diagnosisList.add(d);
				}
			}
		}

		return diagnosisList;
	}

	private List<Map<String, Object>> toRetrievalTerms(String token, Map<Object, Map<String, Object>> retrievalTerms)
	{
		if (StringUtils.isBlank(token)) return Collections.emptyList();

		String[] retrievalTermArray = token.split(IN_COLUMN_SEPARATOR);
		List<Map<String, Object>> retrievalTermList = new ArrayList<>(retrievalTermArray.length);
		for (String code : retrievalTermArray)
		{
			if (StringUtils.isNotBlank(code))
			{
				Map<String, Object> retrievalTerm = retrievalTerms.get(code);
				if (retrievalTerm != null)
				{
					// note: no dedup
					retrievalTermList.add(retrievalTerm);
				}
			}
		}

		return retrievalTermList;
	}

	private Map<String, Object> toMateriaal(String token, Map<Object, Map<String, Object>> materials)
	{
		if (StringUtils.isBlank(token)) return null;
		return materials.get(token);
	}

	private Map<String, Object> toGender(String token, Map<Object, Map<String, Object>> genders)
	{
		if (StringUtils.isBlank(token)) return null;
		return genders.get(token);
	}

	private Map<String, Object> toAgeGroup(String token, Map<Object, Map<String, Object>> agegroups)
	{
		if (StringUtils.isBlank(token)) return null;
		return agegroups.get(token);
	}

	private Integer toYear(String token)
	{
		if (StringUtils.isBlank(token)) return null;
		if (!StringUtils.isNumeric(token)) return null;
		return Integer.valueOf(token);
	}

	@Override
	public List<DatabaseAction> getSupportedDatabaseActions()
	{
		return Arrays.asList(DatabaseAction.ADD);
	}

	@Override
	public boolean getMustChangeEntityName()
	{
		return false;
	}

	private Map<Object, Map<String, Object>> findAll(EntityMetaData entityMetaData)
	{
		return stream(dataService.findAll(entityMetaData.getName()).spliterator(), false).map(
				e -> entityToSourceConverter.convert(e, entityMetaData)).collect(
				Collectors.toMap(e -> e.get(entityMetaData.getIdAttribute().getName()), e -> e));
	}

}
