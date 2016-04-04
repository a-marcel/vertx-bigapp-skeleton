package test.codec;

import java.util.Map;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

public class HttpRequest {
	static String PATH_NAME = "path";

	static String PARAMS = "params";

	static String METHOD = "method";

	private JsonObject jsonObject;

	public HttpRequest() {
		this(new JsonObject());
	}

	public HttpRequest(JsonObject object) {
		this.jsonObject = object;
	}

	public static HttpRequest buildFromVertxHttpServerRequest(HttpServerRequest vertxRequest) {
		HttpRequest request = new HttpRequest();
		request.setPath(vertxRequest.path());
		request.setMethod(vertxRequest.method().toString());

		MultiMap params = vertxRequest.params();

		if (null != params) {
			JsonObject mappedParams = new JsonObject();
			for (Map.Entry< String, String > entry : params.entries()) {
				mappedParams.put(entry.getKey(), entry.getValue());
			}

			request.setParams(mappedParams);
		}

		return request;
	}

	public String getPath() {
		return this.jsonObject.getString(PATH_NAME);
	}

	public void setPath(String path) {
		this.jsonObject.put(PATH_NAME, path);
	}

	public String getMethod() {
		return this.jsonObject.getString(METHOD);
	}

	public void setMethod(String method) {
		this.jsonObject.put(METHOD, method);
	}

	public void setParams(JsonObject params) {
		this.jsonObject.put(PARAMS, params);
	}

	public String getParam(String name) {
		if (null == this.jsonObject.getJsonObject(PARAMS)) {
			return null;
		}

		try {
			JsonObject params = this.jsonObject.getJsonObject(PARAMS);

			if (params.containsKey(name)) {
				return params.getString(name);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "HttpRequest [jsonObject=" + jsonObject + "]";
	}
}