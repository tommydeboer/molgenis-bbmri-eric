package org.molgenis.app.bbmri.eric;

import org.mockito.Mock;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.app.bbmri.eric.CollectionsQueryTransformerImpl.DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE;
import static org.molgenis.app.bbmri.eric.CollectionsQueryTransformerImpl.DISEASE_TYPES_ENTITY_ID;
import static org.testng.Assert.assertEquals;

public class CollectionsQueryTransformerImplTest extends AbstractMockitoTest
{
	@Mock
	private DataService dataService;

	@Mock
	private Icd10ClassExpander icd10ClassExpander;

	private CollectionsQueryTransformerImpl collectionsQueryTransformerImpl;
	private List<Entity> expandedDiseaseEntities;
	private Entity diseaseEntity;

	@SuppressWarnings("unchecked")
	@BeforeMethod
	public void setUpBeforeMethod()
	{
		collectionsQueryTransformerImpl = new CollectionsQueryTransformerImpl(icd10ClassExpander, dataService);

		when(icd10ClassExpander.expandClasses(singletonList(diseaseEntity))).thenReturn(expandedDiseaseEntities);
	}

	/**
	 * Data providers are triggered before the @BeforeMethod so this can be used to set up the required mocks
	 */
	private void setExpandedDiseaseEntities()
	{
		diseaseEntity = mock(Entity.class);
		Entity expandedDiseaseEntity = mock(Entity.class);
		expandedDiseaseEntities = asList(diseaseEntity, expandedDiseaseEntity);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void testBbmriEricCollectionsQueryTransformerImpl()
	{
		new CollectionsQueryTransformerImpl(null, null);
	}

	@SuppressWarnings("UnnecessaryLocalVariable")
	@DataProvider(name = "nonTransformableQueryProvider")
	public Iterator<Object[]> nonTransformableQueryProvider()
	{
		setExpandedDiseaseEntities();

		List<Object[]> dataList = new ArrayList<>();

		{
			Query query = new QueryImpl<>().eq("field", "value");
			dataList.add(new Object[] { query, query });
		}
		{
			Query query = new QueryImpl<>().search("value").or().eq("field", "value");
			dataList.add(new Object[] { query, query });
		}
		{
			Query query = new QueryImpl<>().search("disease");
			dataList.add(new Object[] { query, query });
		}

		return dataList.iterator();
	}

	@Test(dataProvider = "nonTransformableQueryProvider")
	public void testNonTransformableQueries(Query<Entity> query, Query<Entity> expectedTransformedQuery)
	{
		Query<Entity> transformedQuery = collectionsQueryTransformerImpl.transformQuery(query);
		assertEquals(transformedQuery, expectedTransformedQuery);
	}

	@DataProvider(name = "transformableQueryProvider")
	public Iterator<Object[]> transformableQueryProvider()
	{
		setExpandedDiseaseEntities();

		List<Object[]> dataList = new ArrayList<>();

		{
			Query query = new QueryImpl<>().eq(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, "disease");
			Query expected = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseEntities);
			dataList.add(new Object[] { query, expected });
		}
		{
			Query query = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, singletonList("disease"));
			Query expected = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseEntities);
			dataList.add(new Object[] { query, expected });
		}
		{
			Query query = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE,
					asList("disease", "disease2"));
			Query expected = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseEntities);
			dataList.add(new Object[] { query, expected });
		}
		{
			Query query = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE,
					asList("disease", "unknown disease"));
			Query expected = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseEntities);
			dataList.add(new Object[] { query, expected });
		}
		{
			Query query = new QueryImpl<>().eq(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, "disease")
										   .and()
										   .eq("otherAttr", "test");
			Query expected = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseEntities)
											  .and()
											  .eq("otherAttr", "test");
			dataList.add(new Object[] { query, expected });
		}
		{
			Query query = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, singletonList("disease"))
										   .or()
										   .in("otherAttr", singletonList("test"));
			Query expected = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseEntities)
											  .or()
											  .in("otherAttr", singletonList("test"));
			dataList.add(new Object[] { query, expected });
		}
		{
			Query query = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, singletonList("disease"))
										   .and()
										   .search("test");
			Query expected = new QueryImpl<>().in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseEntities)
											  .and()
											  .search("test");
			dataList.add(new Object[] { query, expected });
		}

		return dataList.iterator();
	}

	@SuppressWarnings("unchecked")
	@Test(dataProvider = "transformableQueryProvider")
	public void testTransformableQueries(Query<Entity> query, Query<Entity> expectedTransformedQuery)
	{
		when(dataService.findAll(eq(DISEASE_TYPES_ENTITY_ID), any(Stream.class))).thenReturn(Stream.of(diseaseEntity));

		Query<Entity> transformedQuery = collectionsQueryTransformerImpl.transformQuery(query);
		assertEquals(transformedQuery, expectedTransformedQuery);
	}
}