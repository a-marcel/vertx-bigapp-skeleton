package test.codec.response;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import test.RouterHandler;

public class HttpResponseHandler
    implements Handler<AsyncResult<Message<HttpResponse>>>
{
    final HttpServerResponse response;

    Logger logger = LoggerFactory.getLogger( RouterHandler.class );

    public HttpResponseHandler( HttpServerResponse response )
    {
        this.response = response;
    }

    @Override
    public void handle( AsyncResult<Message<HttpResponse>> reply )
    {
        Message<HttpResponse> message = reply.result();

        try
        {
            if ( null == message )
            {
                throw new Exception( "Not a valid message" );
            }

            response.end( "huhu" );
        }
        catch ( Exception e )
        {
            logger.warn( String.format( "Wrong response type: %s", e.getMessage() ), e );
            response.setStatusCode( HttpResponseStatus.BAD_REQUEST.code() );
        }
        finally
        {
            if ( !response.ended() )
            {
                response.end();
            }
        }

    }
}
