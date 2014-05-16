package org.molgenis.palga.importer;

import static org.molgenis.palga.importer.PalgaImporterController.URI;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.DataService;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.framework.ui.MolgenisPluginController;
import org.molgenis.palga.Diagnosis;
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
	private final RetrievalTermImporter retrievalTermImporter;
	private final DataService dataService;

	@Autowired
	public PalgaImporterController(ThesaurusImporter thesaurusImporter, PalgaSampleImporter palgaSampleImporter,
			RetrievalTermImporter retrievalTermImporter, DataService dataService)
	{
		super(URI);
		this.thesaurusImporter = thesaurusImporter;
		this.palgaSampleImporter = palgaSampleImporter;
		this.retrievalTermImporter = retrievalTermImporter;
		this.dataService = dataService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showImportForm(Model model)
	{
		// Only show thesuaus if not imported yet
		long diagnosisCount = dataService.count(Diagnosis.ENTITY_NAME, new QueryImpl());
		model.addAttribute("showThesurus", (diagnosisCount == 0));
		return "view-palga-import";
	}

	@RequestMapping(value = "/thesaurus", method = RequestMethod.POST)
	public String importThesaurus(@RequestParam("fileLocation") String fileLocation, Model model)
			throws InvalidFormatException, IOException
	{
		boolean showThesurus = true;
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
			else if (!StringUtils.getFilenameExtension(filePath).equalsIgnoreCase("xlsx"))
			{
				model.addAttribute("errorMessage", "Please select an xlsx file");
			}
			else
			{
				thesaurusImporter.importFile(f);
				model.addAttribute("infoMessage", "Thesaurus import started");
				showThesurus = false;
			}
		}

		model.addAttribute("showThesurus", showThesurus);
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

	@RequestMapping(value = "/retrievalterms", method = RequestMethod.POST)
	public String importRetrievalItemFile(@RequestParam("fileLocation") String fileLocation, Model model)
			throws InvalidFormatException, IOException
	{
		if (fileLocation == null)
		{
			model.addAttribute("errorMessage", "Missing retrieval file location");
		}
		else
		{
			File f = new File(fileLocation);
			String filePath = f.getAbsolutePath();
			if (!f.exists())
			{
				model.addAttribute("errorMessage", "File " + filePath + " does not exists");
			}
			else if (!StringUtils.getFilenameExtension(filePath).equalsIgnoreCase("xlsx"))
			{
				model.addAttribute("errorMessage", "Please select an xlsx file");
			}
			else
			{
				retrievalTermImporter.importFile(f);
				model.addAttribute("infoMessage", "Retrieval import started");
			}
		}

		return showImportForm(model);
	}
}
