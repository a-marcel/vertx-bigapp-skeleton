package test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
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

        HttpServer server = vertx.createHttpServer();

        Integer port = config().getInteger( "http.port", 8081 );
        String host = config().getString( "http.host", "localhost" );

        Router router = Router.router( vertx );

        if ( config().containsKey( "routes" ) )
        {
            parseRouteConfig( config().getJsonArray( "routes" ), router );
        }

        // router.route().method(HttpMethod.GET).handler(new
        // RouterHandler(EventBusChannelNames.HTTP_GET_REQUEST_CHANNEL));

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

    void parseRouteConfig( JsonArray routes, Router router )
    {
        for ( Object object : routes )
        {
            if ( object instanceof JsonObject )
            {
                JsonObject route = (JsonObject) object;

                if ( route.containsKey( "path" ) )
                {
                    router.route( route.getString( "path" ) ).handler( routingContext -> {
                        logger.info( "Path " + route.getString( "path" ) );
                    } );
                }
            }
        }
    }
}
