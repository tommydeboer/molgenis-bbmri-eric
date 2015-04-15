package org.molgenis.bbmri.eric.service;

import java.util.List;
import java.util.Map;

public class BbmriEricDataResponse
{
	private List<Map<String, Object>> biobanks;

	public BbmriEricDataResponse()
	{

	}

	public BbmriEricDataResponse(List<Map<String, Object>> biobanks)
	{
		this.biobanks = biobanks;
	}

	public List<Map<String, Object>> getBiobanks()
	{
		return biobanks;
	}

	public void setBiobanks(List<Map<String, Object>> biobanks)
	{
		this.biobanks = biobanks;
	}

}
