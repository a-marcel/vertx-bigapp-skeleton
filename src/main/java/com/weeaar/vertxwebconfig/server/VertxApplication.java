package com.weeaar.vertxwebconfig.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.weeaar.vertxwebconfig.annotation.VertxWebConfig;
import com.weeaar.vertxwebconfig.service.ServiceLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class VertxApplication extends AbstractVerticle {
    static Logger logger = LoggerFactory.getLogger(VertxApplication.class);

    public static void main(String[] args) {
	Launcher.main(new String[] { "run", "service:" + VertxApplication.class.getName() });
    }

    @Override
    public void start() throws Exception {
	logger.debug("starting");

	ServiceLoader serviceLoader = new ServiceLoader();
	List<String> verticals = serviceLoader.findVerticles();

	@SuppressWarnings("rawtypes")
	List<Future> allVerticalFuture = new ArrayList<Future>();

	Config configs = new Config(config());

	if (null != verticals) {
	    for (String name : verticals) {
		/*
		 * Figure out routes
		 */

		if (!name.endsWith(".js")) {
		    Method[] methods = Class.forName(name).getMethods();
		    for (Method method : methods) {
			if (method.isAnnotationPresent(VertxWebConfig.class)) {
			    VertxWebConfig vertxWebConfig = method.getAnnotation(VertxWebConfig.class);

			    configs.addConfigFromAnnotation(vertxWebConfig);
			}
		    }
		} else if (name.endsWith(".js")) {
		    /*
		     * For JavaScript, we need a json config file
		     */
		    /*
		     * proposal
		     */
		    /*
		     * TODO: make it non blocking
		     */
		    Scanner scan = null;
		    JsonObject configObject = null;

		    try {
			String javaScriptConfigFile = name.substring(0, name.length() - 3);
			Enumeration<URL> urls = ClassLoader
				.getSystemResources(javaScriptConfigFile.concat(".config.json"));

			while (urls.hasMoreElements()) {
			    URL url = urls.nextElement();

			    scan = new Scanner(url.openStream());
			    String configJson = "";
			    while (scan.hasNextLine()) {
				configJson += scan.nextLine();
			    }

			    configObject = new JsonObject(configJson);
			}
		    } catch (IOException e) {
			logger.error("Error at opening file", e);
		    } catch (DecodeException e) {
			logger.error("Error at decoding file", e);
		    } finally {
			if (scan != null) {
			    scan.close();
			}
		    }

		    configs.addConfigFromJsonObject(configObject);
		}

		/*
		 * Starting verticle
		 */
		Future<String> verticleFuture = Future.future();

		logger.info("Deploy verticle " + name);

		DeploymentOptions options = new DeploymentOptions();
		options.setConfig(configs);

		vertx.deployVerticle(name, options, verticleFuture.completer());
		allVerticalFuture.add(verticleFuture);
	    }
	}

	if (allVerticalFuture.size() > 0) {
	    CompositeFuture.all(allVerticalFuture).setHandler(ar -> {
		if (ar.succeeded()) {
		    logger.info("All verticals successfull deployed");

		    startServerVerticle(configs);
		} else {
		    logger.error("Deploying verticals failed", ar.cause());
		}
	    });
	} else {
	    startServerVerticle(configs);
	}
    }

    void startServerVerticle(Config config) {
	if (null != config.getServerConfig()) {

	    ServerConfig serverConfig = config.getServerConfig();

	    DeploymentOptions options = new DeploymentOptions();
	    options.setConfig(config);

	    vertx.deployVerticle(serverConfig.getClassName(), options);
	}
    }
}
