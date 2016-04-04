package test;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import test.codec.HttpRequest;
import test.codec.HttpResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.Map;

public class RouterHandler implements Handler< RoutingContext > {
	Logger logger = LoggerFactory.getLogger(RouterHandler.class);

	private final String channelName;

	public RouterHandler(String channelName) {
		this.channelName = channelName;
	}

	@Override
	public void handle(RoutingContext requestBody) {
		requestBody.vertx().eventBus().send(channelName, HttpRequest.buildFromVertxHttpServerRequest(requestBody.request()), new Handler< AsyncResult< Message< HttpResponse > > >() {
			@Override
			public void handle(AsyncResult< Message< HttpResponse > > reply) {
				Message< HttpResponse > message = reply.result();

				HttpServerResponse response = requestBody.response();

				try {
					if (null == message) {
						throw new Exception("Not a valid message");
					}

					HttpResponse messageResponse = (HttpResponse) message.body();

					if (null != messageResponse.getStatusCode()) {
						response.setStatusCode(messageResponse.getStatusCode());
					}

					if (null != messageResponse.getHeader()) {
						for (Map.Entry< String, String > header : messageResponse.getHeader().entrySet()) {
							response.putHeader(header.getKey(), header.getValue());
						}
					}

					if (null == messageResponse.getBody()) {
						response.end();
					} else {
						response.end(messageResponse.getBody());
					}

				} catch (Exception e) {
					logger.warn(String.format("Wrong response type (channelName: %s) : %s", channelName, e.getMessage()));
					response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
				} finally {
					if (!response.ended()) {
						response.end();
					}
				}
			}
		});
	}
}