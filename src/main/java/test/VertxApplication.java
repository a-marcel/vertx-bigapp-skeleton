package test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
import test.codec.request.HttpRequest;
import test.codec.response.HttpResponse;

public class VertxApplication extends AbstractVerticle {

	static Logger logger = LoggerFactory.getLogger(VertxApplication.class);

	public static void main(String[] args) {
		Launcher.main(new String[] { "run", "service:" + VertxApplication.class.getName() });
	}

	@Override
	public void start() throws Exception {
		logger.debug("starting");

		JsonArray verticals = null;
		if (config().containsKey("verticals")) {
			verticals = config().getJsonArray("verticals");
		}

		List< Future > allVerticalFuture = new ArrayList< Future >();

		List< JsonArray > routes = new ArrayList< JsonArray >();

		if (null != verticals) {
			for (Object object : verticals) {
				if (object instanceof JsonObject) {
					JsonObject options = (JsonObject) object;

					if (options.containsKey("routes")) {
						routes.addAll(options.getJsonArray("routes").getList());
					}

					Future< String > verticleFuture = Future.future();

					DeploymentOptions deploymentOptions = new DeploymentOptions();
					deploymentOptions.setConfig(options);

					vertx.deployVerticle("service:"
							+ options.getString("name"), deploymentOptions, verticleFuture.completer());

					allVerticalFuture.add(verticleFuture);
				}
			}
		}

		if (allVerticalFuture.size() > 0) {
			CompositeFuture.all(allVerticalFuture).setHandler(ar ->
			{
				if (ar.succeeded()) {
					logger.info("All verticals successfull deployed");
				} else {
					logger.error("Deploying verticals failed", ar.cause());
				}
			});
		}

		if (config().containsKey("serverServiceName")) {
			String serverVerticleName = config().getString("serverServiceName");

			DeploymentOptions options = new DeploymentOptions();
			options.setConfig(new JsonObject().put("routes", routes));

			vertx.deployVerticle("service:" + serverVerticleName, options);
		}
	}
}
