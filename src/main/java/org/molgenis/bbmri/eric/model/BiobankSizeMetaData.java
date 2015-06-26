package org.molgenis.bbmri.eric.model;

import static org.molgenis.MolgenisFieldTypes.INT;
import static org.molgenis.MolgenisFieldTypes.STRING;

import org.molgenis.data.EntityMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.springframework.stereotype.Component;

@Component
public class BiobankSizeMetaData extends DefaultEntityMetaData
{
	public static final EntityMetaData META_DATA = new BiobankSizeMetaData();
	public static final String ENTITY_NAME = "biobanksize";
	public static final String FULLY_QUALIFIED_NAME = BbmriEricPackage.NAME + '_' + ENTITY_NAME;

	public static final String ID = "id";
	public static final String LABEL = "label";

	public BiobankSizeMetaData()
	{
		super(ENTITY_NAME, BbmriEricPackage.getPackage());

		setLabel("Biobank size");
		setDescription("Size of the biobank collection");

		addAttribute(ID).setIdAttribute(true).setDataType(INT).setNillable(false).setLabel("ID").setDescription("ID");
		addAttribute(LABEL).setDataType(STRING).setNillable(false).setLabelAttribute(true).setLabel("Label")
				.setDescription("Label");
	}

}
