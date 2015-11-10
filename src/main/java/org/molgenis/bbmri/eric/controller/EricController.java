package org.molgenis.bbmri.eric.controller;

import static org.molgenis.bbmri.eric.controller.EricController.BASE_URI;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.molgenis.bbmri.eric.service.NlToEricConverter;
import org.molgenis.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(BASE_URI)
public class EricController
{
	public static final String BASE_URI = "/bbmri";
	private NlToEricConverter nlToEricConverter;

	// MIME type for LDIF
	public static final String APPLICATION_DIRECTORY_VALUE = "application/directory";

	private final DataService dataService;

	@Autowired
	public EricController(DataService dataService, NlToEricConverter nlToEricConverter)
	{
		if (dataService == null) throw new IllegalArgumentException("dataService is null");
		if (nlToEricConverter == null) throw new IllegalArgumentException("nlToEricConverter is null");

		this.dataService = dataService;
		this.nlToEricConverter = nlToEricConverter;
	}

	// @RequestMapping(value = "/all", method = GET, produces = APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public BbmriEricDataResponse retrieveAllNodes()
	// {
	// return getEricData(new QueryImpl());
	// }
	//
	// @RequestMapping(value = "/{node}", method = GET, produces = APPLICATION_JSON_VALUE)
	// @ResponseBody
	// public BbmriEricDataResponse retrieveBbmriDataAsJson(@PathVariable("node") String node)
	// {
	// Query q = new QueryImpl().eq(DirectoryMetaData.BIOBANK_COUNTRY, node.toUpperCase());
	//
	// return getEricData(q);
	// }
	//
	// public BbmriEricDataResponse getEricData(Query q)
	// {
	// Iterable<Entity> it = RunAsSystemProxy
	// .runAsSystem(() -> dataService.findAll(DirectoryMetaData.FULLY_QUALIFIED_NAME, q));
	//
	// List<Map<String, Object>> entities = new ArrayList<>();
	// for (Entity entity : it)
	// {
	// Map<String, Object> entityMap = new LinkedHashMap<>();
	//
	// for (AttributeMetaData attr : entity.getEntityMetaData().getAttributes())
	// {
	//
	// if (attr.getDataType().equals(MolgenisFieldTypes.COMPOUND))
	// {
	// Map<String, Object> compoundMap = new LinkedHashMap<>();
	// for (AttributeMetaData innerAttr : attr.getAttributeParts())
	// {
	// if (innerAttr.getDataType().equals(MolgenisFieldTypes.CATEGORICAL))
	// {
	// compoundMap.put(innerAttr.getName(), entity.getEntity(innerAttr.getName()).getIdValue());
	// }
	// else
	// {
	// compoundMap.put(innerAttr.getName(), entity.getString(innerAttr.getName()));
	// }
	// }
	// entityMap.put(attr.getName(), compoundMap);
	// }
	// else
	// {
	// if (attr.getDataType().equals(MolgenisFieldTypes.CATEGORICAL))
	// {
	// entityMap.put(attr.getName(), entity.getEntity(attr.getName()).getIdValue());
	// }
	// else
	// {
	// entityMap.put(attr.getName(), entity.getString(attr.getName()));
	// }
	// }
	// }
	//
	// entities.add(entityMap);
	// }
	//
	// return new BbmriEricDataResponse(entities);
	// }

	@RequestMapping(value = "/convert", method = GET)
	@ResponseBody
	public void convert()
	{
		nlToEricConverter.convertNlToEric();
	}
}
