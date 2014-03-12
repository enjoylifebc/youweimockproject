package com.mock.trading.web;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.xml.XmlConfiguration;

import com.sun.jersey.spi.container.servlet.ServletContainer;

public class JerseyMain
{

	public void init()
	{
		Server server = new Server(9998);

		XmlConfiguration configuration;
		try
		{
			configuration = new XmlConfiguration(new File("jetty.xml").toURL());
			configuration.configure(server);

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
			context.setContextPath("/");
			server.setHandler(context);

			ServletHolder h = new ServletHolder(new ServletContainer());
			h.setInitParameter("com.sun.jersey.config.property.packages", "com.mock.trading.web");
			context.addServlet(h, "/*");
			server.start();
			server.join();
			
		}
		catch ( Exception e1)
		{
			e1.printStackTrace();
		}

	}
}