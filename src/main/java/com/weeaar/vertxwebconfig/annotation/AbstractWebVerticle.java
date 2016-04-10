package com.weeaar.vertxwebconfig.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public abstract class AbstractWebVerticle
    extends AbstractVerticle
{
    Logger logger = LoggerFactory.getLogger( getClass() );

    @Override
    public void start( Future<Void> fut )
        throws Exception
    {
        Method[] methods = this.getClass().getMethods();

        @SuppressWarnings( "rawtypes" )
        List<Future> eventBusConsumer = new ArrayList<Future>();

        for ( Method method : methods )
        {
            if ( method.isAnnotationPresent( VertxWebConfig.class ) )
            {
                VertxWebConfig vertxWebConfig = method.getAnnotation( VertxWebConfig.class );

                Future<Void> consumerFuture = Future.future();

                MessageConsumer<Object> consumer = vertx.eventBus().consumer( vertxWebConfig.channelName(), message -> {
                    try
                    {
                        method.invoke( this, message );
                    }
                    catch ( Exception e )
                    {
                        logger.error( "Cannot call method " + method.getName(), e );
                    }
                } );

                consumer.completionHandler( consumerFuture.completer() );

                eventBusConsumer.add( consumerFuture );
            }
        }

        if ( null != eventBusConsumer && eventBusConsumer.size() > 0 )
        {
            CompositeFuture.all( eventBusConsumer ).setHandler( all -> {
                if ( all.succeeded() )
                {
                    fut.complete();
                }
                else
                {
                    logger.error( "Error in evenbus deploy ", all.cause() );
                    fut.fail( all.cause() );
                }
            } );
        }
    }
}
