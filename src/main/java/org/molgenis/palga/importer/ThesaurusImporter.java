package org.molgenis.palga.importer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.molgenis.data.Entity;
import org.molgenis.data.Repository;
import org.molgenis.data.RepositoryCollection;
import org.molgenis.data.excel.ExcelRepositoryCollection;
import org.molgenis.palga.Diagnosis;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Imports the Thesaurus file into the Description table.
 * 
 * If an palga code is encountered that already exists, the description is concatinated.
 * 
 * TODO Now the Thesaurus file can only be imported once because when you import it a second time instead of updating,
 * the descriptions are concatenated
 */
@Service
public class ThesaurusImporter
{
	private static final String IDENTIFIER_COLUMN = "DEPALCE";
	private static final String DESCRIPTION_COLUMN = "DETEROM";
	private static final String SHEET_NAME = "DRTPWRK";
	private static final Logger logger = Logger.getLogger(ThesaurusImporter.class);

	@PersistenceContext
	private EntityManager entityManager;

	public ThesaurusImporter()
	{
	}

	public ThesaurusImporter(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	@Async
	@Transactional
	public void importFile(File file) throws InvalidFormatException, IOException
	{
		long t0 = System.currentTimeMillis();
		String fileName = file.getAbsolutePath();
		logger.info("Going to import thesaurus file [" + fileName + "]");

		RepositoryCollection source = new ExcelRepositoryCollection(file);

		// entityManager.getTransaction().begin();

		Repository thesaurusRepo = null;
		try
		{
			thesaurusRepo = source.getRepositoryByEntityName(SHEET_NAME);

			long i = 0;
			for (Entity thesaurusEntity : thesaurusRepo)
			{
				String identifier = thesaurusEntity.getString(IDENTIFIER_COLUMN);
				String description = thesaurusEntity.getString(DESCRIPTION_COLUMN);

				Diagnosis diagnosis = findDiagnosis(identifier);

				if (diagnosis == null)
				{
					diagnosis = new Diagnosis();
					diagnosis.setIdentifier(identifier);
					diagnosis.setDescription(description);
					entityManager.persist(diagnosis);
				}
				else
				{
					// Concat descriptions
					description = String.format("%s,%s", diagnosis.getDescription(), description);
					diagnosis.setDescription(description);
					entityManager.merge(diagnosis);
				}

				entityManager.flush();

				if (i++ % 1000 == 0)
				{
					logger.info("Imported " + i + " rows");
				}
			}

			// entityManager.getTransaction().commit();

			long t = System.currentTimeMillis() - t0;
			logger.info("Import of thesaurus file [" + fileName + "] completed in " + t + " msec");
		}
		catch (Exception e)
		{
			logger.error("Exception importing thesaurus file [" + fileName + "] ", e);
			// entityManager.getTransaction().rollback();
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(thesaurusRepo);
		}
	}

	private Diagnosis findDiagnosis(String identifier)
	{
		TypedQuery<Diagnosis> q = entityManager.createQuery(
				"SELECT d FROM Diagnosis d WHERE d.identifier = :identifier", Diagnosis.class);
		List<Diagnosis> result = q.setParameter("identifier", identifier).getResultList();
		if (result.isEmpty())
		{
			return null;
		}

		return result.get(0);
	}
}
