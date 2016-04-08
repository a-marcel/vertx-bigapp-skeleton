package com.weeaar.vertx.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.weeaar.vertx.annotation.VertxWebConfig;
import com.weeaar.vertx.codec.service.ServiceLoader;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Launcher;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public abstract class VertxApplication
    extends AbstractVerticle
{
    static Logger logger = LoggerFactory.getLogger( VertxApplication.class );

    public static void main( String[] args )
    {
        Launcher.main( new String[] { "run", "service:" + VertxApplication.class.getName() } );
    }

    @Override
    public void start()
        throws Exception
    {
        logger.debug( "starting" );

        ServiceLoader serviceLoader = new ServiceLoader();
        List<String> verticals = serviceLoader.findVerticles();

        @SuppressWarnings( "rawtypes" )
        List<Future> allVerticalFuture = new ArrayList<Future>();

        List<JsonObject> routes = new ArrayList<JsonObject>();

        if ( null != verticals )
        {
            for ( String name : verticals )
            {
                /*
                 * Figure out routes
                 */
                Method[] methods = Class.forName( name ).getMethods();
                for ( Method method : methods )
                {
                    if ( method.isAnnotationPresent( VertxWebConfig.class ) )
                    {
                        VertxWebConfig vertxWebConfig = method.getAnnotation( VertxWebConfig.class );

                        JsonObject route = new JsonObject();
                        if ( null != vertxWebConfig.channelName() )
                        {
                            route.put( "channelName", vertxWebConfig.channelName() );
                        }

                        if ( null != vertxWebConfig.path() )
                        {
                            route.put( "path", vertxWebConfig.path() );
                        }

                        if ( null != vertxWebConfig.pathRegex() )
                        {
                            route.put( "pathRegex", vertxWebConfig.pathRegex() );
                        }

                        if ( !route.isEmpty() )
                        {
                            routes.add( route );
                        }
                    }
                }

                /*
                 * Starting verticle
                 */
                Future<String> verticleFuture = Future.future();

                vertx.deployVerticle( name, verticleFuture.completer() );
                allVerticalFuture.add( verticleFuture );
            }
        }

        if ( allVerticalFuture.size() > 0 )
        {
            CompositeFuture.all( allVerticalFuture ).setHandler( ar -> {
                if ( ar.succeeded() )
                {
                    logger.info( "All verticals successfull deployed" );

                    startServerVerticle( routes );
                }
                else
                {
                    logger.error( "Deploying verticals failed", ar.cause() );
                }
            } );
        }
        else
        {
            startServerVerticle( routes );
        }
    }

    void startServerVerticle( List<JsonObject> routes )
    {
        if ( config().containsKey( "serverService" ) )
        {
            JsonObject serverServiceOptions = config().getJsonObject( "serverService" );

            String serverVerticleName = serverServiceOptions.getString( "name" );

            JsonObject deployConfig = new JsonObject();
            deployConfig.put( "routes", routes );

            DeploymentOptions options = new DeploymentOptions();

            if ( serverServiceOptions.containsKey( "config" ) )
            {
                JsonObject serverConfig = serverServiceOptions.getJsonObject( "config" );

                for ( Map.Entry<String, Object> configObject : serverConfig.getMap().entrySet() )
                {
                    deployConfig.put( configObject.getKey(), configObject.getValue() );
                }

                options.setConfig( deployConfig );
            }

            vertx.deployVerticle( serverVerticleName, options );
        }
    }
}
