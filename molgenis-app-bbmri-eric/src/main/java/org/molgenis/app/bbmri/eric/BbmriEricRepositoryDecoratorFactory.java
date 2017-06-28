package org.molgenis.app.bbmri.eric;

import org.molgenis.data.*;
import org.molgenis.data.aggregation.AggregateAnonymizer;
import org.molgenis.data.cache.l1.L1Cache;
import org.molgenis.data.cache.l2.L2Cache;
import org.molgenis.data.cache.l3.L3Cache;
import org.molgenis.data.elasticsearch.SearchService;
import org.molgenis.data.index.IndexActionRegisterService;
import org.molgenis.data.listeners.EntityListenersService;
import org.molgenis.data.platform.decorators.MolgenisRepositoryDecoratorFactory;
import org.molgenis.data.settings.AppSettings;
import org.molgenis.data.transaction.TransactionInformation;
import org.molgenis.data.validation.EntityAttributesValidator;
import org.molgenis.data.validation.ExpressionValidator;
import org.molgenis.data.validation.QueryValidator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import static java.util.Objects.requireNonNull;

@Primary
@Component
public class BbmriEricRepositoryDecoratorFactory extends MolgenisRepositoryDecoratorFactory
{
	private final CollectionsQueryTransformer collectionsQueryTransformer;

	public BbmriEricRepositoryDecoratorFactory(EntityManager entityManager,
			EntityAttributesValidator entityAttributesValidator, AggregateAnonymizer aggregateAnonymizer,
			AppSettings appSettings, DataService dataService, ExpressionValidator expressionValidator,
			SystemRepositoryDecoratorRegistry repositoryDecoratorRegistry,
			IndexActionRegisterService indexActionRegisterService, SearchService searchService, L1Cache l1Cache,
			L2Cache l2Cache, TransactionInformation transactionInformation,
			EntityListenersService entityListenersService, L3Cache l3Cache,
			PlatformTransactionManager transactionManager, QueryValidator queryValidator,
			CollectionsQueryTransformer collectionsQueryTransformer)
	{
		super(entityManager, entityAttributesValidator, aggregateAnonymizer, appSettings, dataService,
				expressionValidator, repositoryDecoratorRegistry, indexActionRegisterService, searchService, l1Cache,
				l2Cache, transactionInformation, entityListenersService, l3Cache, transactionManager, queryValidator);
		this.collectionsQueryTransformer = requireNonNull(collectionsQueryTransformer);
	}

	@Override
	public Repository<Entity> createDecoratedRepository(Repository<Entity> repository)
	{
		Repository<Entity> decoratedRepository = super.createDecoratedRepository(repository);
		return new CollectionsRepositoryDecorator(decoratedRepository, collectionsQueryTransformer);
	}
}
