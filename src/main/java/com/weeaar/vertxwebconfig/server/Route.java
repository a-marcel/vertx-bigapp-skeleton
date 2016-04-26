package com.weeaar.vertxwebconfig.server;

import io.vertx.core.json.JsonObject;

public class Route extends JsonObject {

    Route(JsonObject route) {
	if (route.containsKey("channelName")) {
	    this.setChannelName(route.getString("channelName"));
	}

	if (route.containsKey("path")) {
	    this.setPath(route.getString("path"));
	}

	if (route.containsKey("isRegex")) {
	    this.setIsRegex(route.getBoolean("isRegex"));
	}

	if (route.containsKey("method")) {
	    this.setMethod(route.getString("method"));
	}
    }

    Route() {

    }

    public void setChannelName(String channelName) {
	this.put("channelName", channelName);
    }

    public String getChannelName() {
	if (this.containsKey("channelName")) {
	    return this.getString("channelName");
	}
	return null;
    }

    public void setPath(String path) {
	this.put("path", path);
    }

    public String getPath() {
	if (this.containsKey("path")) {
	    return this.getString("path");
	}
	return null;
    }

    public void setIsRegex(Boolean regex) {
	this.put("isRegex", regex);
    }

    public boolean isRegex() {
	if (this.containsKey("isRegex")) {
	    return (this.getBoolean("isRegex") ? true : false);
	}
	return false;
    }

    public void setMethod(String method) {
	this.put("method", method);
    }

    public String getMethod() {
	if (this.containsKey("method")) {
	    return this.getString("method");
	}
	return null;
    }

}
