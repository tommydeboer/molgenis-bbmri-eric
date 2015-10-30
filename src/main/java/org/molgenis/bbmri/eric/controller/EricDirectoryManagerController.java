package org.molgenis.bbmri.eric.controller;

import static org.molgenis.bbmri.eric.controller.EricDirectoryManagerController.URI;

import org.molgenis.bbmri.eric.service.EricDownloadService;
import org.molgenis.bbmri.eric.service.EricDownloadService.DownloadReport;
import org.molgenis.ui.MolgenisPluginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(URI)
public class EricDirectoryManagerController extends MolgenisPluginController
{
	private static final String ID = "ericdirectorymgr";
	public static final String URI = MolgenisPluginController.PLUGIN_URI_PREFIX + ID;

	@Autowired
	private EricDownloadService ericDownloadService;

	public EricDirectoryManagerController()
	{
		super(URI);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String init()
	{
		return "view-ericdirectorymgr";
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	@ResponseBody
	public DownloadReport download()
	{
		return ericDownloadService.downloadSourcesOnDemand();
	}
}
