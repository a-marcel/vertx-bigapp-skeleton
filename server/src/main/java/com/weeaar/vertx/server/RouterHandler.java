package com.weeaar.vertx.server;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Map;

import com.weeaar.vertx.codec.request.HttpRequest;
import com.weeaar.vertx.codec.response.HttpResponse;
import com.weeaar.vertx.codec.response.HttpResponseHandler;

public class RouterHandler implements Handler< RoutingContext > {
	Logger logger = LoggerFactory.getLogger(RouterHandler.class);

	// Handler<AsyncResult<Message<HttpResponse>>> httpResponseHandler;

	private final String channelName;

	public RouterHandler(String channelName) {
		this.channelName = channelName;
	}

	/*
	 * public void setHttpResponseHandler(
	 * Handler<AsyncResult<Message<HttpResponse>>> handler ) {
	 * this.httpResponseHandler = handler; }
	 */

	/*
	 * public Handler<AsyncResult<Message<HttpResponse>>>
	 * getHttpResponseHandler() { if ( null == httpResponseHandler )
	 * return httpResponseHandler; }
	 */

	@Override
	public void handle(RoutingContext context) {
		context.vertx().eventBus().send(channelName, new HttpRequest(context.request()), new HttpResponseHandler(context.response()));
	}
}