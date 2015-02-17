package org.molgenis.palga.importer;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.Repository;
import org.molgenis.data.csv.CsvRepository;
import org.molgenis.data.csv.CsvRepositoryCollection;
import org.molgenis.data.processor.CellProcessor;

/**
 * Repository collection for Palga sample file
 */
public class PalgaSampleRepositoryCollection extends CsvRepositoryCollection
{
	private static final char SEPARATOR = '|';
	private final File psvFile;

	public PalgaSampleRepositoryCollection(File file, CellProcessor... cellProcessors) throws InvalidFormatException,
			IOException
	{
		super(file, cellProcessors);
		this.psvFile = file;
	}

	public PalgaSampleRepositoryCollection(File file) throws InvalidFormatException, IOException
	{
		super(file);
		this.psvFile = file;
	}

	public File getFile()
	{
		return psvFile;
	}

	@Override
	public Repository getRepositoryByEntityName(String name)
	{
		return new CsvRepository(psvFile, cellProcessors, SEPARATOR);
	}
}
