package org.molgenis.palga.elasticsearch;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.molgenis.data.AggregateQuery;
import org.molgenis.data.AggregateResult;
import org.molgenis.data.Aggregateable;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.IndexedCrudRepository;
import org.molgenis.data.Query;
import org.molgenis.data.elasticsearch.ElasticsearchRepository;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ElasticsearchCacheRepositoryDecorator implements IndexedCrudRepository, Aggregateable
{
	private final ElasticsearchRepository elasticsearchRepository;
	private LoadingCache<AggregateQuery, AggregateResult> aggregateCache;

	public ElasticsearchCacheRepositoryDecorator(ElasticsearchRepository elasticsearchRepository)
	{
		if (elasticsearchRepository == null) throw new IllegalArgumentException("elasticsearchRepository is null");
		this.elasticsearchRepository = elasticsearchRepository;
	}

	@Override
	public EntityMetaData getEntityMetaData()
	{
		return elasticsearchRepository.getEntityMetaData();
	}

	@Override
	public <E extends Entity> Iterable<E> iterator(Class<E> clazz)
	{
		return elasticsearchRepository.iterator(clazz);
	}

	@Override
	public String getUrl()
	{
		return elasticsearchRepository.getUrl();
	}

	@Override
	public long count()
	{
		return elasticsearchRepository.count();
	}

	@Override
	public Query query()
	{
		return elasticsearchRepository.query();
	}

	@Override
	public long count(Query q)
	{
		return elasticsearchRepository.count(q);
	}

	@Override
	public Iterable<Entity> findAll(Query q)
	{
		return elasticsearchRepository.findAll(q);
	}

	@Override
	public <E extends Entity> Iterable<E> findAll(Query q, Class<E> clazz)
	{
		return elasticsearchRepository.findAll(q, clazz);
	}

	@Override
	public Entity findOne(Query q)
	{
		return elasticsearchRepository.findOne(q);
	}

	@Override
	public Entity findOne(Object id)
	{
		return elasticsearchRepository.findOne(id);
	}

	@Override
	public Iterable<Entity> findAll(Iterable<Object> ids)
	{
		return elasticsearchRepository.findAll(ids);
	}

	@Override
	public <E extends Entity> Iterable<E> findAll(Iterable<Object> ids, Class<E> clazz)
	{
		return elasticsearchRepository.findAll(ids, clazz);
	}

	@Override
	public <E extends Entity> E findOne(Object id, Class<E> clazz)
	{
		return elasticsearchRepository.findOne(id, clazz);
	}

	@Override
	public <E extends Entity> E findOne(Query q, Class<E> clazz)
	{
		return elasticsearchRepository.findOne(q, clazz);
	}

	@Override
	public Iterator<Entity> iterator()
	{
		return elasticsearchRepository.iterator();
	}

	@Override
	public void close() throws IOException
	{
		elasticsearchRepository.close();
	}

	@Override
	public String getName()
	{
		return elasticsearchRepository.getName();
	}

	@Override
	public AggregateResult aggregate(AggregateQuery aggregateQuery)
	{
		try
		{
			return getAggregateCache().get(aggregateQuery);
		}
		catch (ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void add(Entity entity)
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.add(entity);
	}

	@Override
	public Integer add(Iterable<? extends Entity> entities)
	{
		getAggregateCache().invalidateAll();
		return elasticsearchRepository.add(entities);
	}

	@Override
	public void flush()
	{
		elasticsearchRepository.flush();
	}

	@Override
	public void clearCache()
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.clearCache();
	}

	@Override
	public void update(Entity entity)
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.update(entity);
	}

	@Override
	public void update(Iterable<? extends Entity> entities)
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.update(entities);
	}

	@Override
	public void delete(Entity entity)
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.delete(entity);
	}

	@Override
	public void delete(Iterable<? extends Entity> entities)
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.delete(entities);
	}

	@Override
	public void deleteById(Object id)
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.deleteById(id);
	}

	@Override
	public void deleteById(Iterable<Object> ids)
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.deleteById(ids);
	}

	@Override
	public void deleteAll()
	{
		getAggregateCache().invalidateAll();
		elasticsearchRepository.deleteAll();
	}

	@Override
	public void rebuildIndex()
	{
		elasticsearchRepository.rebuildIndex();
	}

	@Override
	public void drop()
	{
		elasticsearchRepository.drop();
	}

	@Override
	public void create()
	{
		elasticsearchRepository.create();
	}

	public void invalidateCache()
	{
		getAggregateCache().invalidateAll();
	}

	private LoadingCache<AggregateQuery, AggregateResult> getAggregateCache()
	{
		if (aggregateCache == null)
		{
			aggregateCache = CacheBuilder.newBuilder().maximumSize(1000)
					.build(new CacheLoader<AggregateQuery, AggregateResult>()
					{
						@Override
						public AggregateResult load(AggregateQuery aggregateQuery) throws Exception
						{
							return elasticsearchRepository.aggregate(aggregateQuery);
						}
					});
		}

		return aggregateCache;
	}
}
