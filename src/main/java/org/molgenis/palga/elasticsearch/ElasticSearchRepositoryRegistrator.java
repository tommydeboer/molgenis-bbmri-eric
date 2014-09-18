package org.molgenis.palga.elasticsearch;

import static org.molgenis.palga.importer.PalgaSampleImporter.ENTITY_NAME_PALGA_SAMPLE;

import org.molgenis.data.DataService;
import org.molgenis.data.Repository;
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
	private final DataService dataService;
	private final SearchService elasticSearchService;

	@Autowired
	public ElasticSearchRepositoryRegistrator(DataService dataService, SearchService elasticSearchService)
	{
		if (dataService == null) throw new IllegalArgumentException("dataService is null");
		if (elasticSearchService == null) throw new IllegalArgumentException("elasticSearchService is null");
		this.dataService = dataService;
		this.elasticSearchService = elasticSearchService;
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
			dataService.removeRepository(ENTITY_NAME_PALGA_SAMPLE);
			dataService.addRepository(new ElasticsearchRepository(repository.getEntityMetaData(),
					(org.molgenis.data.elasticsearch.SearchService) elasticSearchService));
		}
	}
}
