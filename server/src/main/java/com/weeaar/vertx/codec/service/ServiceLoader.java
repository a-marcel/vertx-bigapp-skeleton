package com.weeaar.vertx.codec.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.service.ServiceVerticleFactory;

public class ServiceLoader {

	Logger logger = LoggerFactory.getLogger(ServiceLoader.class);

	ServiceVerticleFactory factory;

	public List< String > findVerticles() {
		List< String > verticles = new ArrayList< String >();

		Properties prop = new Properties();
		InputStream input = null;
		Scanner scan = null;
		String filename = "META-INF/vertx.verticle";

		try {

			input = getClass().getClassLoader().getResourceAsStream(filename);

			if (input == null) {
				logger.debug("Could not found " + filename);

				return null;
			}

			scan = new Scanner(input);
			while (scan.hasNextLine()) {
				String split[] = scan.nextLine().trim().split("=");
				if (split.length == 2) {
					verticles.add(split[1]);
					logger.debug("Found verticle : " + split[1]);
				}
			}
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (scan != null) {
				scan.close();
			}
		}

		return verticles;
	}

	public DeploymentOptions loadOptions(String verticleName) {
		if (null == factory) {
			factory = new ServiceVerticleFactory();
		}

		DeploymentOptions deploymentOptions = new DeploymentOptions();

		Future< String > fut = Future.future();

		factory.resolve(verticleName, deploymentOptions, getClass().getClassLoader(), fut);

		if (fut.succeeded()) {
			return deploymentOptions;
		}
		return null;
	}
}
