package org.molgenis.app.bbmri.eric;

import org.molgenis.data.Entity;
import org.molgenis.data.Query;

/**
 * Transforms a query on the 'eu_bbmri_eric_collections' repository
 */
public interface CollectionsQueryTransformer
{
	Query<Entity> transformQuery(Query<Entity> query);
}
