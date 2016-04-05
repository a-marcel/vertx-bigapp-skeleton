package test.codec.request;

import java.util.Map;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class HttpRequest {

	public HttpRequest() {
		// TODO Auto-generated constructor stub
	}

	public HttpRequest(HttpRequest request) {
		// TODO Auto-generated constructor stub
	}

	public HttpRequest(JsonObject json) {
		// TODO Auto-generated constructor stub
	}

	public HttpRequest(HttpServerRequest request) {

	}

	public JsonObject toJson() {
		return null;
	}
}