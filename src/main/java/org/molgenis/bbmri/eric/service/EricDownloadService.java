package org.molgenis.bbmri.eric.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.molgenis.bbmri.eric.model.BbmriEricPackage;
import org.molgenis.bbmri.eric.model.CatalogueMetaData;
import org.molgenis.bbmri.eric.model.EricSourceMetaData;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.support.DefaultEntity;
import org.molgenis.security.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.google.gson.Gson;

/**
 * Service that downloads JSON objects from ERIC sources every midnight. The biobanks are added to the BBMRI ERIC
 * catalogue.
 * 
 * @author tommy
 *
 */
@Service
public class EricDownloadService
{
	private static final Logger LOG = LoggerFactory.getLogger(EricDownloadService.class);

	private static final String ERIC_SOURCE_ENTITY = BbmriEricPackage.NAME + '_' + EricSourceMetaData.ENTITY_NAME;

	private DataService dataService;
	private Gson gson;

	@Autowired
	public EricDownloadService(DataService dataService)
	{
		this.dataService = dataService;
		this.gson = new Gson();
	}

	@Scheduled(cron = "0 0 0 * * *")
	@RunAsSystem
	@Transactional
	public void downloadSources()
	{
		Iterable<Entity> it = dataService.findAll(ERIC_SOURCE_ENTITY);

		if (!it.iterator().hasNext())
		{
			LOG.info("No ERIC sources to download.");
			return;
		}
		else
		{
			LOG.info("Starting biobanks import of external ERIC sources.");
		}

		int adds = 0;
		int updates = 0;
		int sources = 0;
		for (Entity source : it)
		{
			try
			{
				LOG.info(String.format("Importing ERIC biobanks from %s", source.get(EricSourceMetaData.SOURCE)
						.toString()));

				OutputStream out = new ByteArrayOutputStream();
				URL request = new URL(source.get(EricSourceMetaData.SOURCE).toString());
				FileCopyUtils.copy(request.openStream(), out);
				BbmriEricDataResponse bedr = gson.fromJson(out.toString(), BbmriEricDataResponse.class);

				for (Map<String, Object> biobank : bedr.getBiobanks())
				{
					DefaultEntity ericBiobank = new DefaultEntity(
							dataService.getEntityMetaData(CatalogueMetaData.FULLY_QUALIFIED_NAME), dataService);
					for (Entry<String, Object> entry : biobank.entrySet())
					{
						ericBiobank.set(entry.getKey(), entry.getValue());
					}

					// add/update
					if (dataService.findOne(CatalogueMetaData.FULLY_QUALIFIED_NAME, ericBiobank.getIdValue()) == null)
					{
						dataService.add(CatalogueMetaData.FULLY_QUALIFIED_NAME, ericBiobank);
						adds++;
					}
					else
					{
						dataService.update(CatalogueMetaData.FULLY_QUALIFIED_NAME, ericBiobank);
						updates++;
					}
				}
				sources++;
			}
			catch (Exception e)
			{
				LOG.warn(String.format("Couldn't parse JSON from %s - Check if the URL or JSON is valid.",
						source.get(EricSourceMetaData.SOURCE)));
				LOG.warn(e.toString());
			}

		}

		LOG.info(String.format("Imported %s source(s). Added %s biobank(s). Updated %s biobank(s).",
				Integer.toString(sources), Integer.toString(adds), Integer.toString(updates)));
	}
}
