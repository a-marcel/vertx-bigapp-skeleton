package com.weeaar.vertx.verticals.robotstxt;

import io.netty.handler.codec.http.HttpResponseStatus;


import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import com.weeaar.vertx.annotation.AbstractWebVerticle;
import com.weeaar.vertx.annotation.VertxWebConfig;
import com.weeaar.vertx.codec.request.HttpRequest;
import com.weeaar.vertx.codec.response.HttpResponse;
import com.weeaar.vertx.server.Route;

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
