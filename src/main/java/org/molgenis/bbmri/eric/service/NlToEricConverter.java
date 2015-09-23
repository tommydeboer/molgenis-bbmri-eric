package org.molgenis.bbmri.eric.service;

import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_ACRONYM;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_IMAGING_DATA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_MALE_SAMPLES_DATA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_MEDICAL_RECORDS;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_NATIONAL_REGISTRIES;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_OTHER;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_AVAILABLE_SURVEY_DATA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CLINICAL;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CONTACT_CITY;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CONTACT_COUNTRY;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CONTACT_EMAIL;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CONTACT_FIRST_NAME;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CONTACT_LAST_NAME;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CONTACT_PHONE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_CONTACT_ZIP;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_COUNTRY;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_DATA_ACCESS_DESCRIPTION;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_DATA_ACCESS_FEE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_DATA_ACCESS_JOINT_PROJECTS;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_DATA_ACCESS_URI;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_DESCRIPTION;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_HIS_AVAILABLE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_ID;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_IS_AVAILABLE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_IT_STAFF_SIZE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_IT_SUPPORT_AVAILABLE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_JURIDICAL_PERSON;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_BLOOD;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_DNA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_FAECES;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_IMMORTALIZED_CELL_LINES;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_ISOLATED_PATHOGEN;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_OTHER;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_PLASMA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_RNA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_SALIVA;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_SERUM;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_TISSUE_FFPE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_TISSUE_FROZEN;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_MATERIAL_STORED_URINE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_NAME;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_PARTNER_CHARTER_SIGNED;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_POPULATION;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_RESEARCH_STUDY;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_SAMPLE_ACCESS_DESCRIPTION;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_SAMPLE_ACCESS_FEE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_SAMPLE_ACCESS_URI;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_SIZE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_STANDALONE;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.BIOBANK_URL;
import static org.molgenis.bbmri.eric.model.DirectoryMetaData.DIAGNOSIS_AVAILABLE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.molgenis.bbmri.eric.model.DirectoryMetaData;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.support.DefaultEntity;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.security.core.runas.RunAsSystem;
import org.molgenis.security.core.runas.RunAsSystemProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Translates BBMRI-NL sample collections to BBMRI-ERIC catalogue entries. Entries (biobanks) are stored in the
 * 'catalogue' entity.
 * 
 * @author tommy
 *
 */
@Service
public class NlToEricConverter
{
	private final DataService dataService;

	private static final Logger LOG = LoggerFactory.getLogger(NlToEricConverter.class);

	private final String BBMRI_NL_SOURCE_ENTITY = "bbmri_nl_sample_collections";
	private final String NL = "NL";

	// mapping defaults
	private final String DEFAULT_JURIDICAL_PERSON = "BBMRI-NL";
	private final int DEFAULT_IT_STAFF_SIZE = 1;
	private final String DEFAULT_DIAGNOSIS_AVAILABLE = "urn:miriam:icd:*";

	// floor(log10(nr of samples). All NL biobanks have at least 500 samples (actual numbers are not available)
	// floor(log10(500)) = 2
	private final int DEFAULT_NR_OF_SAMPLES = 2;
	private String defaultContactEmail;

	// nl attribute names
	private final String ATT_MATERIALS = "materials";
	private final String ATT_SEX = "sex";
	private final String ATT_DATA_CATEGORIES = "data_categories";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_FEE = "sampleAccessFee";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS = "sampleAccessJointProjects";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_DESCRIPTION = "sampleAccessDescription";
	private final String ATT_BIOBANK_DATA_ACCESS_FEE = "dataAccessFee";
	private final String ATT_BIOBANK_DATA_ACCESS_JOINT_PROJECTS = "dataAccessJointProjects";
	private final String ATT_BIOBANK_DATA_ACCESS_DESCRIPTION = "dataAccessDescription";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_URI = "sampleAccessURI";
	private final String ATT_BIOBANK_DATA_ACCESS_URI = "dataAccessURI";
	private final String ATT_EMAIL = "email";
	private final String ATT_COUNTRY = "country";
	private final String ATT_FIRST_NAME = "first_name";
	private final String ATT_LAST_NAME = "last_name";
	private final String ATT_PHONE = "phone";
	private final String ATT_CITY = "city";
	private final String ATT_ZIP = "zip";
	private final String ATT_CONTACT_PERSON = "contact_person";
	private final String ATT_TYPE = "type";
	private final String ATT_DESCRIPTION = "description";
	private final String ATT_URL = "website";
	private final String ATT_ACRONYM = "acronym";

	private static final HashMap<String, String> materialMapping = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			// old miabis format
			put("DNA", BIOBANK_MATERIAL_STORED_DNA);
			put("CDNA", BIOBANK_MATERIAL_STORED_RNA);
			put("MICRO_RNA", BIOBANK_MATERIAL_STORED_RNA);
			put("WHOLE_BLOOD", BIOBANK_MATERIAL_STORED_BLOOD);
			put("PERIPHERAL_BLOOD_CELLS", BIOBANK_MATERIAL_STORED_BLOOD);
			put("PLASMA", BIOBANK_MATERIAL_STORED_PLASMA);
			put("SERUM", BIOBANK_MATERIAL_STORED_SERUM);
			put("TISSUE_FROZEN", BIOBANK_MATERIAL_STORED_TISSUE_FROZEN);
			put("TISSUE_PARAFFIN_EMBEDDED", BIOBANK_MATERIAL_STORED_TISSUE_FFPE);
			put("CELL_LINES", BIOBANK_MATERIAL_STORED_IMMORTALIZED_CELL_LINES);
			put("URINE", BIOBANK_MATERIAL_STORED_URINE);
			put("SALIVA", BIOBANK_MATERIAL_STORED_SALIVA);
			put("FECES", BIOBANK_MATERIAL_STORED_FAECES);
			put("PATHOGEN", BIOBANK_MATERIAL_STORED_ISOLATED_PATHOGEN);
			put("RNA", BIOBANK_MATERIAL_STORED_RNA);
			put("OTHER", BIOBANK_MATERIAL_STORED_OTHER);

			// new miabis format (uses ERIC classification):
			put(BIOBANK_MATERIAL_STORED_DNA, BIOBANK_MATERIAL_STORED_DNA);
			put(BIOBANK_MATERIAL_STORED_RNA, BIOBANK_MATERIAL_STORED_RNA);
			put(BIOBANK_MATERIAL_STORED_BLOOD, BIOBANK_MATERIAL_STORED_BLOOD);
			put(BIOBANK_MATERIAL_STORED_PLASMA, BIOBANK_MATERIAL_STORED_PLASMA);
			put(BIOBANK_MATERIAL_STORED_SERUM, BIOBANK_MATERIAL_STORED_SERUM);
			put(BIOBANK_MATERIAL_STORED_TISSUE_FROZEN, BIOBANK_MATERIAL_STORED_TISSUE_FROZEN);
			put(BIOBANK_MATERIAL_STORED_TISSUE_FFPE, BIOBANK_MATERIAL_STORED_TISSUE_FFPE);
			put(BIOBANK_MATERIAL_STORED_IMMORTALIZED_CELL_LINES, BIOBANK_MATERIAL_STORED_IMMORTALIZED_CELL_LINES);
			put(BIOBANK_MATERIAL_STORED_URINE, BIOBANK_MATERIAL_STORED_URINE);
			put(BIOBANK_MATERIAL_STORED_SALIVA, BIOBANK_MATERIAL_STORED_SALIVA);
			put(BIOBANK_MATERIAL_STORED_FAECES, BIOBANK_MATERIAL_STORED_FAECES);
			put(BIOBANK_MATERIAL_STORED_ISOLATED_PATHOGEN, BIOBANK_MATERIAL_STORED_ISOLATED_PATHOGEN);
			put(BIOBANK_MATERIAL_STORED_OTHER, BIOBANK_MATERIAL_STORED_OTHER);
		}
	};

	private static final HashMap<String, String> sexMapping = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			// old miabis format
			put("MALE", BIOBANK_AVAILABLE_MALE_SAMPLES_DATA);
			put("FEMALE", BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA);

			// new miabis format (uses ERIC classification):
			put(BIOBANK_AVAILABLE_MALE_SAMPLES_DATA, BIOBANK_AVAILABLE_MALE_SAMPLES_DATA);
			put(BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA, BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA);
		}
	};

	private static final HashMap<String, String> dataMapping = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			// old miabis format
			put("BIOLOGICAL_SAMPLES", BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES);
			put("SURVEY_DATA", BIOBANK_AVAILABLE_SURVEY_DATA);
			put("IMAGING_DATA", BIOBANK_AVAILABLE_IMAGING_DATA);
			put("MEDICAL_RECORDS", BIOBANK_AVAILABLE_MEDICAL_RECORDS);
			put("NATIONAL_REGISTRIES", BIOBANK_AVAILABLE_NATIONAL_REGISTRIES);
			put("GENEALOGICAL_RECORDS", BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS);
			put("PHYSIOLOGICAL_BIOCHEMICAL_MEASUREMENTS", BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS);
			put("OTHER", BIOBANK_AVAILABLE_OTHER);

			// new miabis format (uses ERIC classification
			put(BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES, BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES);
			put(BIOBANK_AVAILABLE_SURVEY_DATA, BIOBANK_AVAILABLE_SURVEY_DATA);
			put(BIOBANK_AVAILABLE_IMAGING_DATA, BIOBANK_AVAILABLE_IMAGING_DATA);
			put(BIOBANK_AVAILABLE_MEDICAL_RECORDS, BIOBANK_AVAILABLE_MEDICAL_RECORDS);
			put(BIOBANK_AVAILABLE_NATIONAL_REGISTRIES, BIOBANK_AVAILABLE_NATIONAL_REGISTRIES);
			put(BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS, BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS);
			put(BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS, BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS);
			put(BIOBANK_AVAILABLE_OTHER, BIOBANK_AVAILABLE_OTHER);
		}
	};

	@Autowired
	public NlToEricConverter(DataService dataService, @Value("${default_contact_email}") String defaultContactEmail)
	{
		if (dataService == null) throw new IllegalArgumentException("dataService is null");
		if (defaultContactEmail == null) throw new RuntimeException(
				"Property default_contact_email not set in molgenis-server.properties");

		this.dataService = dataService;
		this.defaultContactEmail = defaultContactEmail;
	}

	// scheduled at midnight-ish
	@Scheduled(cron = "0 5 0 * * *")
	@RunAsSystem
	@Transactional
	public void convertNlToEric()
	{
		if (!dataService.hasRepository(BBMRI_NL_SOURCE_ENTITY))
		{
			LOG.warn("BBMRI-NL entity not found, skipping conversion to BBMRI-ERIC.");
			return;
		}

		if (defaultContactEmail == null) throw new RuntimeException(
				"Please set default_contact_email in molgenis-server.properties");

		// delete old NL nodes
		LOG.info("Deleting old NL catalogue nodes");

		Query q = new QueryImpl().eq(DirectoryMetaData.BIOBANK_COUNTRY, NL);
		Iterable<Entity> entitiesToDelete = RunAsSystemProxy.runAsSystem(() -> dataService.findAll(
				DirectoryMetaData.FULLY_QUALIFIED_NAME, q));

		dataService.delete(DirectoryMetaData.FULLY_QUALIFIED_NAME, entitiesToDelete);

		LOG.info("Starting conversion of BBMRI-NL data to BBMRI-ERIC. BBMRI-NL entity = {}", BBMRI_NL_SOURCE_ENTITY);

		Iterable<Entity> it = dataService.findAll(BBMRI_NL_SOURCE_ENTITY);

		int adds = 0;
		for (Entity nlBiobank : it)
		{
			DefaultEntity ericBiobank = new DefaultEntity(
					dataService.getEntityMetaData(DirectoryMetaData.FULLY_QUALIFIED_NAME), dataService);

			// mapped attributes
			ericBiobank.set(BIOBANK_ID, ericId((String) nlBiobank.getIdValue()));
			ericBiobank.set(BIOBANK_NAME, nlBiobank.getLabelValue());
			ericBiobank.set(BIOBANK_COUNTRY, NL);
			ericBiobank.set(BIOBANK_SAMPLE_ACCESS_FEE, nlBiobank.get(ATT_BIOBANK_SAMPLE_ACCESS_FEE));
			ericBiobank.set(BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS,
					nlBiobank.get(ATT_BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS));
			ericBiobank.set(BIOBANK_SAMPLE_ACCESS_DESCRIPTION, nlBiobank.get(ATT_BIOBANK_SAMPLE_ACCESS_DESCRIPTION));
			ericBiobank.set(BIOBANK_DATA_ACCESS_FEE, nlBiobank.get(ATT_BIOBANK_DATA_ACCESS_FEE));
			ericBiobank.set(BIOBANK_DATA_ACCESS_JOINT_PROJECTS, nlBiobank.get(ATT_BIOBANK_DATA_ACCESS_JOINT_PROJECTS));
			ericBiobank.set(BIOBANK_DATA_ACCESS_DESCRIPTION, nlBiobank.get(ATT_BIOBANK_DATA_ACCESS_DESCRIPTION));
			ericBiobank.set(BIOBANK_SAMPLE_ACCESS_URI, nlBiobank.get(ATT_BIOBANK_SAMPLE_ACCESS_URI));
			ericBiobank.set(BIOBANK_DATA_ACCESS_URI, nlBiobank.get(ATT_BIOBANK_DATA_ACCESS_URI));
			ericBiobank.set(BIOBANK_DESCRIPTION, nlBiobank.get(ATT_DESCRIPTION));
			ericBiobank.set(BIOBANK_URL, nlBiobank.get(ATT_URL));
			ericBiobank.set(BIOBANK_ACRONYM, nlBiobank.get(ATT_ACRONYM));

			// contact person (only use first)
			Entity person = nlBiobank.getEntities(ATT_CONTACT_PERSON).iterator().next();
			if (person.get(ATT_EMAIL) == null)
			{
				ericBiobank.set(BIOBANK_CONTACT_EMAIL, defaultContactEmail);
			}
			else
			{
				ericBiobank.set(BIOBANK_CONTACT_EMAIL, person.get(ATT_EMAIL));
			}

			if (person.getEntity(ATT_COUNTRY) == null)
			{
				ericBiobank.set(BIOBANK_CONTACT_COUNTRY, NL);
			}
			else
			{
				ericBiobank.set(BIOBANK_CONTACT_COUNTRY, person.getEntity(ATT_COUNTRY).getIdValue());
			}

			ericBiobank.set(BIOBANK_CONTACT_FIRST_NAME, person.get(ATT_FIRST_NAME));
			ericBiobank.set(BIOBANK_CONTACT_LAST_NAME, person.get(ATT_LAST_NAME));
			ericBiobank.set(BIOBANK_CONTACT_PHONE, person.get(ATT_PHONE));
			ericBiobank.set(BIOBANK_CONTACT_CITY, person.get(ATT_CITY));
			ericBiobank.set(BIOBANK_CONTACT_ZIP, person.get(ATT_ZIP));

			// mrefs to booleans
			convertMref(nlBiobank, ericBiobank, materialMapping, ATT_MATERIALS);
			convertMref(nlBiobank, ericBiobank, sexMapping, ATT_SEX);
			convertMref(nlBiobank, ericBiobank, dataMapping, ATT_DATA_CATEGORIES);

			// non-available mappings using default values
			ericBiobank.set(BIOBANK_PARTNER_CHARTER_SIGNED, true);
			ericBiobank.set(BIOBANK_SIZE, DEFAULT_NR_OF_SAMPLES);
			ericBiobank.set(BIOBANK_IT_SUPPORT_AVAILABLE, false);
			ericBiobank.set(BIOBANK_IT_STAFF_SIZE, DEFAULT_IT_STAFF_SIZE);
			ericBiobank.set(BIOBANK_IS_AVAILABLE, false);
			ericBiobank.set(BIOBANK_HIS_AVAILABLE, false);
			ericBiobank.set(BIOBANK_JURIDICAL_PERSON, DEFAULT_JURIDICAL_PERSON);

			// rule based mappings
			Set<String> types = new HashSet<>();
			nlBiobank.getEntities(ATT_TYPE).forEach((type) -> types.add(type.getIdValue().toString()));

			if (types.contains("HOSPITAL"))
			{
				ericBiobank.set(DIAGNOSIS_AVAILABLE, DEFAULT_DIAGNOSIS_AVAILABLE);
				ericBiobank.set(BIOBANK_CLINICAL, true);
				types.remove("HOSPITAL");
			}
			else
			{
				ericBiobank.set(BIOBANK_CLINICAL, false);
			}

			if (types.contains("POPULATION_BASED"))
			{
				ericBiobank.set(BIOBANK_POPULATION, true);
				types.remove("POPULATION_BASED");
			}
			else
			{
				ericBiobank.set(BIOBANK_POPULATION, false);
			}

			if (!types.isEmpty())
			{
				ericBiobank.set(BIOBANK_RESEARCH_STUDY, true);
			}
			else
			{
				ericBiobank.set(BIOBANK_RESEARCH_STUDY, false);
			}

			if (ericBiobank.getString(BIOBANK_JURIDICAL_PERSON).equals("BBMRI-NL"))
			{
				ericBiobank.set(BIOBANK_STANDALONE, true);
			}
			else
			{
				ericBiobank.set(BIOBANK_STANDALONE, false);
			}

			dataService.add(DirectoryMetaData.FULLY_QUALIFIED_NAME, ericBiobank);
			adds++;

		}
		LOG.info(String.format("Added %s NL biobanks.", Integer.toString(adds)));
	}

	/**
	 * Generates BBMRI ERIC identifier.
	 * 
	 * ISO 3166-1 alpha-2 + underscore + biobank national ID or name, prefixed with bbmri-eric:ID:
	 */
	private String ericId(String id)
	{
		return new StringBuilder().append("bbmri-eric:ID:").append(NL).append('_').append(id).toString();
	}

	/**
	 * Translates BBMRI-NL mrefs/categoricals to BBMRI-ERIC booleans using a mapping of ERIC boolean names and NL mref
	 * values.
	 * 
	 * @param nlBiobank
	 *            BBMRI-NL sample_collections entity
	 * @param ericBiobank
	 *            BBMRI-ERIC catalogue entity
	 * @param mapping
	 *            hashmap of ERIC booleans and NL mref values
	 * @param attribute
	 *            the mref attribute of the NL sample_collections entity
	 */
	private void convertMref(Entity nlBiobank, DefaultEntity ericBiobank, HashMap<String, String> mapping,
			String attribute)
	{
		Set<String> refValues = new HashSet<>();
		nlBiobank.getEntities(attribute).forEach((e) -> refValues.add((String) e.getIdValue()));

		for (Entry<String, String> map : mapping.entrySet())
		{
			if (refValues.contains(map.getKey()))
			{
				ericBiobank.set(map.getValue(), true);
			}
			else
			{
				// some NL terms are coalesced into one ERIC term. don't set it to false if it is already true
				if (!(ericBiobank.getBoolean(map.getValue()) != null && ericBiobank.getBoolean(map.getValue()) == true))
				{
					ericBiobank.set(map.getValue(), false);
				}
			}
		}
	}
}
