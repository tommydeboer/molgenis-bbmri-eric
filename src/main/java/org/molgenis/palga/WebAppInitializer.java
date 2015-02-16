package org.molgenis.palga;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.molgenis.ui.MolgenisWebAppInitializer;
import org.springframework.web.WebApplicationInitializer;

public class WebAppInitializer extends MolgenisWebAppInitializer implements WebApplicationInitializer
{
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException
	{
		super.onStartup(servletContext, WebAppConfig.class, false, Integer.MAX_VALUE);

		servletContext.addFilter("IndexingActiveMessageFilter", new IndexingActiveMessageFilter())
				.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/menu/main/dataexplorer");
	}
}