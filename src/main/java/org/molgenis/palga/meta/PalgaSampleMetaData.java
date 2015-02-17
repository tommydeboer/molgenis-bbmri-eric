package org.molgenis.palga.meta;

import static org.molgenis.MolgenisFieldTypes.CATEGORICAL;
import static org.molgenis.MolgenisFieldTypes.INT;
import static org.molgenis.MolgenisFieldTypes.MREF;

import org.molgenis.data.Range;
import org.molgenis.data.support.DefaultEntityMetaData;

public class PalgaSampleMetaData extends DefaultEntityMetaData
{
	public static final PalgaSampleMetaData INSTANCE = new PalgaSampleMetaData();
	public static final String ATTR_ID = "id";
	public static final String ATTR_EXCERPT_NR = "excerptNr";
	public static final String ATTR_ROW_NR = "regelNummer";
	public static final String ATTR_DIAGNOSIS = "diagnose";
	public static final String ATTR_RETRIEVAL_TERM = "retrievalTerm";
	public static final String ATTR_MATERIAL = "materiaal";
	public static final String ATTR_GENDER = "geslacht";
	public static final String ATTR_AGE = "leeftijd";
	public static final String ATTR_YEAR = "jaar";

	private PalgaSampleMetaData()
	{
		super("PalgaSample");
		setLabel("PALGA Openbare Databank");
		addAttribute(ATTR_ID).setIdAttribute(true).setNillable(false).setVisible(false);
		addAttribute(ATTR_EXCERPT_NR).setNillable(false).setDataType(INT).setLabel("Excerpt nummer").setVisible(false);
		addAttribute(ATTR_DIAGNOSIS).setDataType(MREF).setRefEntity(DiagnosisMetaData.INSTANCE).setNillable(true)
				.setLabel("Zoekterm");
		addAttribute(ATTR_RETRIEVAL_TERM).setDataType(MREF).setRefEntity(RetrievaltermMetaData.INSTANCE)
				.setNillable(true).setLabel("Retrievalterm");
		addAttribute(ATTR_MATERIAL).setDataType(CATEGORICAL).setRefEntity(MaterialMetaData.INSTANCE).setNillable(false)
				.setLabel("Materiaal").setAggregateable(true);
		addAttribute(ATTR_GENDER).setDataType(CATEGORICAL).setRefEntity(GenderMetaData.INSTANCE).setNillable(false)
				.setLabel("Geslacht").setAggregateable(true);
		addAttribute(ATTR_AGE).setDataType(CATEGORICAL).setRefEntity(AgegroupMetaData.INSTANCE).setNillable(false)
				.setLabel("Leeftijdscategorie").setAggregateable(true);
		addAttribute(ATTR_YEAR).setDataType(INT).setNillable(false).setRange(new Range(1991l, 2015l)).setLabel("Jaar")
				.setAggregateable(true).setDescription("Jaar van verslaglegging");
	}
}
