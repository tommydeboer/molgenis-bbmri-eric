package org.molgenis.palga.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.molgenis.data.CrudRepository;
import org.molgenis.data.DataService;
import org.molgenis.data.DatabaseAction;
import org.molgenis.data.Entity;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.support.MapEntity;
import org.molgenis.data.support.TransformedEntity;
import org.molgenis.framework.db.EntityImportReport;
import org.molgenis.palga.meta.DiagnosisMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThesaurusImporter extends AbstractImportService
{
	private static final Logger LOG = LoggerFactory.getLogger(ThesaurusImporter.class);
	private final DataService dataService;

	@Autowired
	public ThesaurusImporter(DataService dataService)
	{
		super(DiagnosisMetaData.INSTANCE);
		this.dataService = dataService;
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
