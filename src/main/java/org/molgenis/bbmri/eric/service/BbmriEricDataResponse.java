package org.molgenis.bbmri.eric.service;

import java.util.List;
import java.util.Map;

public class BbmriEricDataResponse
{
	private final List<Map<String, Object>> biobanks;

	public BbmriEricDataResponse(List<Map<String, Object>> biobanks)
	{
		this.biobanks = biobanks;
	}

}
