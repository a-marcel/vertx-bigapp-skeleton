package com.weeaar.vertxwebconfig.server;

import io.vertx.core.json.JsonObject;

public class ServerConfig extends JsonObject {
    public static Integer DEFAULT_PORT = 8081;

    public ServerConfig() {

    }

    public ServerConfig(JsonObject config) {
	if (config.containsKey("serverName")) {
	    this.setClassName(config.getString("serverName"));
	}

	if (config.containsKey("port")) {
	    this.setPort(config.getInteger("port"));
	}

	if (config.containsKey("host")) {
	    this.setHost(config.getString("host"));
	}
    }

    public void setClassName(String className) {
	this.put("serverName", className);
    }

    public void setClassName(Class<?> clazz) {
	this.setClassName(clazz.getName());
    }

    public String getClassName() {
	if (this.containsKey("serverName")) {
	    return this.getString("serverName");
	}
	return null;
    }

    public void setPort(Integer port) {
	this.put("port", port);
    }

    public Integer getPort() {
	if (this.containsKey("port")) {
	    return this.getInteger("port");
	}
	return DEFAULT_PORT;
    }

    public void setHost(String host) {
	this.put("host", host);
    }

    public String getHost() {
	if (this.containsKey("host")) {
	    return this.getString("host");
	}
	return "localhost";
    }
}
