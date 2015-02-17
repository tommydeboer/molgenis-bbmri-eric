package org.molgenis.palga.meta;

import org.molgenis.data.support.DefaultEntityMetaData;

public class RetrievaltermMetaData extends DefaultEntityMetaData
{
	public static final RetrievaltermMetaData INSTANCE = new RetrievaltermMetaData();
	public static final String ATTR_ID = "Termnr";
	public static final String ATTR_DESCRIPTION = "Retrievalterm";

	public RetrievaltermMetaData()
	{
		super("Retrievalterm");
		setLabel("Retrievalterm");
		addAttribute(ATTR_ID).setIdAttribute(true).setNillable(false).setLookupAttribute(true);
		addAttribute(ATTR_DESCRIPTION).setLookupAttribute(true).setNillable(false).setLabelAttribute(true);
	}

}
