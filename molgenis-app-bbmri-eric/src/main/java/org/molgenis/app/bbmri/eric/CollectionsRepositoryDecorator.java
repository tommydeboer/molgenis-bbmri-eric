package org.molgenis.app.bbmri.eric;

import org.molgenis.data.AbstractRepositoryDecorator;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.Repository;
import org.molgenis.data.aggregation.AggregateQuery;
import org.molgenis.data.aggregation.AggregateResult;
import org.molgenis.data.support.AggregateQueryImpl;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class CollectionsRepositoryDecorator extends AbstractRepositoryDecorator<Entity>
{
	static final String COLLECTIONS_ID = "eu_bbmri_eric_collections";

	private final CollectionsQueryTransformer queryTransformer;

	CollectionsRepositoryDecorator(Repository<Entity> decoratedRepository, CollectionsQueryTransformer queryTransformer)
	{
		super(decoratedRepository);
		this.queryTransformer = requireNonNull(queryTransformer);
	}

	@Override
	public long count(Query<Entity> query)
	{
		if (isCollectionsEntityType())
		{
			query = query != null ? queryTransformer.transformQuery(query) : null;
		}
		return delegate().count(query);
	}

	@Override
	public Entity findOne(Query<Entity> query)
	{
		if (isCollectionsEntityType())
		{
			query = query != null ? queryTransformer.transformQuery(query) : null;
		}
		return delegate().findOne(query);
	}

	@Override
	public Stream<Entity> findAll(Query<Entity> query)
	{
		if (isCollectionsEntityType())
		{
			query = query != null ? queryTransformer.transformQuery(query) : null;
		}
		return delegate().findAll(query);
	}

	@Override
	public AggregateResult aggregate(AggregateQuery aggregateQuery)
	{
		if (isCollectionsEntityType())
		{
			Query<Entity> q = aggregateQuery.getQuery();
			Query<Entity> transformedQuery = q != null ? queryTransformer.transformQuery(q) : null;
			aggregateQuery = new AggregateQueryImpl(aggregateQuery.getAttributeX(), aggregateQuery.getAttributeY(),
					aggregateQuery.getAttributeDistinct(), transformedQuery);
		}
		return delegate().aggregate(aggregateQuery);
	}

	private boolean isCollectionsEntityType()
	{
		return getEntityType().getId().equals(COLLECTIONS_ID);
	}
}