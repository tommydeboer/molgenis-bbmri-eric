package org.molgenis.palga.importer;

import static org.molgenis.palga.importer.PalgaPluginController.URI;

import org.apache.commons.lang3.StringUtils;
import org.molgenis.data.DataService;
import org.molgenis.framework.ui.MolgenisPluginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(URI)
public class PalgaPluginController extends MolgenisPluginController
{
	public static final String ID = "palga";
	public static final String URI = MolgenisPluginController.PLUGIN_URI_PREFIX + ID;
	private final DataService dataService;

	@Autowired
	public PalgaPluginController(DataService dataService)
	{
		super(URI);
		this.dataService = dataService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showForm()
	{
		return "view-palga";
	}

	@RequestMapping(value = "/delete/{entity}", method = RequestMethod.GET)
	public String importPalgaSampleFile(@PathVariable("entity") String entity, Model model)
	{
		if (StringUtils.isNotBlank(entity) && dataService.hasRepository(entity))
		{
			dataService.deleteAll(entity);
		}

		return showForm();
	}
}
