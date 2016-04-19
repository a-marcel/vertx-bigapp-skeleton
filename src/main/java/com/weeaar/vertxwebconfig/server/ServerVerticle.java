package com.weeaar.vertxwebconfig.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.weeaar.vertxwebconfig.codec.request.HttpRequest;
import com.weeaar.vertxwebconfig.codec.request.HttpRequestCodec;
import com.weeaar.vertxwebconfig.codec.response.HttpResponse;
import com.weeaar.vertxwebconfig.codec.response.HttpResponseCodec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class ServerVerticle
    extends AbstractVerticle
{
    Logger logger = LoggerFactory.getLogger( ServerVerticle.class );

    @Override
    public void start( Future<Void> fut )
        throws Exception
    {
        super.start();

        vertx.eventBus().registerDefaultCodec( HttpRequest.class, new HttpRequestCodec() );
        vertx.eventBus().registerDefaultCodec( HttpResponse.class, new HttpResponseCodec() );

        @SuppressWarnings( "rawtypes" )
        List<Future> allServerFuture = new ArrayList<Future>();

        Integer port = config().getInteger( "http.port", 8081 );
        String host = config().getString( "http.host", "localhost" );

        JsonObject portConfig = config();

        if ( portConfig.containsKey( "portWithRoutes" ) )
        {
            logger.debug( "Starting with Config: " + portConfig.getJsonObject( "portWithRoutes" ) );

            for ( Map.Entry<String, Object> entry : portConfig.getJsonObject( "portWithRoutes" ).getMap().entrySet() )
            {
                Future<Void> serverFuture = Future.future();

                if ( entry.getValue() instanceof JsonObject )
                {
                    JsonObject config = (JsonObject) entry.getValue();

                    if ( entry.getKey().equals( "DEFAULT" ) )
                    {
                        startServerOnPort( port, host, config, serverFuture );
                    }
                    else
                    {
                        Integer newPort = Integer.valueOf( entry.getKey() );
                        startServerOnPort( newPort, host, config, serverFuture );
                    }
                }
                else
                {
                    logger.error( "Configuration is wrong" );
                }

                allServerFuture.add( serverFuture );
            }
        }
        else
        {
            logger.error( "Wrong Configuration" );
        }

        if ( allServerFuture.size() > 0 )
        {
            CompositeFuture.all( allServerFuture ).setHandler( ar -> {
                if ( ar.succeeded() )
                {
                    fut.complete();
                }
                else
                {
                    fut.fail( ar.cause() );
                }
            } );
        }
        else
        {
            fut.complete();
        }
    }

    void startServerOnPort( Integer port, String host, JsonObject config, Future<Void> fut )
    {
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router( vertx );

        if ( config.containsKey( "routes" ) )
        {
            parseRouteConfig( config.getJsonArray( "routes" ), router, host.concat( ":" ).concat( port.toString() ) );
        }

        server.requestHandler( router::accept );

        server.listen( port, host, result -> {
            if ( result.succeeded() )
            {
                logger.info( "Web Server listen on port " + port );
                fut.complete();
            }
            else
            {
                logger.error( "Web server not stated: " + result.cause().getMessage() );
                fut.fail( result.cause() );
            }
        } );

    }

    void parseRouteConfig( JsonArray routes, Router router, String hostInfo )
    {
        for ( Object object : routes )
        {
            if ( object instanceof JsonObject )
            {
                JsonObject route = (JsonObject) object;

                if ( route.containsKey( "path" ) && route.containsKey( "channelName" ) )
                {
                    logger.info( "Binding url " + route.getString( "path" ) + " -> " + hostInfo );
                    router.route( route.getString( "path" ) ).handler( new RouterHandler( route.getString( "channelName" ) ) );
                }
                else if ( route.containsKey( "pathRegex" ) && route.containsKey( "channelName" ) )
                {
                    logger.info( "Binding url " + route.getString( "path" ) );
                    router.route().pathRegex( route.getString( "pathRegex" ) ).handler( new RouterHandler( route.getString( "channelName" ) ) );
                }
                else
                {
                    logger.error( "Cannot bind route because path or channelName is missing" );
                }
            }
        }
    }
}
