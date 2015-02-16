package org.molgenis.palga;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.molgenis.data.DataService;
import org.molgenis.data.importer.ImportRunMetaData;
import org.molgenis.util.ApplicationContextProvider;

public class IndexingActiveMessageFilter implements Filter
{
	private DataService dataService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException
	{

		if (dataService == null)
		{
			dataService = ApplicationContextProvider.getApplicationContext().getBean(DataService.class);
		}

		if (dataService.query(new ImportRunMetaData().getName()).eq("status", "RUNNING").count() > 0)
		{
			request.setAttribute("warningMessage", "Indexering actief. De getallen zijn niet accuraat!!!");
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{

	}

}
