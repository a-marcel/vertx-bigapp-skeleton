package test.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class HttpResponseCodec implements MessageCodec< HttpResponse, HttpResponse > {
	Logger logger = LoggerFactory.getLogger(HttpResponseCodec.class);

	@Override
	public void encodeToWire(Buffer buffer, HttpResponse s) {
		s.getJsonObject().writeToBuffer(buffer);

	}

	@Override
	public HttpResponse decodeFromWire(int pos, Buffer buffer) {
		JsonObject o = new JsonObject();
		o.readFromBuffer(pos, buffer);

		return new HttpResponse(o);
	}

	@Override
	public HttpResponse transform(HttpResponse s) {
		logger.debug(s);
		return s;
	}

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}
}