package org.molgenis.palga.meta;

import org.molgenis.data.support.DefaultEntityMetaData;

public class MaterialMetaData extends DefaultEntityMetaData
{
	public static final MaterialMetaData INSTANCE = new MaterialMetaData();
	public static final String ATTR_ID = "id";
	public static final String ATTR_TYPE = "type";

	private MaterialMetaData()
	{
		super("Material");
		setLabel("Materiaal");
		addAttribute(ATTR_ID).setIdAttribute(true).setNillable(false).setVisible(false);
		addAttribute(ATTR_TYPE).setUnique(true).setNillable(false).setLookupAttribute(true).setLabelAttribute(true);
	}
}