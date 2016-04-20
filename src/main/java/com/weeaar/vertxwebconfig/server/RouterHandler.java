package com.weeaar.vertxwebconfig.server;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.weeaar.vertxwebconfig.codec.request.HttpRequest;
import com.weeaar.vertxwebconfig.codec.response.HttpResponseHandler;

public class RouterHandler implements Handler<RoutingContext> {
    Logger logger = LoggerFactory.getLogger(RouterHandler.class);

    private final String channelName;
    
    private JsonObject defaultHeader;

    public RouterHandler(String channelName, JsonObject defaultHeader) {
	this.channelName = channelName;
	this.defaultHeader = defaultHeader;
    }

    @Override
    public void handle(RoutingContext context) {
	context.vertx().eventBus().send(channelName, new HttpRequest(context.request()),
		new HttpResponseHandler(context.response(), this.defaultHeader));
    }
}