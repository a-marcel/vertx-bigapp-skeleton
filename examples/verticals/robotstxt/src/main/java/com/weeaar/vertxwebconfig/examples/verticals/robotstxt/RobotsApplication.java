package com.weeaar.vertxwebconfig.examples.verticals.robotstxt;

import com.weeaar.vertxwebconfig.annotation.AbstractWebVerticle;
import com.weeaar.vertxwebconfig.annotation.VertxWebConfig;
import com.weeaar.vertxwebconfig.codec.request.HttpRequest;
import com.weeaar.vertxwebconfig.codec.response.HttpResponse;

import io.netty.handler.codec.http.HttpResponseStatus;


import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpHeaders;

public class RobotsApplication
    extends AbstractWebVerticle
{
    Logger logger = LoggerFactory.getLogger( RobotsApplication.class );

    @VertxWebConfig( channelName = "robotstxtChannel", path = "/robots.txt" )
    public void testCall( Message<HttpRequest> message )
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode( HttpResponseStatus.OK.code() );
        response.setBody( "User-agent: *\nDisallow: /" );
        response.addHeader( HttpHeaders.CONTENT_TYPE.toString(), "text/plain" );

        message.reply( response );
    }
}
