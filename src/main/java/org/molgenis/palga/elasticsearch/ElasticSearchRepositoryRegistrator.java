package org.molgenis.palga.elasticsearch;

import org.molgenis.data.DataService;
import org.molgenis.data.Repository;
import org.molgenis.data.elasticsearch.ElasticSearchRepository;
import org.molgenis.elasticsearch.ElasticSearchService;
import org.molgenis.palga.PalgaSample;
import org.molgenis.search.SearchService;
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
		Repository repository = dataService.getRepositoryByEntityName(PalgaSample.ENTITY_NAME);
		dataService.removeRepository(PalgaSample.ENTITY_NAME);
		dataService.addRepository(new ElasticSearchRepository((ElasticSearchService) elasticSearchService, repository));
	}
}
