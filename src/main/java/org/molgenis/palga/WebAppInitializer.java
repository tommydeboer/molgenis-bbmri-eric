package org.molgenis.palga;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.molgenis.ui.MolgenisWebAppInitializer;
import org.springframework.web.WebApplicationInitializer;

public class WebAppInitializer extends MolgenisWebAppInitializer implements WebApplicationInitializer
{
	private static final int MAX_FILE_SIZE = 1024;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException
	{
		super.onStartup(servletContext, WebAppConfig.class, false, MAX_FILE_SIZE);

		servletContext.addFilter("IndexingActiveMessageFilter", new IndexingActiveMessageFilter())
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/menu/main/dataexplorer");
	}
}