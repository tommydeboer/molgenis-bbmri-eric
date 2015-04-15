package org.molgenis.bbmri.eric.model;

import static org.molgenis.MolgenisFieldTypes.BOOL;
import static org.molgenis.MolgenisFieldTypes.EMAIL;
import static org.molgenis.MolgenisFieldTypes.HYPERLINK;
import static org.molgenis.MolgenisFieldTypes.INT;
import static org.molgenis.MolgenisFieldTypes.STRING;
import static org.molgenis.MolgenisFieldTypes.TEXT;

import org.molgenis.data.EntityMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.springframework.stereotype.Component;

/**
 * BBMRI-ERIC model v1.0
 * 
 * @author tommy
 *
 */
@Component
public class CatalogueMetaData extends DefaultEntityMetaData
{
	public static final EntityMetaData META_DATA = new CatalogueMetaData();
	public static final String ENTITY_NAME = "catalogue";

	public static final String BIOBANK_ID = "biobankID";
	public static final String BIOBANK_NAME = "biobankName";
	public static final String BIOBANK_JURIDICAL_PERSON = "biobankJuridicalPerson";
	public static final String BIOBANK_COUNTRY = "biobankCountry";
	public static final String BIOBANK_SIZE = "biobankSize";

	public static final String BIOBANK_MATERIAL_STORED_DNA = "biobankMaterialStoredDNA";
	public static final String BIOBANK_MATERIAL_STORED_CDNA_MRNA = "biobankMaterialStoredcDNAmRNA";
	public static final String BIOBANK_MATERIAL_STORED_MICRO_RNA = "biobankMaterialStoredmicroRNA";
	public static final String BIOBANK_MATERIAL_STORED_WHOLE_BLOOD = "biobankMaterialStoredWholeBlood";
	public static final String BIOBANK_MATERIAL_STORED_PBC = "biobankMaterialStoredPBC";

	public static final String BIOBANK_MATERIAL_STORED_PLASMA = "biobankMaterialStoredPlasma";
	public static final String BIOBANK_MATERIAL_STORED_SERUM = "biobankMaterialStoredSerum";
	public static final String BIOBANK_MATERIAL_STORED_TISSUE_CRYO = "biobankMaterialStoredTissueCryo";
	public static final String BIOBANK_MATERIAL_STORED_TISSUE_PARAFFIN = "biobankMaterialStoredTissueParaffin";
	public static final String BIOBANK_MATERIAL_STORED_CELL_LINES = "biobankMaterialStoredCellLines";

	public static final String BIOBANK_MATERIAL_STORED_URINE = "biobankMaterialStoredUrine";
	public static final String BIOBANK_MATERIAL_STORED_SALIVA = "biobankMaterialStoredSaliva";
	public static final String BIOBANK_MATERIAL_STORED_FAECES = "biobankMaterialStoredFaeces";
	public static final String BIOBANK_MATERIAL_STORED_PATHOGEN = "biobankMaterialStoredPathogen";
	public static final String BIOBANK_MATERIAL_STORED_OTHER = "biobankMaterialStoredOther";

	public static final String BIOBANK_PARTNER_CHARTER_SIGNED = "biobankPartnerCharterSigned";
	public static final String BIOBANK_SAMPLE_ACCESS_FEE = "biobankSampleAccessFee";
	public static final String BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS = "biobankSampleAccessJointProjects";
	public static final String BIOBANK_SAMPLE_ACCESS_DESCRIPTION = "biobankSampleAccessDescription";
	public static final String BIOBANK_DATA_ACCESS_FEE = "biobankDataAccessFee";

	public static final String BIOBANK_DATA_ACCESS_JOINT_PROJECTS = "biobankDataAccessJointProjects";
	public static final String BIOBANK_DATA_ACCESS_DESCRIPTION = "biobankDataAccessDescription";
	public static final String BIOBANK_SAMPLE_ACCESS_URI = "biobankSampleAccessURI";
	public static final String BIOBANK_DATA_ACCESS_URI = "biobankDataAccessURI";
	public static final String BIOBANK_AVAILABLE_MALE_SAMPLES_DATA = "biobankAvailableMaleSamplesData";

	public static final String BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA = "biobankAvailableFemaleSamplesData";
	public static final String BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES = "biobankAvailableBiologicalSamples";
	public static final String BIOBANK_AVAILABLE_SURVEY_DATA = "biobankAvailableSurveyData";
	public static final String BIOBANK_AVAILABLE_IMAGING_DATA = "biobankAvailableImagingData";
	public static final String BIOBANK_AVAILABLE_MEDICAL_RECORDS = "biobankAvailableMedicalRecords";

	public static final String BIOBANK_AVAILABLE_NATIONAL_REGISTRIES = "biobankAvailableNationalRegistries";
	public static final String BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS = "biobankAvailableGenealogicalRecords";
	public static final String BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS = "biobankAvailablePhysioBiochemMeasurements";
	public static final String BIOBANK_AVAILABLE_OTHER = "biobankAvailableOther";
	public static final String BIOBANK_IT_SUPPORT_AVAILABLE = "biobankITSupportAvailable";

	public static final String BIOBANK_IT_STAFF_SIZE = "biobankITStaffSize";
	public static final String BIOBANK_IS_AVAILABLE = "biobankISAvailable";
	public static final String BIOBANK_HIS_AVAILABLE = "biobankHISAvailable";
	public static final String DIAGNOSIS_AVAILABLE = "diagnosisAvailable";
	public static final String BIOBANK_CONTACT_EMAIL = "biobankContactEmail";

	public static final String BIOBANK_CONTACT_COUNTRY = "biobankContactCountry";
	public static final String BIOBANK_CONTACT_FIRST_NAME = "biobankContactFirstName";
	public static final String BIOBANK_CONTACT_LAST_NAME = "biobankContactLastName";
	public static final String BIOBANK_CONTACT_PHONE = "biobankContactPhone";
	public static final String BIOBANK_CONTACT_ADDRESS = "biobankContactAddress";

	public static final String BIOBANK_CONTACT_CITY = "biobankContactCity";
	public static final String BIOBANK_CONTACT_ZIP = "biobankContactZIP";
	public static final String BIOBANK_CONTACT_LATITUDE = "biobankContactLatitude";
	public static final String BIOBANK_CONTACT_LONGITUDE = "biobankContactLongitude";
	public static final String BIOBANK_CLINICAL = "biobankClinical";

	public static final String BIOBANK_POPULATION = "biobankPopulation";
	public static final String BIOBANK_RESEARCH_STUDY = "biobankResearchStudy";
	public static final String BIOBANK_STANDALONE = "biobankStandalone";
	public static final String BIOBANK_DESCRIPTION = "biobankDescription";
	public static final String BIOBANK_URL = "biobankURL";

	public static final String BIOBANK_ACRONYM = "biobankAcronym";
	public static final String BIOBANK_MATERIAL_STORED_RNA = "biobankMaterialStoredRNA";

	public CatalogueMetaData()
	{
		super(ENTITY_NAME, BbmriEricPackage.getPackage());

		addAttribute(BIOBANK_ID).setDataType(STRING).setNillable(false).setIdAttribute(true);
		addAttribute(BIOBANK_NAME).setDataType(STRING).setNillable(false).setLabelAttribute(true)
				.setLookupAttribute(true);
		addAttribute(BIOBANK_JURIDICAL_PERSON).setDataType(STRING).setNillable(false);
		addAttribute(BIOBANK_COUNTRY).setDataType(STRING).setNillable(false);
		addAttribute(BIOBANK_SIZE).setDataType(INT).setNillable(false);

		addAttribute(BIOBANK_MATERIAL_STORED_DNA).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_CDNA_MRNA).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_MICRO_RNA).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_WHOLE_BLOOD).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_PBC).setDataType(BOOL).setNillable(false);

		addAttribute(BIOBANK_MATERIAL_STORED_PLASMA).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_SERUM).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_TISSUE_CRYO).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_TISSUE_PARAFFIN).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_CELL_LINES).setDataType(BOOL).setNillable(false);

		addAttribute(BIOBANK_MATERIAL_STORED_URINE).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_SALIVA).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_FAECES).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_PATHOGEN).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_MATERIAL_STORED_OTHER).setDataType(BOOL).setNillable(false);

		addAttribute(BIOBANK_PARTNER_CHARTER_SIGNED).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_SAMPLE_ACCESS_FEE).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_SAMPLE_ACCESS_DESCRIPTION).setDataType(TEXT).setNillable(true);
		addAttribute(BIOBANK_DATA_ACCESS_FEE).setDataType(BOOL).setNillable(true);

		addAttribute(BIOBANK_DATA_ACCESS_JOINT_PROJECTS).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_DATA_ACCESS_DESCRIPTION).setDataType(TEXT).setNillable(true);
		addAttribute(BIOBANK_SAMPLE_ACCESS_URI).setDataType(HYPERLINK).setNillable(true);
		addAttribute(BIOBANK_DATA_ACCESS_URI).setDataType(HYPERLINK).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_MALE_SAMPLES_DATA).setDataType(BOOL).setNillable(true);

		addAttribute(BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_SURVEY_DATA).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_IMAGING_DATA).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_MEDICAL_RECORDS).setDataType(BOOL).setNillable(true);

		addAttribute(BIOBANK_AVAILABLE_NATIONAL_REGISTRIES).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_AVAILABLE_OTHER).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_IT_SUPPORT_AVAILABLE).setDataType(BOOL).setNillable(true);

		addAttribute(BIOBANK_IT_STAFF_SIZE).setDataType(INT).setNillable(true);
		addAttribute(BIOBANK_IS_AVAILABLE).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_HIS_AVAILABLE).setDataType(BOOL).setNillable(true);

		// the divergent naming of DIAGNOSIS_AVAILABLE is a historical artefact, do not 'fix'!
		addAttribute(DIAGNOSIS_AVAILABLE).setDataType(STRING).setNillable(true);
		addAttribute(BIOBANK_CONTACT_EMAIL).setDataType(EMAIL).setNillable(false);

		addAttribute(BIOBANK_CONTACT_COUNTRY).setDataType(STRING).setNillable(false);
		addAttribute(BIOBANK_CONTACT_FIRST_NAME).setDataType(STRING).setNillable(true);
		addAttribute(BIOBANK_CONTACT_LAST_NAME).setDataType(STRING).setNillable(true);
		addAttribute(BIOBANK_CONTACT_PHONE).setDataType(STRING).setNillable(true);
		addAttribute(BIOBANK_CONTACT_ADDRESS).setDataType(STRING).setNillable(true);

		addAttribute(BIOBANK_CONTACT_CITY).setDataType(STRING).setNillable(true);
		addAttribute(BIOBANK_CONTACT_ZIP).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_CONTACT_LATITUDE).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_CONTACT_LONGITUDE).setDataType(BOOL).setNillable(true);
		addAttribute(BIOBANK_CLINICAL).setDataType(BOOL).setNillable(false);

		addAttribute(BIOBANK_POPULATION).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_RESEARCH_STUDY).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_STANDALONE).setDataType(BOOL).setNillable(false);
		addAttribute(BIOBANK_DESCRIPTION).setDataType(TEXT).setNillable(true);
		addAttribute(BIOBANK_URL).setDataType(HYPERLINK).setNillable(true);

		addAttribute(BIOBANK_ACRONYM).setDataType(STRING).setNillable(true);
		addAttribute(BIOBANK_MATERIAL_STORED_RNA).setDataType(BOOL).setNillable(false);
	}
}
