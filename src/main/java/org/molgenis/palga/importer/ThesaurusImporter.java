package org.molgenis.palga.importer;

import static java.util.stream.StreamSupport.stream;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.molgenis.data.CrudRepository;
import org.molgenis.data.DataService;
import org.molgenis.data.DatabaseAction;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.importer.EntitiesValidationReportImpl;
import org.molgenis.data.importer.ImportService;
import org.molgenis.data.support.MapEntity;
import org.molgenis.data.support.TransformedEntity;
import org.molgenis.framework.db.EntitiesValidationReport;
import org.molgenis.framework.db.EntityImportReport;
import org.molgenis.palga.meta.DiagnosisMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

@Service
public class ThesaurusImporter implements ImportService
{
	private static final Logger LOG = LoggerFactory.getLogger(ThesaurusImporter.class);
	private final DataService dataService;

	@Autowired
	public ThesaurusImporter(DataService dataService)
	{
		this.dataService = dataService;
	}

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public EntityImportReport doImport(RepositoryCollection source, DatabaseAction databaseAction)
	{
		LOG.info("Importing theasurus...");

		EntityImportReport report = new EntityImportReport();

		for (String entityName : source.getEntityNames())
		{
			if (entityName.equalsIgnoreCase(DiagnosisMetaData.INSTANCE.getName()))
			{
				Collection<Entity> entitiesToImport = preprocess(source
						.getRepositoryByEntityName(DiagnosisMetaData.INSTANCE.getName()));

				// Use transformed entities
				entitiesToImport = entitiesToImport.stream()
						.map(e -> new TransformedEntity(e, DiagnosisMetaData.INSTANCE, dataService))
						.collect(Collectors.toList());

				CrudRepository repo = (CrudRepository) dataService.getRepositoryByEntityName(DiagnosisMetaData.INSTANCE
						.getName());

				switch (databaseAction)
				{
					case ADD:
						Integer count = repo.add(entitiesToImport);
						report.addEntityCount(DiagnosisMetaData.INSTANCE.getName(), count);
						break;

					case ADD_UPDATE_EXISTING:
						List<Entity> entitiesNew = new ArrayList<>();
						List<Entity> entitiesUpdate = new ArrayList<>();

						entitiesToImport.forEach(e -> {
							String id = e.getString(DiagnosisMetaData.ATTR_DEPALCE);
							if (id == null) throw new MolgenisDataException("Missing id value");
							if (repo.findOne(id) == null)
							{
								entitiesNew.add(e);
							}
							else
							{
								entitiesUpdate.add(e);
							}
						});

						if (!entitiesNew.isEmpty())
						{
							Integer c = repo.add(entitiesNew);
							report.addEntityCount(DiagnosisMetaData.INSTANCE.getName(), c);
						}

						if (!entitiesUpdate.isEmpty())
						{
							repo.update(entitiesUpdate);
							report.addEntityCount(DiagnosisMetaData.INSTANCE.getName(), entitiesUpdate.size());
						}
						break;

					case UPDATE:
						repo.update(entitiesToImport);
						report.addEntityCount(DiagnosisMetaData.INSTANCE.getName(), entitiesToImport.size());
						break;

					default:
						break;

				}

			}
		}

		LOG.info("Thesaurus import done.");

		return report;
	}

	@Override
	public EntitiesValidationReport validateImport(File file, RepositoryCollection source)
	{
		EntitiesValidationReport report = new EntitiesValidationReportImpl();
		Iterator<String> it = source.getEntityNames().iterator();
		if (it.hasNext())
		{
			String entityName = it.next();

			EntityMetaData sourceEntityMetaData = source.getRepositoryByEntityName(entityName).getEntityMetaData();
			List<String> sourceAttrs = stream(sourceEntityMetaData.getAtomicAttributes().spliterator(), false).map(
					attr -> attr.getName().toLowerCase()).collect(Collectors.toList());

			if (sourceEntityMetaData.getName().equalsIgnoreCase(entityName))
			{
				report.getSheetsImportable().put(entityName, true);

				// All attributes
				List<String> attributes = stream(DiagnosisMetaData.INSTANCE.getAtomicAttributes().spliterator(), false)
						.map(attr -> attr.getName().toLowerCase()).collect(Collectors.toList());

				// Missing required Attributes
				List<String> missing = stream(DiagnosisMetaData.INSTANCE.getAtomicAttributes().spliterator(), false)
						.filter(attr -> !attr.isNillable()).map(attr -> attr.getName())
						.filter(attrName -> !sourceAttrs.contains(attrName.toLowerCase())).collect(Collectors.toList());
				report.getFieldsRequired().put(entityName, missing);

				// Available Attributes
				List<String> availableAttributeNames = stream(sourceEntityMetaData.getAtomicAttributes().spliterator(),
						false).map(attr -> attr.getName())
						.filter(attrName -> attributes.contains(attrName.toLowerCase())).collect(Collectors.toList());
				report.getFieldsImportable().put(entityName, availableAttributeNames);

				// Not importable attributes
				List<String> unknown = stream(sourceEntityMetaData.getAtomicAttributes().spliterator(), false)
						.map(attr -> attr.getName()).filter(attrName -> !attributes.contains(attrName.toLowerCase()))
						.collect(Collectors.toList());
				report.getFieldsUnknown().put(entityName, unknown);
			}
		}

		return report;
	}

	@Override
	public boolean canImport(File file, RepositoryCollection source)
	{
		for (String name : source.getEntityNames())
		{
			if (name.equalsIgnoreCase(DiagnosisMetaData.INSTANCE.getName())) return true;
		}

		return false;
	}

	@Override
	public List<DatabaseAction> getSupportedDatabaseActions()
	{
		return Arrays.asList(DatabaseAction.ADD, DatabaseAction.ADD_UPDATE_EXISTING, DatabaseAction.UPDATE);
	}

	@Override
	public boolean getMustChangeEntityName()
	{
		return false;
	}

	// Concatenate description with the same code (DEPALCE)
	private Collection<Entity> preprocess(Iterable<Entity> entities)
	{
		Map<String, Entity> entitiesByCode = new LinkedHashMap<>();
		for (Entity entity : entities)
		{
			String code = entity.getString(DiagnosisMetaData.ATTR_DEPALCE);
			Entity e = entitiesByCode.get(code);
			if (e != null)
			{
				String description = String.format("%s,%s", e.getString(DiagnosisMetaData.ATTR_DETEROM),
						entity.getString(DiagnosisMetaData.ATTR_DETEROM));
				e.set(DiagnosisMetaData.ATTR_DETEROM, description);
			}
			else
			{
				// Use MapEntity because ExcelEntity is not editable
				entitiesByCode.put(code, new MapEntity(entity, DiagnosisMetaData.INSTANCE));
			}
		}

		return entitiesByCode.values();
	}

}
