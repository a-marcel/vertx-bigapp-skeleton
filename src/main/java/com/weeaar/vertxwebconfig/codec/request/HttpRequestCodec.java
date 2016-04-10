package com.weeaar.vertxwebconfig.codec.request;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class HttpRequestCodec
    implements MessageCodec<HttpRequest, HttpRequest>
{
    @Override
    public void encodeToWire( Buffer buffer, HttpRequest s )
    {
        s.toJson().writeToBuffer( buffer );

    }

    @Override
    public HttpRequest decodeFromWire( int pos, Buffer buffer )
    {
        JsonObject o = new JsonObject();
        o.readFromBuffer( pos, buffer );

        return new HttpRequest( o );
    }

    @Override
    public HttpRequest transform( HttpRequest s )
    {
        return s;
    }

    @Override
    public String name()
    {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID()
    {
        return -1;
    }
}
