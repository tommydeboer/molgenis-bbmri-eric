package org.molgenis.palga.elasticsearch;

import static org.molgenis.palga.importer.PalgaSampleImporter.ENTITY_NAME_PALGA_SAMPLE;

import org.apache.log4j.Logger;
import org.molgenis.data.DataService;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryDecoratorFactory;
import org.molgenis.data.elasticsearch.ElasticsearchRepository;
import org.molgenis.data.elasticsearch.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchRepositoryRegistrator implements ApplicationListener<ContextRefreshedEvent>, Ordered
{
	private static final Logger LOG = Logger.getLogger(ElasticSearchRepositoryRegistrator.class);

	private final DataService dataService;
	private final SearchService elasticSearchService;
	// temporary workaround for module dependencies
	private final RepositoryDecoratorFactory repositoryDecoratorFactory;

	@Autowired
	public ElasticSearchRepositoryRegistrator(DataService dataService, SearchService elasticSearchService,
			RepositoryDecoratorFactory repositoryDecoratorFactory)
	{
		if (dataService == null) throw new IllegalArgumentException("dataService is null");
		if (elasticSearchService == null) throw new IllegalArgumentException("elasticSearchService is null");
		if (repositoryDecoratorFactory == null) throw new IllegalArgumentException("repositoryDecoratorFactory is null");
		this.dataService = dataService;
		this.elasticSearchService = elasticSearchService;
		this.repositoryDecoratorFactory = repositoryDecoratorFactory;
	}

	@Override
	public int getOrder()
	{
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		if (dataService.hasRepository(ENTITY_NAME_PALGA_SAMPLE))
		{
			// workaround for palga sample entities
			Repository repository = dataService.getRepositoryByEntityName(ENTITY_NAME_PALGA_SAMPLE);
			registerElasticSearchRepository(repository);
		}
	}

	public void registerElasticSearchRepository(Repository repository)
	{
		LOG.info("Registering PalgaSample repository ...");
		dataService.removeRepository(ENTITY_NAME_PALGA_SAMPLE);
		// TODO: fix this more elegantly
		dataService.addRepository(repositoryDecoratorFactory
				.createDecoratedRepository(new ElasticsearchCacheRepositoryDecorator(new ElasticsearchRepository(
						repository.getEntityMetaData(), elasticSearchService))));
		LOG.info("Registered PalgaSample repository");
	}
}
