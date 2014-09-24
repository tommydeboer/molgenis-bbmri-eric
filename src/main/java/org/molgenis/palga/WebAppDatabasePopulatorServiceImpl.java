package org.molgenis.palga;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.molgenis.data.DataService;
import org.molgenis.data.IndexedCrudRepositorySecurityDecorator;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.dataexplorer.controller.DataExplorerController;
import org.molgenis.framework.db.WebAppDatabasePopulatorService;
import org.molgenis.omx.auth.GroupAuthority;
import org.molgenis.omx.auth.MolgenisGroup;
import org.molgenis.omx.auth.MolgenisUser;
import org.molgenis.omx.auth.UserAuthority;
import org.molgenis.omx.core.RuntimeProperty;
import org.molgenis.palga.controller.HomeController;
import org.molgenis.palga.importer.PalgaSampleImporter;
import org.molgenis.security.MolgenisSecurityWebAppDatabasePopulatorService;
import org.molgenis.security.core.utils.SecurityUtils;
import org.molgenis.security.runas.RunAsSystem;
import org.molgenis.security.user.UserAccountController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebAppDatabasePopulatorServiceImpl implements WebAppDatabasePopulatorService
{
	static final String KEY_APP_HREF_CSS = "app.href.css";
	static final String KEY_APP_NAME = "app.name";
	static final String KEY_APP_HREF_LOGO = "app.href.logo";

	private final DataService dataService;
	private final MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService;

	@Autowired
	public WebAppDatabasePopulatorServiceImpl(DataService dataService,
			MolgenisSecurityWebAppDatabasePopulatorService molgenisSecurityWebAppDatabasePopulatorService)
	{
		if (dataService == null) throw new IllegalArgumentException("DataService is null");
		this.dataService = dataService;

		if (molgenisSecurityWebAppDatabasePopulatorService == null) throw new IllegalArgumentException(
				"MolgenisSecurityWebAppDatabasePopulator is null");
		this.molgenisSecurityWebAppDatabasePopulatorService = molgenisSecurityWebAppDatabasePopulatorService;

	}

	@Override
	@Transactional
	@RunAsSystem
	public void populateDatabase()
	{
		molgenisSecurityWebAppDatabasePopulatorService.populateDatabase(this.dataService, HomeController.ID);
		MolgenisUser anonymousUser = molgenisSecurityWebAppDatabasePopulatorService.getAnonymousUser();
		MolgenisGroup usersGroup = molgenisSecurityWebAppDatabasePopulatorService.getAllUsersGroup();

		UserAuthority anonymousAuthority = new UserAuthority();
		anonymousAuthority.setMolgenisUser(anonymousUser);
		anonymousAuthority.setRole(SecurityUtils.AUTHORITY_ANONYMOUS);
		dataService.add(UserAuthority.ENTITY_NAME, anonymousAuthority);

		GroupAuthority usersGroupHomeAuthority = new GroupAuthority();
		usersGroupHomeAuthority.setMolgenisGroup(usersGroup);
		usersGroupHomeAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_READ_PREFIX + HomeController.ID.toUpperCase());
		dataService.add(GroupAuthority.ENTITY_NAME, usersGroupHomeAuthority);

		GroupAuthority usersGroupUserAccountAuthority = new GroupAuthority();
		usersGroupUserAccountAuthority.setMolgenisGroup(usersGroup);
		usersGroupUserAccountAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX
				+ UserAccountController.ID.toUpperCase());
		dataService.add(GroupAuthority.ENTITY_NAME, usersGroupUserAccountAuthority);

		UserAuthority anonymousDataExplorerAuthority = new UserAuthority();
		anonymousDataExplorerAuthority.setMolgenisUser(anonymousUser);
		anonymousDataExplorerAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX
				+ DataExplorerController.ID.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, anonymousDataExplorerAuthority);

		UserAuthority anonymousHomeAuthority = new UserAuthority();
		anonymousHomeAuthority.setMolgenisUser(anonymousUser);
		anonymousHomeAuthority.setRole(SecurityUtils.AUTHORITY_PLUGIN_WRITE_PREFIX + HomeController.ID.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, anonymousHomeAuthority);

		UserAuthority entityPaglasampleAuthority = new UserAuthority();
		entityPaglasampleAuthority.setMolgenisUser(anonymousUser);
		entityPaglasampleAuthority.setRole("ROLE_ENTITY_COUNT_"
				+ PalgaSampleImporter.ENTITY_NAME_PALGA_SAMPLE.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityPaglasampleAuthority);

		UserAuthority entityMaterialAuthority = new UserAuthority();
		entityMaterialAuthority.setMolgenisUser(anonymousUser);
		entityMaterialAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ PalgaSampleImporter.ENTITY_NAME_MATERIAL.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityMaterialAuthority);

		UserAuthority entityDiagnosisAuthority = new UserAuthority();
		entityDiagnosisAuthority.setMolgenisUser(anonymousUser);
		entityDiagnosisAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ PalgaSampleImporter.ENTITY_NAME_DIAGNOSIS.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityDiagnosisAuthority);

		UserAuthority entityRetrievalTermAuthority = new UserAuthority();
		entityRetrievalTermAuthority.setMolgenisUser(anonymousUser);
		entityRetrievalTermAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ PalgaSampleImporter.ENTITY_NAME_RETRIEVAL_TERM.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityRetrievalTermAuthority);

		UserAuthority entityAgegroupAuthority = new UserAuthority();
		entityAgegroupAuthority.setMolgenisUser(anonymousUser);
		entityAgegroupAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ PalgaSampleImporter.ENTITY_NAME_AGE_GROUP.toUpperCase());

		dataService.add(UserAuthority.ENTITY_NAME, entityAgegroupAuthority);

		UserAuthority entityGenderAuthority = new UserAuthority();
		entityGenderAuthority.setMolgenisUser(anonymousUser);
		entityGenderAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ PalgaSampleImporter.ENTITY_NAME_GENDER.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityGenderAuthority);

		UserAuthority entityRTPAuthority = new UserAuthority();
		entityRTPAuthority.setMolgenisUser(anonymousUser);
		entityRTPAuthority.setRole(SecurityUtils.AUTHORITY_ENTITY_READ_PREFIX
				+ RuntimeProperty.ENTITY_NAME.toUpperCase());
		dataService.add(UserAuthority.ENTITY_NAME, entityRTPAuthority);

		Map<String, String> runtimePropertyMap = new HashMap<String, String>();
		runtimePropertyMap.put(KEY_APP_HREF_CSS, "palga.css");
		runtimePropertyMap.put(KEY_APP_HREF_LOGO, "/img/logo_palga.png");
		runtimePropertyMap.put(KEY_APP_NAME, "PALGA");

		runtimePropertyMap.put(DataExplorerController.INITLOCATION,
				"chr:'1',viewStart:10000000,viewEnd:10100000,cookieKey:'human',nopersist:true");
		runtimePropertyMap.put(DataExplorerController.COORDSYSTEM,
				"{speciesName: 'Human',taxon: 9606,auth: 'GRCh',version: '37',ucscName: 'hg19'}");
		runtimePropertyMap
				.put(DataExplorerController.CHAINS,
						"{hg18ToHg19: new Chainset('http://www.derkholm.net:8080/das/hg18ToHg19/', 'NCBI36', 'GRCh37',{speciesName: 'Human',taxon: 9606,auth: 'NCBI',version: 36,ucscName: 'hg18'})}");
		// for use of the demo dataset add to
		// SOURCES:",{name:'molgenis mutations',uri:'http://localhost:8080/das/molgenis/',desc:'Default from WebAppDatabasePopulatorService'}"
		runtimePropertyMap
				.put(DataExplorerController.SOURCES,
						"[{name:'Genome',twoBitURI:'http://www.biodalliance.org/datasets/hg19.2bit',tier_type: 'sequence'},{name: 'Genes',desc: 'Gene structures from GENCODE 19',bwgURI: 'http://www.biodalliance.org/datasets/gencode.bb',stylesheet_uri: 'http://www.biodalliance.org/stylesheets/gencode.xml',collapseSuperGroups: true,trixURI:'http://www.biodalliance.org/datasets/geneIndex.ix'},{name: 'Repeats',desc: 'Repeat annotation from Ensembl 59',bwgURI: 'http://www.biodalliance.org/datasets/repeats.bb',stylesheet_uri: 'http://www.biodalliance.org/stylesheets/bb-repeats.xml'},{name: 'Conservation',desc: 'Conservation',bwgURI: 'http://www.biodalliance.org/datasets/phastCons46way.bw',noDownsample: true}]");
		runtimePropertyMap
				.put(DataExplorerController.BROWSERLINKS,
						"{Ensembl: 'http://www.ensembl.org/Homo_sapiens/Location/View?r=${chr}:${start}-${end}',UCSC: 'http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&position=chr${chr}:${start}-${end}',Sequence: 'http://www.derkholm.net:8080/das/hg19comp/sequence?segment=${chr}:${start},${end}'}");

		// Charts include/exclude charts
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_AGGREGATES, String.valueOf(true));
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_CHARTS, String.valueOf(false));
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_ANNOTATORS, String.valueOf(false));
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_DATA, String.valueOf(false));

		runtimePropertyMap.put(DataExplorerController.KEY_HIDE_SEARCH_BOX, String.valueOf(true));
		runtimePropertyMap.put(DataExplorerController.KEY_HIDE_ITEM_SELECTION, String.valueOf(true));

		runtimePropertyMap.put(DataExplorerController.KEY_MOD_AGGREGATES_DISTINCT_HIDE, String.valueOf(true));
		runtimePropertyMap.put(DataExplorerController.KEY_MOD_AGGREGATES_DISTINCT_OVERRIDE + "."
				+ PalgaSampleImporter.ENTITY_NAME_PALGA_SAMPLE, PalgaSampleImporter.ATTR_EXCERPT_NR);

		runtimePropertyMap.put(DataExplorerController.KEY_SHOW_WIZARD_ONINIT, String.valueOf(true));

		runtimePropertyMap.put("i18nLocale", "palga");

		// Aggregate anonymization threshold (10)
		runtimePropertyMap.put(IndexedCrudRepositorySecurityDecorator.SETTINGS_KEY_AGGREGATE_ANONYMIZATION_THRESHOLD,
				Integer.toString(10));

		// Annotators include files/tools
		String molgenisHomeDir = System.getProperty("molgenis.home");

		if (molgenisHomeDir == null)
		{
			throw new IllegalArgumentException("missing required java system property 'molgenis.home'");
		}

		if (!molgenisHomeDir.endsWith("/")) molgenisHomeDir = molgenisHomeDir + '/';

		for (Entry<String, String> entry : runtimePropertyMap.entrySet())
		{
			RuntimeProperty runtimeProperty = new RuntimeProperty();
			String propertyKey = entry.getKey();
			runtimeProperty.setIdentifier(RuntimeProperty.class.getSimpleName() + '_' + propertyKey);
			runtimeProperty.setName(propertyKey);
			runtimeProperty.setValue(entry.getValue());
			dataService.add(RuntimeProperty.ENTITY_NAME, runtimeProperty);
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