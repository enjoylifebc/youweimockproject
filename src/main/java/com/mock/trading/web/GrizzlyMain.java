package com.mock.trading.web;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class GrizzlyMain {
	static Logger logger = LoggerFactory.getLogger(GrizzlyMain.class);

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost/").port(9998).build();
	}

	public static final URI BASE_URI = getBaseURI();

	public static void start() {
		logger.info("Starting grizzly webservice ...");
		try {
			ResourceConfig rc = new PackagesResourceConfig(
					"com.mock.trading.web");
			GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
			logger.info(String.format(
					"Jersey app started with WADL available at "
							+ "%sapplication.wadl\n ", BASE_URI, BASE_URI));
			System.in.read();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new GrizzlyMain().start();
	}
}