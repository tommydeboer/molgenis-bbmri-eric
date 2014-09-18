package org.molgenis.palga.importer;

import static org.molgenis.palga.importer.PalgaImporterController.URI;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.framework.ui.MolgenisPluginController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(URI)
public class PalgaImporterController extends MolgenisPluginController
{
	public static final String ID = "palgaimport";
	public static final String URI = MolgenisPluginController.PLUGIN_URI_PREFIX + ID;
	private final PalgaSampleImporter palgaSampleImporter;

	@Autowired
	public PalgaImporterController(PalgaSampleImporter palgaSampleImporter)
	{
		super(URI);
		this.palgaSampleImporter = palgaSampleImporter;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showImportForm(Model model)
	{
		return "view-palga-import";
	}

	@RequestMapping(value = "/palgasample", method = RequestMethod.POST)
	public String importPalgaSampleFile(@RequestParam("fileLocation") String fileLocation, Model model)
			throws InvalidFormatException, IOException
	{
		if (fileLocation == null)
		{
			model.addAttribute("errorMessage", "Missing palga file location");
		}
		else
		{
			File f = new File(fileLocation);
			String filePath = f.getAbsolutePath();
			if (!f.exists())
			{
				model.addAttribute("errorMessage", "File " + filePath + " does not exists");
			}
			else if (!StringUtils.getFilenameExtension(filePath).equalsIgnoreCase("csv")
					&& !StringUtils.getFilenameExtension(filePath).equalsIgnoreCase("psv"))
			{
				model.addAttribute("errorMessage", "Please select a csv file");
			}
			else
			{
				palgaSampleImporter.importFile(f);
				model.addAttribute("infoMessage", "Palga import started");
			}
		}

		return showImportForm(model);
	}
}
