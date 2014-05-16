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
import org.molgenis.palga.RetrievalTerm;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetrievalTermImporter
{
	private static final String IDENTIFIER_COLUMN = "Termnr";
	private static final String DESCRIPTION_COLUMN = "Retrievalterm";
	private static final String SHEET_NAME = "Sheet1";
	private static final Logger logger = Logger.getLogger(RetrievalTermImporter.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Async
	@Transactional
	public void importFile(File file) throws InvalidFormatException, IOException
	{
		long t0 = System.currentTimeMillis();
		String fileName = file.getAbsolutePath();
		logger.info("Going to import RetievalItem file [" + fileName + "]");

		RepositoryCollection source = new ExcelRepositoryCollection(file);
		Repository repo = null;
		try
		{
			repo = source.getRepositoryByEntityName(SHEET_NAME);

			long count = 0;
			for (Entity entity : repo)
			{
				String identifier = entity.getString(IDENTIFIER_COLUMN);
				String description = entity.getString(DESCRIPTION_COLUMN);

				RetrievalTerm term = findRetrievalItem(identifier);
				if (term == null)
				{
					term = new RetrievalTerm();
					term.setIdentifier(identifier);
					term.setDescription(description);
					entityManager.persist(term);
					entityManager.flush();
				}
				else
				{
					// Update description if needed
					if (!term.getDescription().equals(description))
					{
						term.setDescription(description);
						entityManager.merge(term);
						entityManager.flush();
					}
				}

				count++;
			}

			long t = System.currentTimeMillis() - t0;
			logger.info("Import of RetrievalItem file [" + fileName + "] completed in " + t + " msec. Total [" + count
					+ "] items");
		}
		catch (Exception e)
		{
			logger.error("Exception importing thesaurus file [" + fileName + "] ", e);
			throw e;
		}
		finally
		{
			IOUtils.closeQuietly(repo);
		}
	}

	private RetrievalTerm findRetrievalItem(String identifier)
	{
		TypedQuery<RetrievalTerm> q = entityManager.createQuery(
				"SELECT r FROM RetrievalTerm r WHERE r.identifier = :identifier", RetrievalTerm.class);
		List<RetrievalTerm> result = q.setParameter("identifier", identifier).getResultList();
		if (result.isEmpty())
		{
			return null;
		}

		return result.get(0);
	}
}
