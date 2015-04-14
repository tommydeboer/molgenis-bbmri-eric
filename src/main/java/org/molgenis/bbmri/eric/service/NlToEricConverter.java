package org.molgenis.bbmri.eric.service;

import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_ACRONYM;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_IMAGING_DATA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_MALE_SAMPLES_DATA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_MEDICAL_RECORDS;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_NATIONAL_REGISTRIES;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_OTHER;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_AVAILABLE_SURVEY_DATA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CLINICAL;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CONTACT_CITY;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CONTACT_COUNTRY;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CONTACT_EMAIL;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CONTACT_FIRST_NAME;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CONTACT_LAST_NAME;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CONTACT_PHONE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_CONTACT_ZIP;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_COUNTRY;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_DATA_ACCESS_DESCRIPTION;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_DATA_ACCESS_FEE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_DATA_ACCESS_JOINT_PROJECTS;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_DATA_ACCESS_URI;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_DESCRIPTION;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_HIS_AVAILABLE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_ID;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_IS_AVAILABLE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_IT_STAFF_SIZE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_IT_SUPPORT_AVAILABLE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_JURIDICAL_PERSON;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_CDNA_MRNA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_CELL_LINES;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_DNA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_FAECES;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_MICRO_RNA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_OTHER;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_PATHOGEN;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_PBC;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_PLASMA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_SALIVA;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_SERUM;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_TISSUE_CRYO;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_TISSUE_PARAFFIN;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_URINE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_MATERIAL_STORED_WHOLE_BLOOD;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_NAME;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_PARTNER_CHARTER_SIGNED;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_POPULATION;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_RESEARCH_STUDY;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_SAMPLE_ACCESS_DESCRIPTION;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_SAMPLE_ACCESS_FEE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_SAMPLE_ACCESS_URI;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_SIZE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_STANDALONE;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.BIOBANK_URL;
import static org.molgenis.bbmri.eric.model.CatalogueMetaData.DIAGNOSIS_AVAILABLE;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.support.DefaultEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Translates BBMRI-NL sample collections to BBMRI-ERIC catalogue entries. Entries (biobanks) are stored in the
 * 'catalogue' entity.
 * 
 * @author tommy
 *
 */
@Component
public class NlToEricConverter
{
	private final DataService dataService;

	private static final Logger LOG = LoggerFactory.getLogger(NlToEricConverter.class);

	private final String BBMRI_NL_SOURCE_ENTITY = "bbmri_nl_sample_collections";
	private final String NL = "NL";
	public static final String BBMRI_ERIC_CATALOGUE = "bbmri-eric_catalogue";

	// mapping defaults
	private final String DEFAULT_JURIDICAL_PERSON = "BBMRI-NL";
	private final int DEFAULT_IT_STAFF_SIZE = 1;
	private final String DEFAULT_DIAGNOSIS_AVAILABLE = "urn:miriam:icd:*";
	// floor(log10(nr of samples). All NL biobanks have at least 500 samples (actual numbers are not available)
	private final int DEFAULT_NR_OF_SAMPLES = (int) Math.floor(Math.log10(500));
	private String defaultContactEmail;

	// nl attribute names
	private final String ATT_MATERIALS = "materials";
	private final String ATT_SEX = "sex";
	private final String ATT_DATA_CATEGORIES = "data_categories";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_FEE = "biobankSampleAccessFee";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS = "biobankSampleAccessJointProjects";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_DESCRIPTION = "biobankSampleAccessDescription";
	private final String ATT_BIOBANK_DATA_ACCESS_FEE = "biobankDataAccessFee";
	private final String ATT_BIOBANK_DATA_ACCESS_JOINT_PROJECTS = "biobankDataAccessJointProjects";
	private final String ATT_BIOBANK_DATA_ACCESS_DESCRIPTION = "biobankDataAccessDescription";
	private final String ATT_BIOBANK_SAMPLE_ACCESS_URI = "biobankSampleAccessURI";
	private final String ATT_BIOBANK_DATA_ACCESS_URI = "biobankDataAccessURI";
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
			put(BIOBANK_MATERIAL_STORED_DNA, "DNA");
			put(BIOBANK_MATERIAL_STORED_CDNA_MRNA, "CDNA");
			put(BIOBANK_MATERIAL_STORED_MICRO_RNA, "MICRO_RNA");
			put(BIOBANK_MATERIAL_STORED_WHOLE_BLOOD, "WHOLE_BLOOD");
			put(BIOBANK_MATERIAL_STORED_PBC, "PERIPHERAL_BLOOD_CELLS");
			put(BIOBANK_MATERIAL_STORED_PLASMA, "PLASMA");
			put(BIOBANK_MATERIAL_STORED_SERUM, "SERUM");
			put(BIOBANK_MATERIAL_STORED_TISSUE_CRYO, "TISSUE_FROZEN");
			put(BIOBANK_MATERIAL_STORED_TISSUE_PARAFFIN, "TISSUE_PARAFFIN_EMBEDDED");
			put(BIOBANK_MATERIAL_STORED_CELL_LINES, "CELL_LINES");
			put(BIOBANK_MATERIAL_STORED_URINE, "URINE");
			put(BIOBANK_MATERIAL_STORED_SALIVA, "SALIVA");
			put(BIOBANK_MATERIAL_STORED_FAECES, "FECES");
			put(BIOBANK_MATERIAL_STORED_PATHOGEN, "PATHOGEN");
			put(BIOBANK_MATERIAL_STORED_OTHER, "OTHER");
		}
	};

	private static final HashMap<String, String> sexMapping = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			put(BIOBANK_AVAILABLE_MALE_SAMPLES_DATA, "MALE");
			put(BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA, "FEMALE");
		}
	};

	private static final HashMap<String, String> dataMapping = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 1L;
		{
			put(BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES, "BIOLOGICAL_SAMPLES");
			put(BIOBANK_AVAILABLE_SURVEY_DATA, "SURVEY_DATA");
			put(BIOBANK_AVAILABLE_IMAGING_DATA, "IMAGING_DATA");
			put(BIOBANK_AVAILABLE_MEDICAL_RECORDS, "MEDICAL_RECORDS");
			put(BIOBANK_AVAILABLE_NATIONAL_REGISTRIES, "NATIONAL_REGISTRIES");
			put(BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS, "GENEALOGICAL_RECORDS");
			put(BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS, "PHYSIOLOGICAL_BIOCHEMICAL_MEASUREMENTS");
			put(BIOBANK_AVAILABLE_OTHER, "OTHER");
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

	public void convertNlToEric()
	{
		if (defaultContactEmail == null) throw new RuntimeException(
				"Please set default_contact_email in molgenis-server.properties");

		LOG.info("Starting conversion of BBMRI-NL data to BBMRI-ERIC. BBMRI-NL entity = {}", BBMRI_NL_SOURCE_ENTITY);

		Iterable<Entity> it = dataService.findAll(BBMRI_NL_SOURCE_ENTITY);

		int adds = 0;
		int updates = 0;
		for (Entity nlBiobank : it)
		{
			DefaultEntity ericBiobank = new DefaultEntity(dataService.getEntityMetaData(BBMRI_ERIC_CATALOGUE),
					dataService);

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

			// add/update
			if (dataService.findOne(BBMRI_ERIC_CATALOGUE, ericBiobank.getIdValue()) == null)
			{
				dataService.add(BBMRI_ERIC_CATALOGUE, ericBiobank);
				adds++;
			}
			else
			{
				dataService.update(BBMRI_ERIC_CATALOGUE, ericBiobank);
				updates++;
			}

		}
		LOG.info(String.format("Added %s biobanks. Updated %s biobanks.", Integer.toString(adds),
				Integer.toString(updates)));
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
			if (refValues.contains(map.getValue()))
			{
				ericBiobank.set(map.getKey(), true);
			}
			else
			{
				ericBiobank.set(map.getKey(), false);
			}
		}
	}
}
