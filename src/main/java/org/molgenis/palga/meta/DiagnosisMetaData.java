package org.molgenis.palga.meta;

import org.molgenis.data.support.DefaultEntityMetaData;

public class DiagnosisMetaData extends DefaultEntityMetaData
{
	public static final DiagnosisMetaData INSTANCE = new DiagnosisMetaData();
	public static final String ATTR_DEPALCE = "DEPALCE";
	public static final String ATTR_DETEROM = "DETEROM";

	private DiagnosisMetaData()
	{
		super("DRTPWRK");
		setLabel("Zoekterm");
		addAttribute(ATTR_DEPALCE).setIdAttribute(true).setNillable(false).setLookupAttribute(true).setLabel("Code");
		addAttribute(ATTR_DETEROM).setNillable(false).setLookupAttribute(true).setLabel("Omschrijving")
				.setLabelAttribute(true);
	}

}
