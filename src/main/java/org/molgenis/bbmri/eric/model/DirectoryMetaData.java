package org.molgenis.bbmri.eric.model;

import static org.molgenis.MolgenisFieldTypes.BOOL;
import static org.molgenis.MolgenisFieldTypes.CATEGORICAL;
import static org.molgenis.MolgenisFieldTypes.COMPOUND;
import static org.molgenis.MolgenisFieldTypes.EMAIL;
import static org.molgenis.MolgenisFieldTypes.HYPERLINK;
import static org.molgenis.MolgenisFieldTypes.STRING;
import static org.molgenis.MolgenisFieldTypes.TEXT;

import java.util.List;

import org.elasticsearch.common.collect.Lists;
import org.molgenis.data.AttributeMetaData;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.support.DefaultAttributeMetaData;
import org.molgenis.data.support.DefaultEntityMetaData;
import org.springframework.stereotype.Component;

/**
 * BBMRI-ERIC model v2
 * 
 * @author tommy
 *
 */
@Component
public class DirectoryMetaData extends DefaultEntityMetaData
{
	public static final EntityMetaData META_DATA = new DirectoryMetaData();
	public static final String ENTITY_NAME = "directory";
	public static final String FULLY_QUALIFIED_NAME = BbmriEricPackage.NAME + '_' + ENTITY_NAME;

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
	public static final String BIOBANK_TYPE = "biobankType";
	public static final String BIOBANK_DONORS = "biobankDonors";
	public static final String BIOBANK_DATA_AVAILABILITY = "biobankDataAvailability";

	public static final String BIOBANK_MATERIAL = "biobankMaterial";
	public static final String BIOBANK_SAMPLE_ACCESS = "biobankSampleAccess";
	public static final String BIOBANK_DATA_ACCESS = "biobankDataAccess";
	public static final String BIOBANK_IT = "biobankIT";
	public static final String BIOBANK_CONTACT = "biobankContact";

	public DirectoryMetaData()
	{
		super(ENTITY_NAME, BbmriEricPackage.getPackage());

		setLabel("Biobank Directory");
		setDescription("BBMRI-ERIC Biobank Directory");

		addAttribute(BIOBANK_ID).setDataType(STRING).setNillable(false).setIdAttribute(true).setLabel("ID")
				.setDescription("Unique ID of the biobank within BBMRI ERIC");
		addAttribute(BIOBANK_NAME).setDataType(STRING).setNillable(false).setLabelAttribute(true)
				.setLookupAttribute(true).setLabel("Name").setDescription("Name of the biobank");
		addAttribute(BIOBANK_JURIDICAL_PERSON).setDataType(STRING).setNillable(false).setLabel("Organisation")
				.setDescription("Juristic person that hosts the biobank");
		addAttribute(BIOBANK_COUNTRY).setDataType(STRING).setNillable(false).setAggregateable(true).setLabel("Country")
				.setDescription("Country of residence of the biobank");
		addAttribute(BIOBANK_URL).setDataType(HYPERLINK).setNillable(true).setLabel("Website")
				.setDescription("Website");
		addAttribute(BIOBANK_DESCRIPTION).setDataType(TEXT).setNillable(true).setLabel("Description")
				.setDescription("Short description of the biobank");
		addAttribute(BIOBANK_PARTNER_CHARTER_SIGNED).setDataType(BOOL).setNillable(false).setAggregateable(true)
				.setLabel("Partner Charter signed")
				.setDescription("Has the biobank signed the BBMRI-ERIC Partner Charter?");
		addAttribute(BIOBANK_SIZE).setDataType(CATEGORICAL).setRefEntity(new BiobankSizeMetaData()).setNillable(false)
				.setLabel("Size").setDescription("Size of the biobank measured as 10^n samples");
		addAttribute(DIAGNOSIS_AVAILABLE)
				.setDataType(STRING)
				.setNillable(true)
				.setLabel("Diagnosis available")
				.setDescription(
						"Diagnosis available in the biobank, with the ontology prefix, possibly using * and ? wildcards, and prefix notation to denote diagnosis nomenclature");

		addAttribute(BIOBANK_ACRONYM).setDataType(STRING).setNillable(true).setLabel("Acronym")
				.setDescription("Textual string of short name in use for the biobank");

		// Biobank Type Compound
		List<AttributeMetaData> type = Lists.newArrayList();
		type.add(new DefaultAttributeMetaData(BIOBANK_CLINICAL).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Clinical collection")
				.setDescription("Does the biobank have clinical collections?"));
		type.add(new DefaultAttributeMetaData(BIOBANK_POPULATION).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Population-based collection")
				.setDescription("Does the biobank have population based collections?"));
		type.add(new DefaultAttributeMetaData(BIOBANK_RESEARCH_STUDY).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Research/Study collection")
				.setDescription("Does the biobank have research/study collections?"));
		type.add(new DefaultAttributeMetaData(BIOBANK_STANDALONE).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Stand-alone collection")
				.setDescription("Standalone collection standing outside of a biobank"));
		addAttribute(BIOBANK_TYPE).setDataType(COMPOUND).setLabel("Biobank type").setAttributesMetaData(type);

		// Biobank Donors Compound
		List<AttributeMetaData> donor = Lists.newArrayList();
		donor.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_MALE_SAMPLES_DATA).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Male")
				.setDescription("Denotes whether samples/data of male patients/donors are available"));
		donor.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_FEMALE_SAMPLES_DATA).setDataType(BOOL)
				.setNillable(true).setAggregateable(true).setLabel("Female")
				.setDescription("Denotes whether samples/data of male patients/donors are available"));
		addAttribute(BIOBANK_DONORS).setDataType(COMPOUND).setLabel("Donors").setAttributesMetaData(donor);

		// Biobank Data Availability Compound
		List<AttributeMetaData> data = Lists.newArrayList();
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_BIOLOGICAL_SAMPLES).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Biological Samples")
				.setDescription("Are there biological samples available?"));
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_SURVEY_DATA).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Survey Data").setDescription("Are there suvey data available?"));
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_IMAGING_DATA).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Imaging Data").setDescription("Are there imaging data available?"));
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_MEDICAL_RECORDS).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Medical Records")
				.setDescription("Are there medical records available?"));
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_NATIONAL_REGISTRIES)
				.setDataType(BOOL)
				.setNillable(true)
				.setAggregateable(true)
				.setLabel("National Registries")
				.setDescription(
						"Are register data is associated to the participants in the sample collection/study available?"));
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_GENEALOGICAL_RECORDS).setDataType(BOOL)
				.setNillable(true).setAggregateable(true).setLabel("Genealogical Records")
				.setDescription("Are there genealogical records available?"));
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_PHYSIO_BIOCHEM_MEASUREMENTS).setDataType(BOOL)
				.setNillable(true).setAggregateable(true).setLabel("Physiological/Biochemical Measurements")
				.setDescription("Are there physiological/biochemical measurements available?"));
		data.add(new DefaultAttributeMetaData(BIOBANK_AVAILABLE_OTHER).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Other").setDescription("Are there other data or measures available?"));
		addAttribute(BIOBANK_DATA_AVAILABILITY).setDataType(COMPOUND).setLabel("Available data")
				.setAttributesMetaData(data);

		// Biobank Material Compound
		List<AttributeMetaData> material = Lists.newArrayList();
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_DNA).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("DNA").setDescription("Is DNA collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_RNA).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("RNA").setDescription("Is RNA collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_CDNA_MRNA).setDataType(BOOL)
				.setNillable(false).setAggregateable(true).setLabel("cDNA/mRNA")
				.setDescription("Is cDNA/mRNA collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_MICRO_RNA).setDataType(BOOL)
				.setNillable(false).setAggregateable(true).setLabel("microRNA")
				.setDescription("Is microRNA collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_WHOLE_BLOOD).setDataType(BOOL)
				.setNillable(false).setAggregateable(true).setLabel("Whole Blood")
				.setDescription("Is whole blood collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_PBC).setDataType(BOOL).setNillable(false)
				.setLabel("Peripheral Blood Cells").setDescription("Are peripheral blood cells collected?"));

		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_PLASMA).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Plasma").setDescription("Is plasma collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_SERUM).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Serum").setDescription("Is serum collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_TISSUE_CRYO).setDataType(BOOL)
				.setNillable(false).setAggregateable(true).setLabel("Tissue cryo-preserved")
				.setDescription("Is cryo-preserved tissue collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_TISSUE_PARAFFIN).setDataType(BOOL)
				.setNillable(false).setAggregateable(true).setLabel("Tissue FPPE")
				.setDescription("Is paraffin preserved tissue collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_CELL_LINES).setDataType(BOOL)
				.setNillable(false).setAggregateable(true).setLabel("Cell lines")
				.setDescription("Are cell lines collected?"));

		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_URINE).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Urine").setDescription("Is urine collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_SALIVA).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Saliva").setDescription("Is saliva collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_FAECES).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Faeces").setDescription("Is faeces collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_PATHOGEN).setDataType(BOOL)
				.setNillable(false).setAggregateable(true).setLabel("Pathogen")
				.setDescription("Are pathogens collected?"));
		material.add(new DefaultAttributeMetaData(BIOBANK_MATERIAL_STORED_OTHER).setDataType(BOOL).setNillable(false)
				.setAggregateable(true).setLabel("Other").setDescription("Are other types of material collected?"));
		addAttribute(BIOBANK_MATERIAL).setDataType(COMPOUND).setLabel("Material collected")
				.setAttributesMetaData(material);

		// Biobank Sample Access Compound
		List<AttributeMetaData> sample = Lists.newArrayList();
		sample.add(new DefaultAttributeMetaData(BIOBANK_SAMPLE_ACCESS_FEE).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Access Fee asked")
				.setDescription("Denotes whether access to samples may be obtained on fee-based basis"));
		sample.add(new DefaultAttributeMetaData(BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS).setDataType(BOOL)
				.setNillable(true).setAggregateable(true).setLabel("Joint project required")
				.setDescription("Denotes whether access to samples may be obtained on joint project basis'"));
		sample.add(new DefaultAttributeMetaData(BIOBANK_SAMPLE_ACCESS_DESCRIPTION).setDataType(TEXT).setNillable(true)
				.setLabel("Description").setDescription("Short description of access rules"));
		sample.add(new DefaultAttributeMetaData(BIOBANK_SAMPLE_ACCESS_URI).setDataType(HYPERLINK).setNillable(true)
				.setLabel("Link to Access Policy").setDescription("Website describing access policy for the samples"));
		addAttribute(BIOBANK_SAMPLE_ACCESS).setDataType(COMPOUND).setLabel("Sample access policy")
				.setAttributesMetaData(sample);

		// Biobank Data Access Compound
		List<AttributeMetaData> dataAccess = Lists.newArrayList();
		dataAccess.add(new DefaultAttributeMetaData(BIOBANK_DATA_ACCESS_FEE).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Access Fee asked")
				.setDescription("Denotes whether access to data may be obtained on fee-based basis"));
		dataAccess.add(new DefaultAttributeMetaData(BIOBANK_DATA_ACCESS_JOINT_PROJECTS).setDataType(BOOL)
				.setNillable(true).setAggregateable(true).setLabel("Joint project required")
				.setDescription("Denotes whether access to data may be obtained on joint project basis"));
		dataAccess.add(new DefaultAttributeMetaData(BIOBANK_DATA_ACCESS_DESCRIPTION).setDataType(TEXT)
				.setNillable(true).setLabel("Description").setDescription("Short description of access rules"));
		dataAccess.add(new DefaultAttributeMetaData(BIOBANK_DATA_ACCESS_URI).setDataType(HYPERLINK).setNillable(true)
				.setLabel("Link to Access Policy").setDescription("Website describing access policy for the data"));
		addAttribute(BIOBANK_DATA_ACCESS).setDataType(COMPOUND).setLabel("Data access policy")
				.setAttributesMetaData(dataAccess);

		// Biobank IT Compound
		List<AttributeMetaData> it = Lists.newArrayList();
		it.add(new DefaultAttributeMetaData(BIOBANK_IT_SUPPORT_AVAILABLE).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("IT support available")
				.setDescription("Is IT support available at the biobank?"));
		it.add(new DefaultAttributeMetaData(BIOBANK_IT_STAFF_SIZE).setDataType(CATEGORICAL)
				.setRefEntity(new StaffSizeMetaData()).setNillable(false).setLabel("IT staff size")
				.setDescription("Size of the biobank dedicated IT staff measured as 2^n"));
		it.add(new DefaultAttributeMetaData(BIOBANK_IS_AVAILABLE).setDataType(BOOL).setNillable(true)
				.setAggregateable(true).setLabel("Computer-based Information System")
				.setDescription("Does the biobank have a computer-based Information System (IS)?"));
		it.add(new DefaultAttributeMetaData(BIOBANK_HIS_AVAILABLE)
				.setDataType(BOOL)
				.setNillable(true)
				.setAggregateable(true)
				.setLabel("Connection to hospital information system")
				.setDescription(
						"Does the biobank have an on-line or off-line connection to a Hospital Information System (HIS)?"));
		addAttribute(BIOBANK_IT).setDataType(COMPOUND).setLabel("Biobank IT infrastructure").setAttributesMetaData(it);

		// Biobank Contact Compound
		List<AttributeMetaData> contact = Lists.newArrayList();
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_FIRST_NAME).setDataType(STRING).setNillable(true)
				.setLabel("First name").setDescription("First name of the contact"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_LAST_NAME).setDataType(STRING).setNillable(true)
				.setLabel("Last name").setDescription("Last name of the contact"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_EMAIL).setDataType(EMAIL).setNillable(false)
				.setLabel("Email").setDescription("Contact e-mail address"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_PHONE).setDataType(STRING).setNillable(true)
				.setLabel("Phone").setDescription("Contact phone number including international prefix"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_ADDRESS).setDataType(STRING).setNillable(true)
				.setLabel("Address").setDescription("Contact address"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_CITY).setDataType(STRING).setNillable(true)
				.setLabel("City").setDescription("Contact city"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_ZIP).setDataType(STRING).setNillable(true)
				.setLabel("Postal code").setDescription("Contact postal code"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_COUNTRY).setDataType(STRING).setNillable(false)
				.setLabel("Country").setDescription("Country"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_LATITUDE)
				.setDataType(STRING)
				.setNillable(true)
				.setLabel("Latitude")
				.setDescription(
						"Latitute of the biobank in the WGS84 system (the one used by GPS), positive is northern hemisphere"));
		contact.add(new DefaultAttributeMetaData(BIOBANK_CONTACT_LONGITUDE)
				.setDataType(STRING)
				.setNillable(true)
				.setLabel("Longitude")
				.setDescription(
						"Longitude of the biobank in the WGS84 system (the one used by GPS), positive is to the East of Greenwich"));
		addAttribute(BIOBANK_CONTACT).setDataType(COMPOUND).setLabel("Contact information")
				.setAttributesMetaData(contact);
	}
}
