package org.molgenis.app.bbmri.eric;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.QueryRule;
import org.molgenis.data.meta.model.Attribute;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.QueryImpl;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Component
public class CollectionsQueryTransformerImpl implements CollectionsQueryTransformer
{
	private static final String COLLECTIONS_ID = "eu_bbmri_eric_collections";
	private static final String DISEASE_TYPES_ENTITY_ID = "eu_bbmri_eric_disease_types";
	private static final String DISEASE_TYPES_ATTRIBUTE_CODE = "code";
	private static final String DISEASE_TYPES_ATTRIBUTE_LABEL = "label";
	private static final String DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE = "diagnosis_available";

	private final Icd10ClassExpander icd10ClassExpander;
	private final DataService dataService;

	public CollectionsQueryTransformerImpl(Icd10ClassExpander icd10ClassExpander,
			DataService dataService)
	{
		this.icd10ClassExpander = requireNonNull(icd10ClassExpander);
		this.dataService = requireNonNull(dataService);
	}

	@Override
	public Query<Entity> transformQuery(Query<Entity> query)
	{
		Query<Entity> transformedQuery;
		if (isTransformableQuery(query))
		{
			String queryValue = query.getRules().get(0).getValue().toString();

			List<Entity> diseaseTypes = findDiseaseTypes(queryValue).collect(toList());
			if (!diseaseTypes.isEmpty())
			{
				transformedQuery = createTransformedQuery(query, queryValue, diseaseTypes);
			}
			else
			{
				transformedQuery = query;
			}
		}
		else
		{
			transformedQuery = query;
		}
		return transformedQuery;
	}

	private Query<Entity> createTransformedQuery(Query<Entity> query, String queryValue, List<Entity> diseaseTypes)
	{
		Query<Entity> transformedQuery = new QueryImpl<>();
		transformedQuery.pageSize(query.getPageSize());
		transformedQuery.offset(query.getOffset());
		transformedQuery.sort(query.getSort());
		transformedQuery.fetch(query.getFetch());

		getSearchableAttributeNames().forEach(attrName -> transformedQuery.search(attrName, queryValue).or());

		Collection<Entity> expandedDiseaseTypes = expandDiseaseTypes(diseaseTypes);
		transformedQuery.in(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE, expandedDiseaseTypes);
		return transformedQuery;
	}

	/**
	 * Returns the attributes of the BBMRI-ERIC collections entity type that can be used in a query with operator SEARCH.
	 */
	private Stream<String> getSearchableAttributeNames()
	{
		EntityType entityType = dataService.getEntityType(COLLECTIONS_ID);
		return StreamSupport.stream(entityType.getAtomicAttributes().spliterator(), false)
				.filter(this::isSearchableAttribute).map(Attribute::getName);
	}

	/**
	 * Returns whether the given attribute can be used in a query with operator SEARCH.
	 */
	private boolean isSearchableAttribute(Attribute attribute)
	{
		if (attribute.getName().equals(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE))
		{
			return false;
		}

		switch (attribute.getDataType())
		{
			case BOOL:
			case DATE:
			case DATE_TIME:
			case DECIMAL:
			case INT:
			case LONG:
				return false;
			default:
				return true;
		}
	}

	/**
	 * Returns <code>true</code> if query has one rule 'SEARCH sometext'
	 */
	private boolean isTransformableQuery(Query<Entity> q)
	{
		return q.getRules() != null && q.getRules().size() == 1
				&& q.getRules().get(0).getOperator() == QueryRule.Operator.SEARCH
				&& q.getRules().get(0).getField() == null;
	}

	/**
	 * Find disease type entities that match the given query text.
	 */
	private Stream<Entity> findDiseaseTypes(String queryValue)
	{
		return dataService.query(DISEASE_TYPES_ENTITY_ID).eq(DISEASE_TYPES_ATTRIBUTE_CODE, queryValue).or()
				.search(DISEASE_TYPES_ATTRIBUTE_LABEL, queryValue).findAll();
	}

	/**
	 * Expand disease type entities with all their children, grandchildren, etc.
	 */
	private Collection<Entity> expandDiseaseTypes(List<Entity> diseaseTypes)
	{
		return icd10ClassExpander.expandClasses(diseaseTypes);
	}
}
