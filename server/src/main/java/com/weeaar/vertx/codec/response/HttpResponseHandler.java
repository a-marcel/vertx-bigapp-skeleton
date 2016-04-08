package com.weeaar.vertx.codec.response;

import java.util.Map;

import com.weeaar.vertx.server.RouterHandler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HttpResponseHandler implements Handler< AsyncResult< Message< HttpResponse > > > {
	final HttpServerResponse response;

	Logger logger = LoggerFactory.getLogger(RouterHandler.class);

	public HttpResponseHandler(HttpServerResponse response) {
		this.response = response;
	}

	@Override
	public void handle(AsyncResult< Message< HttpResponse > > reply) {
		Message< HttpResponse > message = reply.result();

		try {
			if (null == message) {
				throw new Exception("Not a valid message");
			}

			HttpResponse returnResponse = message.body();

			if (null != returnResponse) {

				if (null != returnResponse.getStatusCode()) {
					response.setStatusCode(returnResponse.getStatusCode());
				}

				if (null != returnResponse.headers()) {
					for (Map.Entry< String, Object > header : returnResponse.headers()) {
						if (header.getValue() instanceof String) {
							response.headers().add(header.getKey(), header.getValue().toString());
						}
					}
				}

				if (null != returnResponse.trailers()) {
					for (Map.Entry< String, Object > trailer : returnResponse.trailers()) {
						if (trailer.getValue() instanceof String) {
							response.trailers().add(trailer.getKey(), trailer.getValue().toString());
						}
					}
				}

				if (null != returnResponse.getBody()) {
					response.end(returnResponse.getBody());
				}

				if (null == returnResponse.headers() && null == returnResponse.trailers()
						&& null == returnResponse.getBody()) {
					response.setStatusCode(HttpResponseStatus.NO_CONTENT.code());
				}
			}
		} catch (Exception e) {
			logger.warn(String.format("Wrong response type: %s", e.getMessage()), e);
			response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code());
		} finally {
			if (!response.ended()) {
				response.end();
			}
		}
	}
}
