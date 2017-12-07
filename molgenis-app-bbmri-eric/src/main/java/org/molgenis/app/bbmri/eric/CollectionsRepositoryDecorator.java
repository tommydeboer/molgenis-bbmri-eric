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
	private final Repository<Entity> decoratedRepository;
	private final CollectionsQueryTransformer queryTransformer;

	public CollectionsRepositoryDecorator(Repository<Entity> decoratedRepository,
			CollectionsQueryTransformer queryTransformer)
	{
		super(decoratedRepository);
		this.decoratedRepository = requireNonNull(decoratedRepository);
		this.queryTransformer = requireNonNull(queryTransformer);
	}

	@Override
	public Repository<Entity> delegate()
	{
		return decoratedRepository;
	}

	@Override
	public long count(Query<Entity> q)
	{
		Query<Entity> transformedQuery = q != null ? queryTransformer.transformQuery(q) : null;
		return super.count(transformedQuery);
	}

	@Override
	public Entity findOne(Query<Entity> q)
	{
		Query<Entity> transformedQuery = q != null ? queryTransformer.transformQuery(q) : null;
		return super.findOne(transformedQuery);
	}

	@Override
	public Stream<Entity> findAll(Query<Entity> q)
	{
		Query<Entity> transformedQuery = q != null ? queryTransformer.transformQuery(q) : null;
		return super.findAll(transformedQuery);
	}

	@Override
	public AggregateResult aggregate(AggregateQuery aggregateQuery)
	{
		Query<Entity> q = aggregateQuery.getQuery();
		Query<Entity> transformedQuery = q != null ? queryTransformer.transformQuery(q) : null;
		AggregateQuery transformedAggregateQuery = new AggregateQueryImpl(aggregateQuery.getAttributeX(),
				aggregateQuery.getAttributeY(), aggregateQuery.getAttributeDistinct(), transformedQuery);
		return super.aggregate(transformedAggregateQuery);
	}
}
