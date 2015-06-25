package org.molgenis.bbmri.eric.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.molgenis.bbmri.eric.model.DirectoryMetaData;
import org.molgenis.bbmri.eric.model.EricSourceMetaData;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.DefaultEntity;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.security.core.runas.RunAsSystem;
import org.molgenis.security.core.runas.RunAsSystemProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

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
		downloadSources();
	}

	@RunAsSystem
	@Transactional
	public DownloadReport downloadSourcesOnDemand()
	{
		DownloadReport downloadReport = new DownloadReport();

		Iterable<Entity> it = dataService.findAll(EricSourceMetaData.FULLY_QUALIFIED_NAME);

		if (!it.iterator().hasNext())
		{
			LOG.info("No ERIC sources to download.");
			return downloadReport;
		}
		else
		{
			LOG.info("Starting biobanks import of external ERIC sources.");
		}

		int sources = 0;
		for (Entity source : it)
		{
			DownloadSourceReport downloadSourceReport = new DownloadSourceReport();
			String sourceUrl = source.get(EricSourceMetaData.SOURCE).toString();
			downloadSourceReport.setSource(sourceUrl);

			try (OutputStream out = new ByteArrayOutputStream();)
			{
				LOG.info(String.format("Importing ERIC biobanks from %s", sourceUrl));

				// get the JSON from this source
				URL request = new URL(sourceUrl);
				FileCopyUtils.copy(request.openStream(), out);
				BbmriEricDataResponse bedr = gson.fromJson(out.toString(), BbmriEricDataResponse.class);

				// we want to delete all old data of each node so find all nodes present in the JSON
				Set<String> nodes = new HashSet<>();
				if (bedr.getBiobanks().iterator().hasNext())
				{
					for (Map<String, Object> entry : bedr.getBiobanks())
					{
						nodes.add(entry.get(DirectoryMetaData.BIOBANK_COUNTRY).toString());
					}
				}

				LOG.info(String.format("Deleting old biobanks for nodes: %s", nodes.toString()));
				// delete entities for each node
				for (String node : nodes)
				{
					Query q = new QueryImpl().eq("biobankCountry", node.toUpperCase());
					Iterable<Entity> entitiesToDelete = RunAsSystemProxy.runAsSystem(() -> dataService.findAll(
							DirectoryMetaData.FULLY_QUALIFIED_NAME, q));

					dataService.delete(DirectoryMetaData.FULLY_QUALIFIED_NAME, entitiesToDelete);
				}

				// add new catalogue entities
				List<Entity> biobanksToAdd = new ArrayList<>();
				for (Map<String, Object> biobank : bedr.getBiobanks())
				{
					DefaultEntity ericBiobank = new DefaultEntity(
							dataService.getEntityMetaData(DirectoryMetaData.FULLY_QUALIFIED_NAME), dataService);
					for (Entry<String, Object> entry : biobank.entrySet())
					{
						if (entry.getValue() instanceof Map)
						{
							@SuppressWarnings("unchecked")
							LinkedTreeMap<String, Object> compoundEntries = (LinkedTreeMap<String, Object>) entry
									.getValue();

							for (Entry<String, Object> compoundEntry : compoundEntries.entrySet())
							{
								String key = compoundEntry.getKey();
								if (key.equals(DirectoryMetaData.BIOBANK_IT_STAFF_SIZE)
										|| key.equals(DirectoryMetaData.BIOBANK_SIZE))
								{
									ericBiobank.set(key, new Double((double) compoundEntry.getValue()).intValue());
								}
								else
								{
									ericBiobank.set(key, compoundEntry.getValue());
								}
							}
						}
						else
						{

							String key = entry.getKey();
							if (key.equals(DirectoryMetaData.BIOBANK_IT_STAFF_SIZE)
									|| key.equals(DirectoryMetaData.BIOBANK_SIZE))
							{
								ericBiobank.set(key, new Double((double) entry.getValue()).intValue());
							}
							else
							{
								ericBiobank.set(key, entry.getValue());
							}
						}

					}
					biobanksToAdd.add(ericBiobank);
				}
				dataService.add(DirectoryMetaData.FULLY_QUALIFIED_NAME, biobanksToAdd);
				sources++;

				downloadSourceReport.setStatus(DownloadSourceReport.Status.SUCCESS);
			}
			catch (Throwable t)
			{
				LOG.warn(String.format("Couldn't import JSON from %s ", source.get(EricSourceMetaData.SOURCE)));
				LOG.warn(t.toString());
				downloadSourceReport.setStatus(DownloadSourceReport.Status.ERROR);
			}
			downloadReport.addSourceReport(downloadSourceReport);
		}

		LOG.info(String.format("Imported %d source(s).", sources));
		return downloadReport;
	}

	public static class DownloadSourceReport
	{
		public enum Status
		{
			UNKNOWN, SUCCESS, ERROR
		}

		private String source;
		private Status status;
		private String message;

		public DownloadSourceReport()
		{
			this.status = Status.UNKNOWN;
		}

		public String getSource()
		{
			return source;
		}

		public void setSource(String source)
		{
			this.source = source;
		}

		public Status getStatus()
		{
			return status;
		}

		public void setStatus(Status status)
		{
			this.status = status;
		}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}

	public static class DownloadReport
	{
		private List<DownloadSourceReport> reports;

		public DownloadReport()
		{
			this.reports = new ArrayList<DownloadSourceReport>();
		}

		private void addSourceReport(DownloadSourceReport sourceReport)
		{
			this.reports.add(sourceReport);
		}

		public List<DownloadSourceReport> getReports()
		{
			return reports;
		}

		public void setReports(List<DownloadSourceReport> reports)
		{
			this.reports = reports;
		}
	}
}
