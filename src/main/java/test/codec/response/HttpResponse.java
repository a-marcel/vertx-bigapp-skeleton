package test.codec.response;

import java.util.HashMap;
import java.util.Map;

import io.vertx.codegen.annotations.CacheReturn;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

@DataObject( generateConverter = true )
public class HttpResponse
{
    private JsonObject json; // Keep a copy of the original json, so we don't lose info when building options subclasses

    public HttpResponse()
    {
    }

    public HttpResponse( HttpResponse request )
    {
        json = request.toJson();
    }

    public HttpResponse( JsonObject json )
    {
        this.json = json;
    }

    public HttpResponse( HttpServerResponse response )
    {
        json = new JsonObject();
        json.put( "statusMessage", response.getStatusMessage() );
        json.put( "statusCode", response.getStatusCode() );

        if ( null != response.headers() )
        {
            JsonObject headers = new JsonObject();
            for ( Map.Entry<String, String> entry : response.headers() )
            {
                headers.put( entry.getKey(), entry.getValue() );
            }

            json.put( "headers", headers );
        }

        if ( null != response.trailers() )
        {
            JsonObject trailers = new JsonObject();
            for ( Map.Entry<String, String> entry : response.trailers() )
            {
                trailers.put( entry.getKey(), entry.getValue() );
            }

            json.put( "trailers", trailers );
        }
    }

    public JsonObject toJson()
    {
        return json != null ? json.copy() : new JsonObject();
    }

    int getStatusCode()
    {
        return json.getInteger( "statusCode", null );
    }

    /**
     * @return the HTTP status message of the response. If this is not specified a default value will be used depending
     *         on what {@link #setStatusCode} has been set to.
     */
    String getStatusMessage()
    {
        return json.getString( "statusMessage", null );
    }

    /**
     * @return The HTTP headers
     */
    JsonObject headers()
    {
        return json.getJsonObject( "headers", null );
    }

    /**
     * @return The HTTP trailers
     */
    JsonObject trailers()
    {
        return json.getJsonObject( "trailers", null );
    }

}
