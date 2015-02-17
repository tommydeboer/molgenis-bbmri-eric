package org.molgenis.palga.meta;

import org.molgenis.data.support.DefaultEntityMetaData;

public class AgegroupMetaData extends DefaultEntityMetaData
{
	public static final AgegroupMetaData INSTANCE = new AgegroupMetaData();
	public static final String ATTR_ID = "id";
	public static final String ATTR_AGEGROUP = "agegroup";

	private AgegroupMetaData()
	{
		super("Agegroup");
		setLabel("Leeftijdscategorie");
		addAttribute(ATTR_ID).setIdAttribute(true).setNillable(false).setVisible(false);
		addAttribute(ATTR_AGEGROUP).setUnique(true).setNillable(false).setLookupAttribute(true).setLabelAttribute(true);
	}

}
