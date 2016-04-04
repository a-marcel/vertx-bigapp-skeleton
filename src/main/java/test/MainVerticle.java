package test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

	Logger logger = LoggerFactory.getLogger(ServerVerticle.class);

	@Override
	public void start(Future< Void > fut) throws Exception {
		super.start();

		MessageConsumer< String > consumer = vertx.eventBus().consumer(EventBusChannelNames.HTTP_GET_REQUEST_CHANNEL, message ->
		{
			logger.info(message.body());
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
