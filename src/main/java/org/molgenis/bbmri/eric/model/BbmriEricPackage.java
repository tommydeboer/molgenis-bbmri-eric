package org.molgenis.bbmri.eric.model;

import org.molgenis.data.Package;
import org.molgenis.data.meta.PackageImpl;

public class BbmriEricPackage
{

	private static Package BBMRI_ERIC_PACKAGE;

	private BbmriEricPackage()
	{

	}

	public static Package getPackage()
	{
		if (BBMRI_ERIC_PACKAGE == null)
		{
			BBMRI_ERIC_PACKAGE = new PackageImpl("bbmri-eric", "Contains the BBMRI-ERIC catalogue model.");
		}
		return BBMRI_ERIC_PACKAGE;
	}
}
