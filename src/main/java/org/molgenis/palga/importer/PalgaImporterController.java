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
	private final ThesaurusImporter thesaurusImporter;
	private final PalgaSampleImporter palgaSampleImporter;

	@Autowired
	public PalgaImporterController(ThesaurusImporter thesaurusImporter, PalgaSampleImporter palgaSampleImporter)
	{
		super(URI);
		this.thesaurusImporter = thesaurusImporter;
		this.palgaSampleImporter = palgaSampleImporter;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showImportForm()
	{
		return "view-palga-import";
	}

	@RequestMapping("/thesaurus")
	public String importThesaurus(@RequestParam("fileLocation") String fileLocation, Model model)
			throws InvalidFormatException, IOException
	{
		if (fileLocation == null)
		{
			model.addAttribute("errorMessage", "Missing thesaurus file location");
		}
		else
		{
			File f = new File(fileLocation);
			String filePath = f.getAbsolutePath();
			if (!f.exists())
			{
				model.addAttribute("errorMessage", "File " + filePath + " does not exists");
			}
			else if (!StringUtils.getFilenameExtension(filePath).equalsIgnoreCase("xls")
					&& !StringUtils.getFilenameExtension(filePath).equalsIgnoreCase("xlsx"))
			{
				model.addAttribute("Please select an excel file");
			}
			else
			{
				thesaurusImporter.importFile(f);
				model.addAttribute("infoMessage", "Thesaurus import started");
			}
		}

		return "view-palga-import";
	}

	@RequestMapping("/palgasample")
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
			else if (!StringUtils.getFilenameExtension(filePath).equalsIgnoreCase("csv"))
			{
				model.addAttribute("Please select a csv file");
			}
			else
			{
				palgaSampleImporter.importFile(f);
				model.addAttribute("infoMessage", "Palga import started");
			}
		}

		return "view-palga-import";
	}
}
