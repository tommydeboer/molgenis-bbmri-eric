package org.molgenis.palga.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.FileRepositoryCollectionFactory;
import org.molgenis.data.RepositoryDecoratorFactory;
import org.molgenis.data.csv.CsvRepositoryCollection;
import org.molgenis.data.elasticsearch.ElasticsearchRepository;
import org.molgenis.data.elasticsearch.SearchService;
import org.molgenis.data.importer.ImportServiceFactory;
import org.molgenis.data.mysql.MysqlRepositoryCollection;
import org.molgenis.data.support.MapEntity;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.palga.importer.PalgaSampleImporter;
import org.molgenis.palga.importer.PalgaSampleRepositoryCollection;
import org.molgenis.palga.importer.ThesaurusImporter;
import org.molgenis.palga.meta.AgegroupMetaData;
import org.molgenis.palga.meta.DiagnosisMetaData;
import org.molgenis.palga.meta.GenderMetaData;
import org.molgenis.palga.meta.MaterialMetaData;
import org.molgenis.palga.meta.PalgaSampleMetaData;
import org.molgenis.palga.meta.RetrievaltermMetaData;
import org.molgenis.security.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchRepositoryRegistrator implements ApplicationListener<ContextRefreshedEvent>, Ordered
{
	private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchRepositoryRegistrator.class);
	private static final List<? extends EntityMetaData> ENTITIES = Arrays.asList(AgegroupMetaData.INSTANCE,
			DiagnosisMetaData.INSTANCE, GenderMetaData.INSTANCE, MaterialMetaData.INSTANCE,
			RetrievaltermMetaData.INSTANCE, PalgaSampleMetaData.INSTANCE);

	private final DataService dataService;
	private final SearchService elasticSearchService;
	private final RepositoryDecoratorFactory repositoryDecoratorFactory;
	private final ImportServiceFactory importServiceFactory;
	private final ThesaurusImporter thesaurusImporter;
	// private final RetrievalTermImporter retrievalTermImporter;
	private final PalgaSampleImporter palgaSampleImporter;
	private final FileRepositoryCollectionFactory fileRepositorySourceFactory;
	private final MysqlRepositoryCollection mysqlRepositoryCollection;

	@Autowired
	public ElasticSearchRepositoryRegistrator(DataService dataService, SearchService elasticSearchService,
			RepositoryDecoratorFactory repositoryDecoratorFactory, ImportServiceFactory importServiceFactory,
			ThesaurusImporter thesaurusImporter, PalgaSampleImporter palgaSampleImporter,
			FileRepositoryCollectionFactory fileRepositorySourceFactory,
			MysqlRepositoryCollection mysqlRepositoryCollection)
	{
		this.dataService = dataService;
		this.elasticSearchService = elasticSearchService;
		this.repositoryDecoratorFactory = repositoryDecoratorFactory;
		this.thesaurusImporter = thesaurusImporter;
		this.importServiceFactory = importServiceFactory;
		// this.retrievalTermImporter = retrievalTermImporter;
		this.palgaSampleImporter = palgaSampleImporter;
		this.fileRepositorySourceFactory = fileRepositorySourceFactory;
		this.mysqlRepositoryCollection = mysqlRepositoryCollection;
	}

	@Override
	public int getOrder()
	{
		return Ordered.LOWEST_PRECEDENCE;
	}

	@RunAsSystem
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		// Register repositories
		ENTITIES.forEach(this::registerMetaData);

		// Register palga sample repo collection
		fileRepositorySourceFactory.addFileRepositoryCollectionClass(PalgaSampleRepositoryCollection.class,
				CsvRepositoryCollection.EXTENSIONS);

		// Register importers
		importServiceFactory.addImportService(thesaurusImporter);
		LOG.info("Registered ThesaurusImporter");

		importServiceFactory.addImportService(palgaSampleImporter);
		LOG.info("Registered PalgaSampleImporter");

		// Add some needed entities
		addLookupEntities();
		LOG.info("Added lookup entities");
	}

	private void addLookupEntities()
	{
		// Materials
		if (dataService.count(MaterialMetaData.INSTANCE.getName(), new QueryImpl()) == 0)
		{
			Entity cytologie = new MapEntity(MaterialMetaData.ATTR_ID);
			cytologie.set(MaterialMetaData.ATTR_ID, "C");
			cytologie.set(MaterialMetaData.ATTR_TYPE, "Cytologie");

			Entity histologie = new MapEntity(MaterialMetaData.ATTR_ID);
			histologie.set(MaterialMetaData.ATTR_ID, "T");
			histologie.set(MaterialMetaData.ATTR_TYPE, "Histologie");

			dataService.add(MaterialMetaData.INSTANCE.getName(), Arrays.asList(cytologie, histologie));
		}

		// Genders
		if (dataService.count(GenderMetaData.INSTANCE.getName(), new QueryImpl()) == 0)
		{
			Entity man = new MapEntity(GenderMetaData.ATTR_ID);
			man.set(GenderMetaData.ATTR_ID, "m");
			man.set(GenderMetaData.ATTR_GENDER, "Man");

			Entity vrouw = new MapEntity(GenderMetaData.ATTR_ID);
			vrouw.set(GenderMetaData.ATTR_ID, "v");
			vrouw.set(GenderMetaData.ATTR_GENDER, "Vrouw");

			dataService.add(GenderMetaData.INSTANCE.getName(), Arrays.asList(man, vrouw));
		}

		// Agegroups
		if (dataService.count(AgegroupMetaData.INSTANCE.getName(), new QueryImpl()) == 0)
		{
			Entity gr1 = new MapEntity(AgegroupMetaData.ATTR_ID);
			gr1.set(AgegroupMetaData.ATTR_ID, "<18");
			gr1.set(AgegroupMetaData.ATTR_AGEGROUP, "0-18");

			Entity gr2 = new MapEntity(AgegroupMetaData.ATTR_ID);
			gr2.set(AgegroupMetaData.ATTR_ID, "18-50");
			gr2.set(AgegroupMetaData.ATTR_AGEGROUP, "18-50");

			Entity gr3 = new MapEntity(AgegroupMetaData.ATTR_ID);
			gr3.set(AgegroupMetaData.ATTR_ID, ">50");
			gr3.set(AgegroupMetaData.ATTR_AGEGROUP, "50+");

			dataService.add(AgegroupMetaData.INSTANCE.getName(), Arrays.asList(gr1, gr2, gr3));
		}
	}

	private void registerMetaData(EntityMetaData entityMetaData)
	{
		// Thesaurus and sample data are stored in ES only, the rest in Mysql repository.
		if (entityMetaData == PalgaSampleMetaData.INSTANCE)
		{
			if (!elasticSearchService.hasMapping(entityMetaData)) // createEsMappings(entityMetaData);
			{
				try
				{
					elasticSearchService.createMappings(entityMetaData, false, false, false, true);
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}

			if (!dataService.hasRepository(entityMetaData.getName()))
			{
				dataService.addRepository(repositoryDecoratorFactory
						.createDecoratedRepository(new ElasticsearchCacheRepositoryDecorator(
								new ElasticsearchRepository(entityMetaData, elasticSearchService))));
			}
		}
		else if (entityMetaData == DiagnosisMetaData.INSTANCE)
		{
			if (!elasticSearchService.hasMapping(entityMetaData)) createEsMappings(entityMetaData);
			if (!dataService.hasRepository(entityMetaData.getName()))
			{
				dataService.addRepository(repositoryDecoratorFactory
						.createDecoratedRepository(new ElasticsearchRepository(entityMetaData, elasticSearchService)));
			}
		}
		else
		{
			mysqlRepositoryCollection.add(entityMetaData);
		}

		LOG.info("Registered '{}' repository.", entityMetaData.getName());
	}

	private void createEsMappings(EntityMetaData entityMetaData)
	{
		try
		{
			elasticSearchService.createMappings(entityMetaData, true, true, false, true);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
