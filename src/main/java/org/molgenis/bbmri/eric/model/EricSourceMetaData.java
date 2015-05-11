package org.molgenis.bbmri.eric.model;

import static org.molgenis.MolgenisFieldTypes.HYPERLINK;
import static org.molgenis.MolgenisFieldTypes.STRING;

import org.molgenis.data.support.DefaultEntityMetaData;
import org.springframework.stereotype.Component;

/**
 * 
 * @author tommy
 *
 */
@Component
public class EricSourceMetaData extends DefaultEntityMetaData
{
	public static final String ENTITY_NAME = "EricSource";

	public static final String ID = "id";
	public static final String SOURCE = "source";
	public static final String FULLY_QUALIFIED_NAME = BbmriEricPackage.NAME + '_' + ENTITY_NAME;

	public EricSourceMetaData()
	{
		super(ENTITY_NAME, BbmriEricPackage.getPackage());

		addAttribute(ID).setDataType(STRING).setNillable(false).setIdAttribute(true);
		addAttribute(SOURCE).setDataType(HYPERLINK).setNillable(false);
	}
}
