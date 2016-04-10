package com.weeaar.vertxwebconfig.examples.verticals.health;

import com.weeaar.vertxwebconfig.annotation.AbstractWebVerticle;
import com.weeaar.vertxwebconfig.annotation.VertxWebConfig;
import com.weeaar.vertxwebconfig.codec.request.HttpRequest;
import com.weeaar.vertxwebconfig.codec.response.HttpResponse;

import io.netty.handler.codec.http.HttpResponseStatus;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import io.vertx.core.eventbus.Message;

public class HealthApplication
    extends AbstractWebVerticle
{
    Logger logger = LoggerFactory.getLogger( HealthApplication.class );

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
