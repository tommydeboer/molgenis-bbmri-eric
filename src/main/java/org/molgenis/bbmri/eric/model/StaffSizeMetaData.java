package org.molgenis.bbmri.eric.model;

import static org.molgenis.MolgenisFieldTypes.INT;
import static org.molgenis.MolgenisFieldTypes.STRING;

import org.molgenis.data.EntityMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.springframework.stereotype.Component;

@Component
public class StaffSizeMetaData extends DefaultEntityMetaData
{
	public static final EntityMetaData META_DATA = new StaffSizeMetaData();
	public static final String ENTITY_NAME = "staffsize";
	public static final String FULLY_QUALIFIED_NAME = BbmriEricPackage.NAME + '_' + ENTITY_NAME;

	public static final String ID = "id";
	public static final String LABEL = "label";

	public StaffSizeMetaData()
	{
		super(ENTITY_NAME, BbmriEricPackage.getPackage());

		addAttribute(ID).setIdAttribute(true).setDataType(INT).setNillable(false).setLabel("ID").setDescription("ID");
		addAttribute(LABEL).setDataType(STRING).setNillable(false).setLabelAttribute(true).setLabel("Label")
				.setDescription("Label");
	}
}
