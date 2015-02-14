package org.molgenis.palga.importer;

import static java.util.stream.StreamSupport.stream;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.molgenis.data.DatabaseAction;
import org.molgenis.data.EntityMetaData;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.importer.EntitiesValidationReportImpl;
import org.molgenis.data.importer.ImportService;
import org.molgenis.framework.db.EntitiesValidationReport;
import org.molgenis.framework.db.EntityImportReport;
import org.springframework.core.Ordered;

public abstract class AbstractImportService implements ImportService
{
	private final EntityMetaData entityMeta;

	@Override
	public int getOrder()
	{
		return Ordered.HIGHEST_PRECEDENCE;
	}

	public AbstractImportService(EntityMetaData entityMeta)
	{
		this.entityMeta = entityMeta;
	}

	@Override
	public abstract EntityImportReport doImport(RepositoryCollection source, DatabaseAction databaseAction);

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
				List<String> attributes = stream(entityMeta.getAtomicAttributes().spliterator(), false).map(
						attr -> attr.getName().toLowerCase()).collect(Collectors.toList());

				// Missing required Attributes
				List<String> missing = stream(entityMeta.getAtomicAttributes().spliterator(), false)
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
			if (name.equalsIgnoreCase(entityMeta.getName())) return true;
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

}
