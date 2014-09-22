package org.molgenis.palga.elasticsearch;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import org.molgenis.data.AggregateQuery;
import org.molgenis.data.AggregateResult;
import org.molgenis.data.Aggregateable;
import org.molgenis.data.CrudRepository;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.Query;
import org.molgenis.data.elasticsearch.ElasticsearchRepository;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ElasticsearchCacheRepositoryDecorator implements CrudRepository, Aggregateable
{
	private final ElasticsearchRepository elasticsearchRepository;
	private LoadingCache<AggregateQuery, AggregateResult> aggregateCache;

	public ElasticsearchCacheRepositoryDecorator(ElasticsearchRepository elasticsearchRepository)
	{
		if (elasticsearchRepository == null) throw new IllegalArgumentException("elasticsearchRepository is null");
		this.elasticsearchRepository = elasticsearchRepository;
	}

	public EntityMetaData getEntityMetaData()
	{
		return elasticsearchRepository.getEntityMetaData();
	}

	public <E extends Entity> Iterable<E> iterator(Class<E> clazz)
	{
		return elasticsearchRepository.iterator(clazz);
	}

	public int hashCode()
	{
		return elasticsearchRepository.hashCode();
	}

	public String getUrl()
	{
		return elasticsearchRepository.getUrl();
	}

	public long count()
	{
		return elasticsearchRepository.count();
	}

	public Query query()
	{
		return elasticsearchRepository.query();
	}

	public long count(Query q)
	{
		return elasticsearchRepository.count(q);
	}

	public Iterable<Entity> findAll(Query q)
	{
		return elasticsearchRepository.findAll(q);
	}

	public <E extends Entity> Iterable<E> findAll(Query q, Class<E> clazz)
	{
		return elasticsearchRepository.findAll(q, clazz);
	}

	public Entity findOne(Query q)
	{
		return elasticsearchRepository.findOne(q);
	}

	public Entity findOne(Object id)
	{
		return elasticsearchRepository.findOne(id);
	}

	public Iterable<Entity> findAll(Iterable<Object> ids)
	{
		return elasticsearchRepository.findAll(ids);
	}

	public <E extends Entity> Iterable<E> findAll(Iterable<Object> ids, Class<E> clazz)
	{
		return elasticsearchRepository.findAll(ids, clazz);
	}

	public <E extends Entity> E findOne(Object id, Class<E> clazz)
	{
		return elasticsearchRepository.findOne(id, clazz);
	}

	public <E extends Entity> E findOne(Query q, Class<E> clazz)
	{
		return elasticsearchRepository.findOne(q, clazz);
	}

	public Iterator<Entity> iterator()
	{
		return elasticsearchRepository.iterator();
	}

	public boolean equals(Object obj)
	{
		return elasticsearchRepository.equals(obj);
	}

	public void close() throws IOException
	{
		elasticsearchRepository.close();
	}

	public String getName()
	{
		return elasticsearchRepository.getName();
	}

	public AggregateResult aggregate(AggregateQuery aggregateQuery)
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
		try
		{
			return aggregateCache.get(aggregateQuery);
		}
		catch (ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void add(Entity entity)
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.add(entity);
	}

	public Integer add(Iterable<? extends Entity> entities)
	{
		aggregateCache.invalidateAll();
		return elasticsearchRepository.add(entities);
	}

	public void flush()
	{
		elasticsearchRepository.flush();
	}

	public void clearCache()
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.clearCache();
	}

	public void update(Entity entity)
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.update(entity);
	}

	public void update(Iterable<? extends Entity> entities)
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.update(entities);
	}

	public void delete(Entity entity)
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.delete(entity);
	}

	public void delete(Iterable<? extends Entity> entities)
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.delete(entities);
	}

	public void deleteById(Object id)
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.deleteById(id);
	}

	public void deleteById(Iterable<Object> ids)
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.deleteById(ids);
	}

	public void deleteAll()
	{
		aggregateCache.invalidateAll();
		elasticsearchRepository.deleteAll();
	}

	public String toString()
	{
		return elasticsearchRepository.toString();
	}
}
