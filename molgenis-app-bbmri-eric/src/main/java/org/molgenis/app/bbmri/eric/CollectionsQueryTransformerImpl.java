package org.molgenis.app.bbmri.eric;

import com.google.common.collect.TreeTraverser;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.Query;
import org.molgenis.data.QueryRule;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

@Component
public class CollectionsQueryTransformerImpl implements CollectionsQueryTransformer
{
	static final String DISEASE_TYPES_ENTITY_ID = "eu_bbmri_eric_disease_types";
	static final String DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE = "diagnosis_available";

	private static final TreeTraverser<QueryRule> RULE_TRAVERSER = TreeTraverser.using(QueryRule::getNestedRules);

	private final Icd10ClassExpander icd10ClassExpander;
	private final DataService dataService;

	CollectionsQueryTransformerImpl(Icd10ClassExpander icd10ClassExpander, DataService dataService)
	{
		this.icd10ClassExpander = requireNonNull(icd10ClassExpander);
		this.dataService = requireNonNull(dataService);
	}

	@Override
	public Query<Entity> transformQuery(Query<Entity> query)
	{
		if (query != null && query.getRules() != null && !query.getRules().isEmpty())
		{
			query.getRules()
				 .forEach(rule -> RULE_TRAVERSER.preOrderTraversal(rule)
												.filter(this::isTransformableRule)
												.forEach(this::transformQueryRule));
		}

		return query;
	}

	private void transformQueryRule(QueryRule rule)
	{
		List<Object> queryValues;

		switch (rule.getOperator())
		{
			case EQUALS:
				queryValues = singletonList(rule.getValue());
				rule.setOperator(QueryRule.Operator.IN);
				break;
			case IN:
				//noinspection unchecked
				queryValues = (List<Object>) rule.getValue();
				break;
			default:
				throw new IllegalStateException("Can't expand queries other than IN or EQUALS");
		}

		List<Entity> diseaseTypes = dataService.findAll(DISEASE_TYPES_ENTITY_ID, queryValues.stream())
											   .collect(toList());

		rule.setValue(expandDiseaseTypes(diseaseTypes));
	}

	/**
	 * Returns <code>true</code> if a rule is 'IN' or 'EQUALS' on the diagnosis_available attribute
	 */
	private boolean isTransformableRule(QueryRule nestedRule)
	{
		return nestedRule.getField() != null && nestedRule.getField()
														  .equals(DISEASE_TYPES_ATTRIBUTE_DIAGNOSIS_AVAILABLE) && (
				nestedRule.getOperator() == QueryRule.Operator.IN
						|| nestedRule.getOperator() == QueryRule.Operator.EQUALS);
	}

	/**
	 * Expand disease type entities with all their children, grandchildren, etc.
	 */
	private Collection<Entity> expandDiseaseTypes(List<Entity> diseaseTypes)
	{
		return icd10ClassExpander.expandClasses(diseaseTypes);
	}
}
