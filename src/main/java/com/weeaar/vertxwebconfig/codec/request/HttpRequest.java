package com.weeaar.vertxwebconfig.codec.request;

import java.util.Map;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.security.cert.X509Certificate;

import io.vertx.codegen.annotations.CacheReturn;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerFileUpload;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.HttpVersion;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

@DataObject( generateConverter = true )
public class HttpRequest
{
    private JsonObject json; // Keep a copy of the original json, so we don't lose info when building options subclasses

    public HttpRequest()
    {
    }

    public HttpRequest( HttpRequest request )
    {
        this.json = request.toJson();
    }

    public HttpRequest( JsonObject json )
    {
        this.json = json.copy();
    }

    public HttpRequest( HttpServerRequest request )
    {
        json = new JsonObject();
        json.put( "version", request.version().name() );
        json.put( "method", request.method().toString() );
        json.put( "path", request.path() );
        json.put( "ssl", request.isSSL() );
        json.put( "uri", request.uri() );
        json.put( "query", request.query() );
        json.put( "remoteAddress", request.remoteAddress().host() );
        json.put( "localAddress", request.localAddress().host() );
        json.put( "absoluteURI", request.absoluteURI() );
        json.put( "isEnded", request.isEnded() );

        if ( null != request.params() )
        {
            JsonObject params = new JsonObject();
            for ( Map.Entry<String, String> entry : request.params() )
            {
                params.put( entry.getKey(), entry.getValue() );
            }

            json.put( "params", params );
        }

        if ( null != request.formAttributes() )
        {
            JsonObject attributes = new JsonObject();
            for ( Map.Entry<String, String> entry : request.formAttributes() )
            {
                attributes.put( entry.getKey(), entry.getValue() );
            }

            json.put( "formAttributes", attributes );
        }

        if ( null != request.headers() )
        {
            JsonObject headers = new JsonObject();
            for ( Map.Entry<String, String> entry : request.headers() )
            {
                headers.put( entry.getKey(), entry.getValue() );
            }

            json.put( "headers", headers );
        }

    }

    public JsonObject toJson()
    {
        return json != null ? json.copy() : new JsonObject();
    }

    /**
     * @return the HTTP version of the request
     */
    String version()
    {
        return json.getString( "version", null );
    }

    /**
     * @return the HTTP method for the request.
     */
    String method()
    {
        return json.getString( "method", null );
    }

    /**
     * @return true if this {@link io.vertx.core.net.NetSocket} is encrypted via SSL/TLS
     */
    boolean isSSL()
    {
        return json.getBoolean( "ssl", false );
    }

    /**
     * @return the URI of the request. This is usually a relative URI
     */
    String uri()
    {
        return json.getString( "uri", null );
    }

    /**
     * @return The path part of the uri. For example /somepath/somemorepath/someresource.foo
     */
    String path()
    {
        return json.getString( "path", null );
    }

    /**
     * @return the query part of the uri. For example someparam=32&amp;someotherparam=x
     */
    String query()
    {
        return json.getString( "query", null );
    }

    /**
     * @return the headers in the request.
     */
    JsonObject headers()
    {
        return json.getJsonObject( "headers", null );
    }

    /**
     * Return the first header value with the specified name
     *
     * @param headerName the header name
     * @return the header value
     */
    String getHeader( String headerName )
    {
        JsonObject headers = headers();

        if ( null != headers && headers.containsKey( headerName ) )
        {
            return headers.getString( headerName );
        }
        return null;
    }

    /**
     * @return the query parameters in the request
     */
    JsonObject params()
    {
        return json.getJsonObject( "params", null );
    }

    /**
     * Return the first param value with the specified name
     *
     * @param paramName the param name
     * @return the param value
     */
    String getParam( String paramName )
    {
        JsonObject params = params();

        if ( null != params && params.containsKey( paramName ) )
        {
            return params.getString( paramName );
        }
        return null;
    }

    /**
     * @return the remote (client side) address of the request
     */
    String remoteAddress()
    {
        return json.getString( "remoteAddress", null );
    }

    /**
     * @return the local (server side) address of the server that handles the request
     */
    String localAddress()
    {
        return json.getString( "localAddress", null );
    }

    /**
     * @return the absolute URI corresponding to the the HTTP request
     */
    String absoluteURI()
    {
        return json.getString( "absoluteURI", null );
    }

    /**
     * Returns a map of all form attributes in the request.
     * <p>
     * Be aware that the attributes will only be available after the whole body has been received, i.e. after the
     * request end handler has been called.
     * <p>
     * {@link #setExpectMultipart(boolean)} must be called first before trying to get the form attributes.
     *
     * @return the form attributes
     */
    JsonObject formAttributes()
    {
        return json.getJsonObject( "formAttributes", null );

    }

    /**
     * Return the first form attribute value with the specified name
     *
     * @param attributeName the attribute name
     * @return the attribute value
     */
    String getFormAttribute( String attributeName )
    {
        JsonObject attributes = formAttributes();

        if ( null != attributes && attributes.containsKey( attributeName ) )
        {
            return attributes.getString( attributeName );
        }
        return null;
    }

    /**
     * Has the request ended? I.e. has the entire request, including the body been read?
     *
     * @return true if ended
     */
    boolean isEnded()
    {
        return json.getBoolean( "ssl", false );
    }

}