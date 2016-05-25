package com.gcm.gottacatchme.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.gcm.gottacatchme.config.WebAppConfig;

public class WebAppInitializer extends AbstractContextLoaderInitializer
{
	@Override
	protected WebApplicationContext createRootApplicationContext()
	{
		AnnotationConfigWebApplicationContext webAppContext = new AnnotationConfigWebApplicationContext();
		webAppContext.register(WebAppConfig.class);
		return webAppContext;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException
	{
		super.onStartup(servletContext);
		servletContext.addServlet("cxf", new CXFServlet()).addMapping("/*");
	}

}
