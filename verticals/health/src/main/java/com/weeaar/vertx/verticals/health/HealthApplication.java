package com.weeaar.vertx.verticals.health;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import com.weeaar.vertx.codec.request.HttpRequest;
import com.weeaar.vertx.codec.response.HttpResponse;

import io.vertx.core.eventbus.MessageConsumer;

public class HealthApplication extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(HealthApplication.class);

	@Override
	public void start(Future< Void > fut) throws Exception {
		super.start();

		MessageConsumer< HttpRequest > consumer = vertx.eventBus().consumer("testChannel", (message) ->
		{
			HttpResponse response = new HttpResponse();
			response.setStatusCode(HttpResponseStatus.OK.code());

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
}
