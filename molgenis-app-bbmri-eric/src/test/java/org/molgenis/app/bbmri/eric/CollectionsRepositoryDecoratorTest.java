package org.molgenis.app.bbmri.eric;

import org.mockito.Mock;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.Repository;
import org.molgenis.test.AbstractMockitoTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CollectionsRepositoryDecoratorTest extends AbstractMockitoTest
{
	@Mock
	private Repository<Entity> decoratedRepository;
	@Mock
	private CollectionsQueryTransformer queryTransformer;
	@Mock
	private Query<Entity> query;
	@Mock
	private Query<Entity> transformedQuery;

	private CollectionsRepositoryDecorator collectionsRepositoryDecorator;

	@BeforeMethod
	public void setUpBeforeMethod()
	{
		when(queryTransformer.transformQuery(query)).thenReturn(transformedQuery);
		collectionsRepositoryDecorator = new CollectionsRepositoryDecorator(decoratedRepository, queryTransformer);
	}

	@Test
	public void testDelegate() throws Exception
	{
		assertEquals(collectionsRepositoryDecorator.delegate(), decoratedRepository);
	}

	@Test
	public void testCount() throws Exception
	{
		when(decoratedRepository.count(transformedQuery)).thenReturn(123L);
		assertEquals(collectionsRepositoryDecorator.count(query), 123L);
	}

	@Test
	public void testFindOne() throws Exception
	{
		Entity entity = mock(Entity.class);
		when(decoratedRepository.findOne(transformedQuery)).thenReturn(entity);
		assertEquals(collectionsRepositoryDecorator.findOne(query), entity);
	}

	@Test
	public void testFindAll() throws Exception
	{
		Stream<Entity> entities = Stream.empty();
		when(decoratedRepository.findAll(transformedQuery)).thenReturn(entities);
		assertEquals(collectionsRepositoryDecorator.findAll(query), entities);
	}
}