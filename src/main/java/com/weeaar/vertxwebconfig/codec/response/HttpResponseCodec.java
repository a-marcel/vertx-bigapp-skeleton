package com.weeaar.vertxwebconfig.codec.response;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

public class HttpResponseCodec
    implements MessageCodec<HttpResponse, HttpResponse>

{
    @Override
    public void encodeToWire( Buffer buffer, HttpResponse s )
    {
        s.toJson().writeToBuffer( buffer );
    }

    @Override
    public HttpResponse decodeFromWire( int pos, Buffer buffer )
    {
        JsonObject o = new JsonObject();
        o.readFromBuffer( pos, buffer );

        return new HttpResponse( o );
    }

    @Override
    public HttpResponse transform( HttpResponse s )
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
