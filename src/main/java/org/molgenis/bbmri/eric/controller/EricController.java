package org.molgenis.bbmri.eric.controller;

import static org.molgenis.bbmri.eric.controller.EricController.BASE_URI;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.MolgenisFieldTypes;
import org.molgenis.bbmri.eric.model.DirectoryMetaData;
import org.molgenis.bbmri.eric.service.BbmriEricDataResponse;
import org.molgenis.bbmri.eric.service.EricDownloadService;
import org.molgenis.bbmri.eric.service.NlToEricConverter;
import org.molgenis.data.AttributeMetaData;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.security.core.runas.RunAsSystemProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author tommy
 *
 */
@Controller
@RequestMapping(BASE_URI)
public class EricController
{
	public static final String BASE_URI = "/bbmri";
	private NlToEricConverter nlToEricConverter;
	private EricDownloadService ericDownloadService;

	// MIME type for LDIF
	public static final String APPLICATION_DIRECTORY_VALUE = "application/directory";

	private final DataService dataService;

	@Autowired
	public EricController(DataService dataService, NlToEricConverter nlToEricConverter,
			EricDownloadService ericDownloadService)
	{
		if (dataService == null) throw new IllegalArgumentException("dataService is null");
		if (nlToEricConverter == null) throw new IllegalArgumentException("nlToEricConverter is null");

		this.dataService = dataService;
		this.nlToEricConverter = nlToEricConverter;
		this.ericDownloadService = ericDownloadService;
	}

	@RequestMapping(value = "/all", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public BbmriEricDataResponse retrieveAllNodes()
	{
		return getEricData(new QueryImpl());
	}

	@RequestMapping(value = "/{node}", method = GET, produces = APPLICATION_JSON_VALUE)
	@ResponseBody
	public BbmriEricDataResponse retrieveBbmriDataAsJson(@PathVariable("node") String node)
	{
		Query q = new QueryImpl().eq("biobankCountry", node.toUpperCase());

		return getEricData(q);
	}

	public BbmriEricDataResponse getEricData(Query q)
	{
		Iterable<Entity> it = RunAsSystemProxy.runAsSystem(() -> dataService.findAll(
				DirectoryMetaData.FULLY_QUALIFIED_NAME, q));

		List<Map<String, Object>> entities = new ArrayList<>();
		for (Entity entity : it)
		{
			Map<String, Object> entityMap = new LinkedHashMap<>();

			for (AttributeMetaData attr : entity.getEntityMetaData().getAttributes())
			{

				if (attr.getDataType().equals(MolgenisFieldTypes.COMPOUND))
				{
					Map<String, Object> compoundMap = new LinkedHashMap<>();
					for (AttributeMetaData innerAttr : attr.getAttributeParts())
					{
						if (innerAttr.getDataType().equals(MolgenisFieldTypes.CATEGORICAL))
						{
							compoundMap.put(innerAttr.getName(), entity.getEntity(innerAttr.getName()).getIdValue());
						}
						else
						{
							compoundMap.put(innerAttr.getName(), entity.getString(innerAttr.getName()));
						}
					}
					entityMap.put(attr.getName(), compoundMap);
				}
				else
				{
					if (attr.getDataType().equals(MolgenisFieldTypes.CATEGORICAL))
					{
						entityMap.put(attr.getName(), entity.getEntity(attr.getName()).getIdValue());
					}
					else
					{
						entityMap.put(attr.getName(), entity.getString(attr.getName()));
					}
				}
			}

			entities.add(entityMap);
		}

		return new BbmriEricDataResponse(entities);
	}

	@RequestMapping(value = "/convert", method = GET)
	@ResponseBody
	public void convert()
	{
		nlToEricConverter.convertNlToEric();
	}
}
