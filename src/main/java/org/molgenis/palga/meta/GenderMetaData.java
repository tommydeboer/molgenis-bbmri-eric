package org.molgenis.palga.meta;

import org.molgenis.data.support.DefaultEntityMetaData;

public class GenderMetaData extends DefaultEntityMetaData
{
	public static final GenderMetaData INSTANCE = new GenderMetaData();
	public static final String ATTR_ID = "id";
	public static final String ATTR_GENDER = "gender";

	private GenderMetaData()
	{
		super("Gender");
		setLabel("Geslacht");
		addAttribute(ATTR_ID).setIdAttribute(true).setNillable(false).setVisible(false);
		addAttribute(ATTR_GENDER).setUnique(true).setNillable(false).setLookupAttribute(true).setLabelAttribute(true);
	}
}
