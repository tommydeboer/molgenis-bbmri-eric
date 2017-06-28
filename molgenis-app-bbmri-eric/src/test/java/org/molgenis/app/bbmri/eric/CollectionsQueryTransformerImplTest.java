package org.molgenis.app.bbmri.eric;

import org.mockito.Mock;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.meta.AttributeType;
import org.molgenis.data.meta.model.Attribute;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.test.AbstractMockitoTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class CollectionsQueryTransformerImplTest extends AbstractMockitoTest
{
	@Mock
	private DataService dataService;

	@Mock
	private Icd10ClassExpander icd10ClassExpander;

	private CollectionsQueryTransformerImpl collectionsQueryTransformerImpl;
	private List<Entity> expandedDiseaseEntities;

	@BeforeMethod
	public void setUpBeforeMethod()
	{
		collectionsQueryTransformerImpl = new CollectionsQueryTransformerImpl(icd10ClassExpander, dataService);

		Attribute boolAttr = createAttribute(AttributeType.BOOL);
		Attribute dateAttr = createAttribute(AttributeType.DATE);
		Attribute dateTimeAttr = createAttribute(AttributeType.DATE_TIME);
		Attribute decimalAttr = createAttribute(AttributeType.DECIMAL);
		Attribute intAttr = createAttribute(AttributeType.INT);
		Attribute longAttr = createAttribute(AttributeType.LONG);
		Attribute stringAttr = createAttribute(AttributeType.STRING);
		Attribute diagnosisAvailableAttr = createAttribute(AttributeType.MREF);
		when(diagnosisAvailableAttr.getName()).thenReturn("diagnosis_available");

		EntityType entityType = mock(EntityType.class);
		when(entityType.getAtomicAttributes()).thenReturn(
				asList(boolAttr, dateAttr, dateTimeAttr, decimalAttr, intAttr, longAttr, stringAttr,
						diagnosisAvailableAttr));
		when(dataService.getEntityType("eu_bbmri_eric_collections")).thenReturn(entityType);

		@SuppressWarnings("unchecked")
		Query<Entity> query = mock(Query.class);

		@SuppressWarnings("unchecked")
		Query<Entity> noDiseaseQuery = mock(Query.class);
		when(query.eq("code", "unknown disease")).thenReturn(noDiseaseQuery);
		when(noDiseaseQuery.or()).thenReturn(noDiseaseQuery);
		when(noDiseaseQuery.search("label", "unknown disease")).thenReturn(noDiseaseQuery);
		when(noDiseaseQuery.findAll()).thenReturn(Stream.empty());

		@SuppressWarnings("unchecked")
		Query<Entity> diseaseQuery = mock(Query.class);

		when(query.eq("code", "disease")).thenReturn(diseaseQuery);
		when(diseaseQuery.or()).thenReturn(diseaseQuery);
		when(diseaseQuery.search("label", "disease")).thenReturn(diseaseQuery);
		Entity diseaseEntity = mock(Entity.class);
		when(diseaseQuery.findAll()).thenReturn(Stream.of(diseaseEntity));

		when(dataService.query("eu_bbmri_eric_disease_types")).thenReturn(query);

		Entity expandedDiseaseEntity = mock(Entity.class);
		expandedDiseaseEntities = asList(diseaseEntity, expandedDiseaseEntity);
		when(icd10ClassExpander.expandClasses(singletonList(diseaseEntity)))
				.thenReturn(expandedDiseaseEntities);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void testBbmriEricCollectionsQueryTransformerImpl()
	{
		new CollectionsQueryTransformerImpl(null, null);
	}

	@DataProvider(name = "testTransformQuery")
	public Iterator<Object[]> testTransformQueryProvider()
	{
		List<Object[]> dataList = new ArrayList<>();
		dataList.add(new Object[] { new QueryImpl<>().eq("field", "value"), new QueryImpl<>().eq("field", "value") });
		dataList.add(new Object[] { new QueryImpl<>().search("value").or().eq("field", "value"),
				new QueryImpl<>().search("value").or().eq("field", "value") });
		dataList.add(new Object[] { new QueryImpl<>().search("unknown disease"),
				new QueryImpl<>().search("unknown disease") });
		dataList.add(new Object[] { new QueryImpl<>().search("disease"),
				new QueryImpl<>().search("STRING", "disease").or().in("diagnosis_available",
						expandedDiseaseEntities) });
		return dataList.iterator();
	}

	@Test(dataProvider = "testTransformQuery")
	public void testTransformQuery(Query<Entity> query, Query<Entity> expectedTransformedQuery)
	{
		Query<Entity> transformedQuery = collectionsQueryTransformerImpl.transformQuery(query);
		assertEquals(transformedQuery, expectedTransformedQuery);
	}

	private Attribute createAttribute(AttributeType attributeType)
	{
		Attribute attribute = when(mock(Attribute.class).getDataType()).thenReturn(attributeType).getMock();
		when(attribute.getName()).thenReturn(attributeType.toString());
		return attribute;
	}
}