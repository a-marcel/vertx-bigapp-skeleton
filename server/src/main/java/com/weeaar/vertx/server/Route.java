package com.weeaar.vertx.server;

import io.vertx.core.json.JsonObject;

public class Route {
	String channelName;
	String regexPath;
	String path;

	Route(JsonObject route) {
		if (route.containsKey("channelName")) {
			channelName = route.getString("channelName");
		}

		if (route.containsKey("regexPath")) {
			regexPath = route.getString("regexPath");
		}

		if (route.containsKey("path")) {
			path = route.getString("path");
		}
	}

	public String getChannelName() {
		return channelName;
	}

	public String getPath() {
		return path;
	}

	public String getRegexPath() {
		return regexPath;
	}
}
