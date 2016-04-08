package com.weeaar.vertx.verticals.robotstxt;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.weeaar.vertx.codec.request.HttpRequest;
import com.weeaar.vertx.codec.response.HttpResponse;
import com.weeaar.vertx.server.Route;
import com.weeaar.vertx.server.RoutesParser;

import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpHeaders;

public class RobotsApplication extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(RobotsApplication.class);

	@Override
	public void start(Future< Void > fut) throws Exception {
		super.start();

		if (config().containsKey("routes")) {
			RoutesParser routeData = new RoutesParser(config().getJsonArray("routes"));

			for (Route route : routeData) {
				MessageConsumer< HttpRequest > consumer = vertx.eventBus().consumer(route.getChannelName(), (message) ->
				{
					HttpResponse response = new HttpResponse();
					response.setStatusCode(HttpResponseStatus.OK.code());
					response.setBody("User-agent: *\nDisallow: /");
					response.addHeader(HttpHeaders.CONTENT_TYPE.toString(), "text/plain");

					message.reply(response);
				});

				consumer.completionHandler(result ->
				{
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});
			}
		} else {
			fut.fail("No routes or config found");
		}
	}
}
