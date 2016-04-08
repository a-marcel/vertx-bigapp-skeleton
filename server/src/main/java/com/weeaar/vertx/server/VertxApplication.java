package com.weeaar.vertx.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.weeaar.vertx.codec.request.HttpRequest;
import com.weeaar.vertx.codec.response.HttpResponse;
import com.weeaar.vertx.codec.service.ServiceLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Launcher;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.rxjava.core.CompositeFuture;
import io.vertx.rxjava.core.Future;
import io.vertx.service.ServiceVerticleFactory;

public abstract class VertxApplication extends AbstractVerticle {

	static Logger logger = LoggerFactory.getLogger(VertxApplication.class);

	public static void main(String[] args) {
		Launcher.main(new String[] { "run", "service:" + VertxApplication.class.getName() });
	}

	@Override
	public void start() throws Exception {
		logger.debug("starting");

		ServiceLoader serviceLoader = new ServiceLoader();
		List< String > verticals = serviceLoader.findVerticles();

		ServiceVerticleFactory factory = new ServiceVerticleFactory();

		List< Future > allVerticalFuture = new ArrayList< Future >();

		List< JsonObject > routes = new ArrayList< JsonObject >();

		if (null != verticals) {
			for (String name : verticals) {
				DeploymentOptions deploymentOptions = new DeploymentOptions();

				io.vertx.core.Future< String > configLoaderFuture = io.vertx.core.Future.future();
				configLoaderFuture.setHandler(resultString ->
				{
					if (resultString.succeeded()) {
						Future< String > verticleFuture = Future.future();

						DeploymentOptions x = deploymentOptions;
						if (null != deploymentOptions.getConfig()
								&& deploymentOptions.getConfig().containsKey("routes")) {
							for (Object route : deploymentOptions.getConfig().getJsonArray("routes")) {
								if (route instanceof JsonObject) {
									routes.add((JsonObject) route);
								}
							}
						}

						vertx.deployVerticle(name, deploymentOptions, verticleFuture.completer());

						allVerticalFuture.add(verticleFuture);
					}
				});

				factory.resolve(name, deploymentOptions, getClass().getClassLoader(), configLoaderFuture);
			}
		}

		/*
		 * if (null != verticals) {
		 * for (String name : verticals) {
		 * JsonObject options = (JsonObject) object;
		 * 
		 * if (options.containsKey("routes")) {
		 * routes.addAll(options.getJsonArray("routes").getList());
		 * }
		 * 
		 * Future< String > verticleFuture = Future.future();
		 * 
		 * DeploymentOptions deploymentOptions = new DeploymentOptions();
		 * deploymentOptions.setConfig(options);
		 * 
		 * vertx.deployVerticle(options.getString("name"), deploymentOptions,
		 * verticleFuture.completer());
		 * 
		 * allVerticalFuture.add(verticleFuture);
		 * }
		 * }
		 */

		if (allVerticalFuture.size() > 0) {
			CompositeFuture.all(allVerticalFuture).setHandler(ar ->
			{
				if (ar.succeeded()) {
					logger.info("All verticals successfull deployed");

					if (config().containsKey("serverService")) {
						JsonObject serverServiceOptions = config().getJsonObject("serverService");
						String serverVerticleName = serverServiceOptions.getString("name");

						JsonObject deployConfig = new JsonObject();
						deployConfig.put("routes", routes);

						DeploymentOptions options = new DeploymentOptions();

						if (serverServiceOptions.containsKey("config")) {
							JsonObject serverConfig = serverServiceOptions.getJsonObject("config");

							for (Map.Entry< String, Object > configObject : serverConfig.getMap().entrySet()) {
								deployConfig.put(configObject.getKey(), configObject.getValue());
							}

							options.setConfig(deployConfig);
						}

						vertx.deployVerticle(serverVerticleName, options);
					}

				} else {
					logger.error("Deploying verticals failed", ar.cause());
				}
			});
		}

	}
}
