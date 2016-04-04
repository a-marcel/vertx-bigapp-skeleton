package test.codec;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.json.JsonObject;

public class HttpResponse {
	static String STATUS_CODE = "statusCode";

	static String BODY = "body";

	static String HEADER = "header";

	private JsonObject jsonObject;

	public HttpResponse() {
		this(new JsonObject());
	}

	public HttpResponse(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public void setStatusCode(Integer statusCode) {
		jsonObject.put(STATUS_CODE, statusCode);
	}

	public Integer getStatusCode() {
		return jsonObject.getInteger(STATUS_CODE);
	}

	public void setBody(String body) {
		jsonObject.put(BODY, body);
	}

	public String getBody() {
		return jsonObject.getString(BODY);
	}

	public void setHeader(JsonObject header) {
		jsonObject.put(HEADER, header);
	}

	public Map< String, String > getHeader() {
		JsonObject header = jsonObject.getJsonObject(HEADER);

		if (null == header) {
			return null;
		}

		Map< String, String > headerMap = new HashMap< String, String >();

		header.forEach((consumer) ->
		{
			headerMap.put(consumer.getKey(), consumer.getValue().toString());
		});

		return headerMap;
	}

	public void putHeader(String name, String value) {
		JsonObject header = jsonObject.getJsonObject(HEADER);

		if (null == header) {
			header = new JsonObject();
		}
		header.put(name, value);

		setHeader(header);
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "HttpResponse [jsonObject=" + jsonObject + "]";
	}
}
