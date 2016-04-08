package com.weeaar.vertx.project;

import com.weeaar.vertx.server.VertxApplication;

import io.vertx.core.Launcher;

public class ProjectApplication extends VertxApplication {
	public static void main(String[] args) {
		Launcher.main(new String[] { "run", "service:" + ProjectApplication.class.getName() });
	}
}
