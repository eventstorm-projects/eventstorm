package eu.eventstorm.cqrs.els.netty;

import co.elastic.clients.transport.http.TransportHttpClient;
import co.elastic.clients.transport.http.TransportHttpClient.Node;
import co.elastic.clients.util.BinaryData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

final class NettyResponse implements TransportHttpClient.Response {

    private final Node node;
    private final HttpResponse response;
    @Nullable
    private final List<ByteBuf> body;

    NettyResponse(Node node, HttpResponse response, @Nullable List<ByteBuf> body) {
        this.node = node;
        this.response = response;
        this.body = body;
    }

    @Override
    public Node node() {
        return node;
    }

    @Override
    public int statusCode() {
        return response.status().code();
    }

    @Override
    public String header(String name) {
        return response.headers().get(name);
    }

    @Override
    public List<String> headers(String name) {
        return response.headers().getAll(name); // returns an empty list if no values
    }

    @Nullable
    @Override
    public BinaryData body() throws IOException {
        if (body == null) {
            return null;
        }

        ByteBuf byteBuf = Unpooled.wrappedBuffer(body.size(), body.toArray(new ByteBuf[body.size()]));

        return new NettyInputStreamBinaryData(
                response.headers().get(HttpHeaderNames.CONTENT_TYPE),
                new ByteBufInputStream(byteBuf, true)
        );
    }

    @Nullable
    @Override
    public HttpResponse originalResponse() {
        return this.response;
    }

    @Override
    public void close() throws IOException {
        if (body != null) {
            for (ByteBuf buf: body) {
                // May have been released already if body() was consumed
                if (buf.refCnt() > 0) {
                    buf.release();
                }
            }
            body.clear();
        }
    }
}
