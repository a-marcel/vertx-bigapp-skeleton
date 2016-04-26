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
import io.vertx.core.http.HttpMethod;
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

    @Override
    public void start(Future<Void> fut) throws Exception {
	super.start();

	vertx.eventBus().registerDefaultCodec(HttpRequest.class, new HttpRequestCodec());
	vertx.eventBus().registerDefaultCodec(HttpResponse.class, new HttpResponseCodec());

	Config config = new Config(config());

	if (null != config.getWebRouterHookClass()) {
	    initWebRouterHook(config.getWebRouterHookClass());
	}

	ServerConfig serverConfig = config.getServerConfig();

	@SuppressWarnings("rawtypes")
	List<Future> allServerFuture = new ArrayList<Future>();

	Integer port = serverConfig.getPort();
	String host = serverConfig.getHost();

	if (config.isValid()) {
	    logger.debug("Starting with Config: " + config);

	    for (Map.Entry<Integer, List<Route>> routes : config.getRoutesConfig().entrySet()) {
		Future<Void> serverFuture = Future.future();

		startServerOnPort(routes.getKey(), host, routes.getValue(), serverFuture);
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

    void startServerOnPort(Integer port, String host, List<Route> routes, Future<Void> fut) {
	HttpServer server = vertx.createHttpServer();

	Router router = Router.router(vertx);

	callRouterWebHook(ROUTER_WEBHOOK_BEFORE, port, router);

	bindRoutes(routes, router, host.concat(":").concat(port.toString()));

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

    void bindRoutes(List<Route> routes, Router router, String hostInfo) {
	Route defaultRoute = null;

	for (Route route : routes) {

	    if (null != route.getPath() && route.getPath().equals("/")) {
		defaultRoute = route;
	    }

	    addRouteToRouter(router, route, hostInfo);
	}
	/*
	 * Its important to bind the handling for / in the end
	 */
	if (null != defaultRoute) {
	    addRouteToRouter(router, defaultRoute, hostInfo);
	}
    }

    void addRouteToRouter(Router router, Route route, String hostInfo) {
	if (null != route.getPath() && null != route.getChannelName()) {
	    for (String path : route.getPath()) {

		logger.info("Binding url " + path + (null != route.getMethod() ? " " + route.getMethod() + " " : "")
			+ " -> " + hostInfo);
		io.vertx.ext.web.Route vertxRoute = null;

		if (!route.isRegex()) {
		    vertxRoute = router.route(path);
		} else {
		    vertxRoute = router.route().pathRegex(path);
		}

		if (null != route.getMethod()) {
		    vertxRoute.method(HttpMethod.valueOf(route.getMethod()));
		}
		vertxRoute.handler(new RouterHandler(route.getChannelName(), null));
	    }
	} else {
	    logger.error("Cannot bind route because path or channelName is missing");
	}

    }
}
