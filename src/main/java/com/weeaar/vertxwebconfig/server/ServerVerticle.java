package com.weeaar.vertxwebconfig.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.weeaar.vertxwebconfig.codec.request.HttpRequest;
import com.weeaar.vertxwebconfig.codec.request.HttpRequestCodec;
import com.weeaar.vertxwebconfig.codec.response.HttpResponse;
import com.weeaar.vertxwebconfig.codec.response.HttpResponseCodec;
import com.weeaar.vertxwebconfig.hooks.WebRouterHook;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class ServerVerticle extends AbstractVerticle {
    Logger logger = LoggerFactory.getLogger(ServerVerticle.class);

    static String ROUTER_WEBHOOK_BEFORE = "before";

    static String ROUTER_WEBHOOK_AFTER = "after";

    WebRouterHook webRouterHook = null;

    JsonObject defaultHeader = new JsonObject();

    @Override
    public void start(Future<Void> fut) throws Exception {
	super.start();

	vertx.eventBus().registerDefaultCodec(HttpRequest.class, new HttpRequestCodec());
	vertx.eventBus().registerDefaultCodec(HttpResponse.class, new HttpResponseCodec());

	if (config().containsKey("webRouterHookClass")) {
	    initWebRouterHook(config().getString("webRouterHookClass"));
	}

	if (config().containsKey("contentType")) {
	    defaultHeader.put(HttpHeaders.CONTENT_TYPE.toString(), config().getString("contentType"));
	}

	@SuppressWarnings("rawtypes")
	List<Future> allServerFuture = new ArrayList<Future>();

	Integer port = config().getInteger("http.port", 8081);
	String host = config().getString("http.host", "localhost");

	JsonObject portConfig = config();

	if (portConfig.containsKey("portWithRoutes")) {
	    logger.debug("Starting with Config: " + portConfig.getJsonObject("portWithRoutes"));

	    for (Map.Entry<String, Object> entry : portConfig.getJsonObject("portWithRoutes").getMap().entrySet()) {
		Future<Void> serverFuture = Future.future();

		if (entry.getValue() instanceof JsonObject) {
		    JsonObject config = (JsonObject) entry.getValue();

		    if (entry.getKey().equals("DEFAULT")) {
			startServerOnPort(port, host, config, serverFuture);
		    } else {
			Integer newPort = Integer.valueOf(entry.getKey());
			startServerOnPort(newPort, host, config, serverFuture);
		    }
		} else {
		    logger.error("Configuration is wrong");
		}

		allServerFuture.add(serverFuture);
	    }
	} else {
	    logger.error("Wrong Configuration");
	}

	if (allServerFuture.size() > 0) {
	    CompositeFuture.all(allServerFuture).setHandler(ar -> {
		if (ar.succeeded()) {
		    fut.complete();
		} else {
		    fut.fail(ar.cause());
		}
	    });
	} else {
	    fut.complete();
	}
    }

    void initWebRouterHook(String webRouterHookClass) {
	try {
	    Class<?> webRouterHookClazz = Class.forName(webRouterHookClass);

	    if (WebRouterHook.class.isAssignableFrom(webRouterHookClazz)) {
		try {
		    webRouterHook = (WebRouterHook) webRouterHookClazz.newInstance();
		} catch (InstantiationException e) {
		    logger.error("Cannot instantiate webRouterHook", e);
		} catch (IllegalAccessException e) {
		    logger.error("Cannot instantiate webRouterHook", e);
		}
	    } else {
		logger.error("Invalid webRouterHook class (don't implements WebRouterHook)");
	    }
	} catch (ClassNotFoundException e) {
	    logger.error("Cannot found webRouterHook class " + webRouterHookClass, e);
	}
    }

    void startServerOnPort(Integer port, String host, JsonObject config, Future<Void> fut) {
	HttpServer server = vertx.createHttpServer();

	Router router = Router.router(vertx);

	callRouterWebHook(ROUTER_WEBHOOK_BEFORE, port, router);

	if (config.containsKey("routes")) {
	    parseRouteConfig(config.getJsonArray("routes"), router, host.concat(":").concat(port.toString()));
	}

	callRouterWebHook(ROUTER_WEBHOOK_AFTER, port, router);

	server.requestHandler(router::accept);

	server.listen(port, host, result -> {
	    if (result.succeeded()) {
		logger.info("Web Server listen on port " + port);
		fut.complete();
	    } else {
		logger.error("Web server not stated: " + result.cause().getMessage());
		fut.fail(result.cause());
	    }
	});
    }

    void callRouterWebHook(String type, Integer port, Router router) {
	if (null != webRouterHook) {
	    if (type.equals(ROUTER_WEBHOOK_BEFORE)) {
		logger.info("Calling webRouterHook before");
		webRouterHook.before(port, router);
	    } else if (type.equals(ROUTER_WEBHOOK_AFTER)) {
		logger.info("Calling webRouterHook after");
		webRouterHook.after(port, router);
	    }
	}
    }

    void parseRouteConfig(JsonArray routes, Router router, String hostInfo) {
	JsonObject defaultRoute = null;

	for (Object object : routes) {
	    if (object instanceof JsonObject) {
		JsonObject route = (JsonObject) object;

		if (route.containsKey("path") && route.getString("path").equals("/")) {
		    defaultRoute = route;
		    continue;
		}

		if (route.containsKey("path") && route.containsKey("channelName")) {
		    logger.info("Binding url " + route.getString("path") + " -> " + hostInfo);
		    router.route(route.getString("path"))
			    .handler(new RouterHandler(route.getString("channelName"), defaultHeader));
		} else if (route.containsKey("pathRegex") && route.containsKey("channelName")) {
		    logger.info("Binding url " + route.getString("path"));

		    router.route().pathRegex(route.getString("pathRegex"))
			    .handler(new RouterHandler(route.getString("channelName"), defaultHeader));
		} else {
		    logger.error("Cannot bind route because path or channelName is missing");
		}
	    }
	}
	/*
	 * Its important to bind the handling for / in the end
	 */

	if (null != defaultRoute) {
	    if (defaultRoute.containsKey("path") && defaultRoute.containsKey("channelName")) {
		logger.info("Binding url " + defaultRoute.getString("path") + " -> " + hostInfo);
		router.route(defaultRoute.getString("path"))
			.handler(new RouterHandler(defaultRoute.getString("channelName"), defaultHeader));
	    } else if (defaultRoute.containsKey("pathRegex") && defaultRoute.containsKey("channelName")) {
		logger.info("Binding url " + defaultRoute.getString("path"));

		router.route().pathRegex(defaultRoute.getString("pathRegex"))
			.handler(new RouterHandler(defaultRoute.getString("channelName"), defaultHeader));
	    } else {
		logger.error("Cannot bind route because path or channelName is missing");
	    }
	}
    }
}
