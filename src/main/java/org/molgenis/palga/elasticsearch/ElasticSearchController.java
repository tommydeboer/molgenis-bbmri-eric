package org.molgenis.palga.elasticsearch;

import static org.molgenis.palga.elasticsearch.ElasticSearchController.URI;

import org.molgenis.data.DataService;
import org.molgenis.data.Repository;
import org.molgenis.data.elasticsearch.ElasticSearchRepository;
import org.molgenis.elasticsearch.ElasticSearchService;
import org.molgenis.framework.ui.MolgenisPluginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(URI)
public class ElasticSearchController extends MolgenisPluginController
{
	public static final String ID = "elasticsearch";
	public static final String URI = MolgenisPluginController.PLUGIN_URI_PREFIX + ID;

	private final ElasticSearchService elasticSearchService;
	private final DataService dataService;

	@Autowired
	public ElasticSearchController(ElasticSearchService elasticSearchService, DataService dataService)
	{
		super(URI);
		if (elasticSearchService == null) throw new IllegalArgumentException("elasticSearchService is null");
		if (dataService == null) throw new IllegalArgumentException("dataService is null");
		this.elasticSearchService = elasticSearchService;
		this.dataService = dataService;
	}

	@RequestMapping("/index")
	@ResponseStatus(HttpStatus.OK)
	public void index(@RequestParam("entity") String entityName)
	{
		Repository repository = dataService.getRepositoryByEntityName(entityName);
		if (repository instanceof ElasticSearchRepository)
		{
			repository = ((ElasticSearchRepository) repository).getRepository();
		}
		elasticSearchService.indexRepository(repository);
	}
}
