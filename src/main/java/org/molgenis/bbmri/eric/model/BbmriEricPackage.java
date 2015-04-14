package org.molgenis.bbmri.eric.model;

import org.molgenis.data.Package;
import org.molgenis.data.meta.PackageImpl;

public class BbmriEricPackage
{

	private static Package BBMRI_ERIC_PACKAGE;
	public static final String NAME = "bbmri-eric";
	public static final String DESCRIPTION = "Contains the BBMRI-ERIC catalogue model.";

	private BbmriEricPackage()
	{

	}

	public static Package getPackage()
	{
		if (BBMRI_ERIC_PACKAGE == null)
		{
			BBMRI_ERIC_PACKAGE = new PackageImpl(NAME, DESCRIPTION);
		}
		return BBMRI_ERIC_PACKAGE;
	}
}
