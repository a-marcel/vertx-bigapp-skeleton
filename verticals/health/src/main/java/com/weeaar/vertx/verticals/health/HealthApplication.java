package com.weeaar.vertx.verticals.health;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Launcher;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.weeaar.vertx.annotation.AbstractWebVerticle;
import com.weeaar.vertx.annotation.VertxWebConfig;
import com.weeaar.vertx.codec.request.HttpRequest;
import com.weeaar.vertx.codec.response.HttpResponse;

import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;

public class HealthApplication
    extends AbstractWebVerticle
{
    Logger logger = LoggerFactory.getLogger( HealthApplication.class );

    /*
     * @Override public void start( Future<Void> fut ) throws Exception { super.start(); MessageConsumer<HttpRequest>
     * consumer = vertx.eventBus().consumer( "testChannel", ( message ) -> { HttpResponse response = new HttpResponse();
     * response.setStatusCode( HttpResponseStatus.OK.code() ); message.reply( response ); } );
     * consumer.completionHandler( result -> { if ( result.succeeded() ) { fut.complete(); } else { fut.fail(
     * result.cause() ); } } ); }
     */

    @VertxWebConfig( channelName = "test2Channel", path = "/test" )
    public void testCall( Message<HttpRequest> message )
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode( HttpResponseStatus.OK.code() );
        response.setBody( "/test" );

        message.reply( response );
    }

    @VertxWebConfig( channelName = "test4Channel", path = "/test3" )
    public void test3Call( Message<HttpRequest> message )
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode( HttpResponseStatus.OK.code() );
        response.setBody( "/test3" );

        message.reply( response );
    }
}
