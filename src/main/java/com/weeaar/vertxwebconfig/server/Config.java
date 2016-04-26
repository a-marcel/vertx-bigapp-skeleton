package com.weeaar.vertxwebconfig.server;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mvel2.util.ArrayTools;

import com.weeaar.vertxwebconfig.annotation.VertxWebConfig;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Config extends JsonObject {
    static Logger logger = LoggerFactory.getLogger(Config.class);

    public Config() {

    }

    Config(JsonObject config) {
	addConfigFromJsonObject(config);
    }

    public void addServerService() {

    }

    public void setWebRouterHookClass(String webRouterHookClass) {
	this.put("webRouterHookClass", webRouterHookClass);
    }

    public String getWebRouterHookClass() {
	if (this.containsKey("webRouterHookClass")) {
	    return this.getString("webRouterHookClass");
	}
	return null;
    }

    public void setServerConfig(ServerConfig config) {
	this.put("serverConfig", config);
    }

    public ServerConfig getServerConfig() {
	if (this.containsKey("serverConfig")) {
	    return new ServerConfig(this.getJsonObject("serverConfig"));
	}
	return null;
    }

    public void addConfigFromAnnotation(VertxWebConfig config) {
	Route route = new Route();

	if (null != config.channelName() && !config.channelName().isEmpty()) {
	    route.setChannelName(config.channelName());
	}

	if (null != config.path() && config.path().length > 0) {
	    if (config.pathIsRegex()) {
		route.put("pathRegex", Arrays.asList(config.path()));
	    } else {
		route.put("path", Arrays.asList(config.path()));
	    }
	}

	if (null != config.method() && !config.method().isEmpty()) {
	    route.setMethod(config.method());
	}

	if (!route.isEmpty()) {
	    String port = "DEFAULT";

	    if (!config.port().isEmpty()) {
		port = config.port();
	    }

	    this.addRoute(port, route);
	}

    }

    public void addConfigFromJsonObject(JsonObject config) {
	if (null != config && config.containsKey("portWithRoutes")) {

	    for (Map.Entry<String, Object> entry : config.getJsonObject("portWithRoutes").getMap().entrySet()) {

		/*
		 * There a Problems with the JsonObject.getInstant functions
		 */
		JsonObject portConfig = null;
		try {
		    portConfig = config.getJsonObject("portWithRoutes").getJsonObject(entry.getKey());
		} catch (Exception e) {
		    logger.error("Problem with config " + e.getMessage());
		}

		if (null != portConfig && portConfig.containsKey("routes")) {

		    JsonArray routesArray = null;
		    try {
			routesArray = portConfig.getJsonArray("routes");
		    } catch (Exception e) {
			logger.error("Problem with config " + e.getMessage());
		    }

		    if (null != routesArray) {

			if (null != routesArray && routesArray.size() > 0) {
			    for (Object object : routesArray) {
				if (object instanceof JsonObject) {
				    this.addRoute(entry.getKey(), new Route((JsonObject) object));
				}
			    }
			}
		    }
		}
	    }
	}

	if (null != config && config.containsKey("serverConfig")) {
	    this.setServerConfig(new ServerConfig(config.getJsonObject("serverConfig")));
	}

	if (null != config && config.containsKey("webRouterHookClass")) {
	    this.setWebRouterHookClass(config.getString("webRouterHookClass"));
	}
    }

    public void addRoute(String port, Route route) {
	if (!this.containsKey("portWithRoutes")) {
	    this.put("portWithRoutes", new JsonObject());
	}

	if (!this.getJsonObject("portWithRoutes").containsKey(port)) {
	    this.getJsonObject("portWithRoutes").put(port, new JsonObject().put("routes", new JsonArray()));
	}

	this.getJsonObject("portWithRoutes").getJsonObject(port).getJsonArray("routes").add(route);
    }

    public boolean isValid() {
	if (this.containsKey("portWithRoutes")) {
	    return true;
	}

	return false;
    }

    public void setDefaultPort(Integer port) {
	this.put("defaultPort", port);
    }

    public Integer getDefaultPort() {
	if (this.containsKey("defaultPort")) {
	    return this.getInteger("defaultPort");
	}

	if (null != this.getServerConfig()) {
	    ServerConfig serverConfig = this.getServerConfig();
	    return serverConfig.getPort();
	}

	return null;
    }

    public Map<Integer, List<Route>> getRoutesConfig() {
	HashMap<Integer, List<Route>> routesConfig = new HashMap<Integer, List<Route>>();

	if (this.isValid()) {

	    for (Map.Entry<String, Object> entry : this.getJsonObject("portWithRoutes").getMap().entrySet()) {

		if (entry.getValue() instanceof JsonObject) {
		    JsonObject config = (JsonObject) entry.getValue();

		    Integer port = getDefaultPort();

		    if (!entry.getKey().equals("DEFAULT")) {
			port = Integer.valueOf(entry.getKey());
		    }

		    if (!routesConfig.containsKey(getDefaultPort())) {
			routesConfig.put(port, new ArrayList<Route>());
		    }

		    if (config.containsKey("routes") && config.getValue("routes") instanceof JsonArray) {
			routesConfig.get(port).addAll(config.getJsonArray("routes").getList());
		    }
		} else {
		    logger.error("Configuration is wrong");
		}
	    }
	}

	return routesConfig;
    }
}
