package org.molgenis.bbmri.eric;

import java.util.Map;
import java.util.Map.Entry;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.auth.UserAuthority;
import org.molgenis.bbmri.eric.controller.HomeController;
import org.molgenis.bbmri.eric.model.BbmriEricPackage;
import org.molgenis.bbmri.eric.model.BiobankSizeMetaData;
import org.molgenis.bbmri.eric.model.StaffSizeMetaData;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.support.DefaultEntity;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.framework.db.WebAppDatabasePopulatorService;
import org.molgenis.security.MolgenisSecurityWebAppDatabasePopulatorService;
import org.molgenis.security.core.runas.RunAsSystem;
import org.molgenis.security.core.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

@Service
public class WebAppDatabasePopulatorServiceImpl implements WebAppDatabasePopulatorService
{
	private final DataService dataService;
	private final MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService;

	@Autowired
	public WebAppDatabasePopulatorServiceImpl(DataService dataService,
			MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService)
	{
		if (dataService == null) throw new IllegalArgumentException("DataService is null");
		this.dataService = dataService;

		if (molgenisSecurityWebAppDatabasePopulatorService == null)
			throw new IllegalArgumentException("MolgenisSecurityWebAppDatabasePopulator is null");
		this.molgenisSecurityWebAppDatabasePopulatorService = molgenisSecurityWebAppDatabasePopulatorService;

	}

	@Override
	@Transactional
	@RunAsSystem
	public void populateDatabase()
	{
		molgenisSecurityWebAppDatabasePopulatorService.populateDatabase(this.dataService, HomeController.ID);

		MolgenisUser anonymousUser = molgenisSecurityWebAppDatabasePopulatorService.getAnonymousUser();
		UserAuthority anonymousHomeAuthority = new UserAuthority();
		anonymousHomeAuthority.setMolgenisUser(anonymousUser);
		anonymousHomeAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX + HomeController.ID.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, anonymousHomeAuthority);

		// BBMRI-ERIC specific population
		dataService.getMeta().addPackage(BbmriEricPackage.getPackage());

		// populate the staffsize categorical
		Map<String, String> staffSizes = Maps.newLinkedHashMap();
		staffSizes.put("0", "N/A");
		staffSizes.put("1", "1-2 FTE");
		staffSizes.put("2", "2-4 FTE");
		staffSizes.put("3", "5-8 FTE");
		staffSizes.put("4", "9-16 FTE");
		staffSizes.put("5", "17-32 FTE");
		staffSizes.put("6", "33-64 FTE");

		for (Entry<String, String> size : staffSizes.entrySet())
		{
			Entity e = new DefaultEntity(new StaffSizeMetaData(), dataService);
			e.set(StaffSizeMetaData.ID, size.getKey());
			e.set(StaffSizeMetaData.LABEL, size.getValue());
			dataService.add(StaffSizeMetaData.FULLY_QUALIFIED_NAME, e);
		}

		// populate the biobank size categorical
		Map<String, String> biobankSizes = Maps.newLinkedHashMap();
		biobankSizes.put("0", "N/A");
		biobankSizes.put("1", "10-99 samples");
		biobankSizes.put("2", "100-999 samples");
		biobankSizes.put("3", "1,000-9,999 samples");
		biobankSizes.put("4", "10,000-99,999 samples");
		biobankSizes.put("5", "100,000-999,999 samples");
		biobankSizes.put("6", "1,000,000-9,999,999 samples");
		biobankSizes.put("7", "10,000,000-99,999,999 samples");

		for (Entry<String, String> size : biobankSizes.entrySet())
		{
			Entity e = new DefaultEntity(new BiobankSizeMetaData(), dataService);
			e.set(StaffSizeMetaData.ID, size.getKey());
			e.set(StaffSizeMetaData.LABEL, size.getValue());
			dataService.add(BiobankSizeMetaData.FULLY_QUALIFIED_NAME, e);
		}
	}

	@Override
	@Transactional
	@RunAsSystem
	public boolean isDatabasePopulated()
	{
		return dataService.count(MolgenisUser.ENTITY_NAME, new QueryImpl()) > 0;
	}
}