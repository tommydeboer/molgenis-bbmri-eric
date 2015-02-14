//package org.molgenis.palga.importer;
//
//import static java.util.stream.StreamSupport.stream;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.elasticsearch.common.collect.Iterables;
//import org.molgenis.data.CrudRepository;
//import org.molgenis.data.DataService;
//import org.molgenis.data.DatabaseAction;
//import org.molgenis.data.Entity;
//import org.molgenis.data.MolgenisDataException;
//import org.molgenis.data.RepositoryCollection;
//import org.molgenis.data.support.TransformedEntity;
//import org.molgenis.framework.db.EntityImportReport;
//import org.molgenis.palga.meta.RetrievaltermMetaData;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class RetrievalTermImporter extends AbstractImportService
//{
//	private static final Logger LOG = LoggerFactory.getLogger(RetrievalTermImporter.class);
//	private final DataService dataService;
//
//	@Autowired
//	public RetrievalTermImporter(DataService dataService)
//	{
//		super(RetrievaltermMetaData.INSTANCE);
//		this.dataService = dataService;
//	}
//
//	@Override
//	public EntityImportReport doImport(RepositoryCollection source, DatabaseAction databaseAction)
//	{
//		LOG.info("Importing retrievalterms...");
//
//		EntityImportReport report = new EntityImportReport();
//
//		for (String entityName : source.getEntityNames())
//		{
//			if (entityName.equalsIgnoreCase(RetrievaltermMetaData.INSTANCE.getName()))
//			{
//				Iterable<Entity> entitiesToImport = source.getRepositoryByEntityName(RetrievaltermMetaData.INSTANCE
//						.getName());
//
//				// Use transformed entities
//				entitiesToImport = stream(entitiesToImport.spliterator(), false).map(
//						e -> new TransformedEntity(e, RetrievaltermMetaData.INSTANCE, dataService)).collect(
//						Collectors.toList());
//
//				CrudRepository repo = (CrudRepository) dataService
//						.getRepositoryByEntityName(RetrievaltermMetaData.INSTANCE.getName());
//
//				switch (databaseAction)
//				{
//					case ADD:
//						Integer count = repo.add(entitiesToImport);
//						report.addEntityCount(RetrievaltermMetaData.INSTANCE.getName(), count);
//						break;
//
//					case ADD_UPDATE_EXISTING:
//						List<Entity> entitiesNew = new ArrayList<>();
//						List<Entity> entitiesUpdate = new ArrayList<>();
//
//						entitiesToImport.forEach(e -> {
//							String id = e.getString(RetrievaltermMetaData.ATTR_ID);
//							if (id == null) throw new MolgenisDataException("Missing id value");
//							if (repo.findOne(id) == null)
//							{
//								entitiesNew.add(e);
//							}
//							else
//							{
//								entitiesUpdate.add(e);
//							}
//						});
//
//						if (!entitiesNew.isEmpty())
//						{
//							Integer c = repo.add(entitiesNew);
//							report.addEntityCount(RetrievaltermMetaData.INSTANCE.getName(), c);
//						}
//
//						if (!entitiesUpdate.isEmpty())
//						{
//							repo.update(entitiesUpdate);
//							report.addEntityCount(RetrievaltermMetaData.INSTANCE.getName(), entitiesUpdate.size());
//						}
//						break;
//
//					case UPDATE:
//						repo.update(entitiesToImport);
//						report.addEntityCount(RetrievaltermMetaData.INSTANCE.getName(),
//								Iterables.size(entitiesToImport));
//						break;
//
//					default:
//						break;
//
//				}
//
//			}
//		}
//
//		LOG.info("Retrievalterm import done.");
//
//		return report;
//	}
// }
