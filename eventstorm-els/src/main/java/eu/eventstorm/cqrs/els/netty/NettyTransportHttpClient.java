package eu.eventstorm.cqrs.els.netty;

import co.elastic.clients.transport.DefaultTransportOptions;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.http.TransportHttpClient;
import eu.eventstorm.util.Strings;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class NettyTransportHttpClient implements TransportHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyTransportHttpClient.class);

    private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
    private final NettyTransportHttpClientConfiguration clientConfiguration;
    private final String basicAuth;
    private final SslContext sslContext;

    public NettyTransportHttpClient(NettyTransportHttpClientConfiguration clientConfiguration) {
        this.clientConfiguration = clientConfiguration;
        this.basicAuth = initBasicAuth(clientConfiguration);
        try {
            sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .sslProvider(SslProvider.JDK)
                    .build();
        } catch (SSLException cause) {
            throw new NettyException("Failed to create SSL context", cause);
        }
    }

    @Override
    public TransportOptions createOptions(@Nullable TransportOptions options) {
        return DefaultTransportOptions.of(options);
    }

    @Override
    public Response performRequest(String endpointId, @Nullable Node node, Request request, TransportOptions transportOptions) {
        try {
            return performRequestAsync(endpointId, node, request, transportOptions).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<Response> performRequestAsync(String endpointId, Node node, Request request, TransportOptions options) {

        if (node == null) {
            node = initNode();
        }

        // init the response
        CompletableFuture<Response> promise = initResponse();

        // init the bootstrap
        Bootstrap bootstrap = initBootstrap(workerGroup, node, promise);

        // init the uri
        String uri = initUri(node, request, options);

        // init the body
        ByteBuf body = initBody(request);

        FullHttpRequest nettyRequest = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1,
                HttpMethod.valueOf(request.method()),
                uri,
                body
        );

        HttpHeaders nettyHeaders = nettyRequest.headers();

        // Netty doesn't set Content-Length automatically with FullRequest.
        nettyHeaders.set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());

        // handle basic auth
        appendAuthentication(nettyHeaders, clientConfiguration);

        int port = node.uri().getPort();
        if (port == -1) {
            port = node.uri().getScheme().equals("https") ? 443 : 80;
        }

        nettyHeaders.set(HttpHeaderNames.HOST, node.uri().getHost() + ":" + port);

        request.headers().forEach(nettyHeaders::set);
        options.headers().forEach((kv) -> nettyHeaders.set(kv.getKey(), kv.getValue()));

        ChannelFuture future0 = bootstrap.connect(node.uri().getHost(), port);
        future0.addListener((ChannelFutureListener) future1 -> {
            if (checkSuccess(future1, promise)) {
                ChannelFuture future2 = future1.channel().writeAndFlush(nettyRequest);
                future2.addListener((ChannelFutureListener) future3 -> {
                    if (checkSuccess(future3, promise)) {
                        // Log request sent?
                    }
                });
            }
        });

        future0.addListener(future4 -> {
            if (future4.cause() != null) {
                promise.completeExceptionally(future4.cause());
            } else if (future4.isCancelled()) {
                promise.completeExceptionally(new RuntimeException("Request was cancelled"));
            }
        });

        return promise;
    }

    @SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
    private static ByteBuf initBody(Request request) {
        Iterable<ByteBuffer> body = request.body();
        if (body == null) {
            return Unpooled.buffer(0);
        } else {
            List<ByteBuffer> bufs;
            if (body instanceof List) {
                bufs = (List<ByteBuffer>) body;
            } else {
                bufs = new ArrayList<>();
                for (ByteBuffer buf : body) {
                    bufs.add(buf);
                }
            }
            return Unpooled.wrappedBuffer(bufs.toArray(new ByteBuffer[bufs.size()]));
        }
    }

    private static String initUri(Node node, Request request, TransportOptions options) {

        String uri = request.path();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initUri [{}]", uri);
        }

        // If the node is not at the server root, prepend its path.
        String nodePath = node.uri().getRawPath();
        if (nodePath.length() > 1) {
            if (uri.charAt(0) == '/') {
                uri = uri.substring(1);
            }
            uri = nodePath + uri;
        }


        // Append query parameters
        String queryString = queryString(request, options);
        if (queryString != null) {
            uri = uri + "?" + queryString;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initUri complete : [{}]", uri);
        }

        return uri;
    }

    private Node initNode() {
        return new Node(URI.create(clientConfiguration.getScheme() + "://" + clientConfiguration.getHost() + ":" + clientConfiguration.getPort()));
    }

    private static String queryString(Request request, TransportOptions options) {
        Map<String, String> requestParams = request.queryParams();
        Map<String, String> optionsParams = options == null ? Collections.emptyMap() : options.queryParameters();

        Map<String, String> allParams;
        if (requestParams.isEmpty()) {
            allParams = optionsParams;
        } else if (optionsParams.isEmpty()) {
            allParams = requestParams;
        } else {
            allParams = new HashMap<>(requestParams);
            allParams.putAll(optionsParams);
        }

        if (allParams.isEmpty()) {
            return null;
        } else {
            return allParams
                    .entrySet()
                    .stream()
                    .map(e -> {
                        return URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                                URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8);
                    })
                    .collect(Collectors.joining("&"));
        }
    }

    private boolean checkSuccess(Future<?> future, CompletableFuture<?> promise) {
        if (future.isSuccess()) {
            return true;
        }

        if (future.cause() != null) {
            promise.completeExceptionally(future.cause());
        } else if (future.isCancelled()) {
            promise.completeExceptionally(new RuntimeException("Request was cancelled"));
        } else {
            promise.completeExceptionally(new RuntimeException("Unexpected future state"));
        }
        return false;
    }

    @Override
    public void close() throws IOException {

    }

    private static class ChannelHandler extends SimpleChannelInboundHandler<HttpObject> {

        private final CompletableFuture<Response> promise;
        private final Node node;
        private volatile HttpResponse response;
        private volatile List<ByteBuf> body;

        ChannelHandler(Node node, CompletableFuture<Response> promise) {
            this.node = node;
            this.promise = promise;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
            if (msg instanceof HttpResponse) {
                this.response = (HttpResponse) msg;

            } else if (msg instanceof HttpContent content) {
                ByteBuf buf = content.content();
                if (buf.readableBytes() > 0) {
                    buf.retain();
                    if (this.body == null) {
                        this.body = new ArrayList<>();
                    }
                    this.body.add(buf);
                }

                if (msg instanceof LastHttpContent) {
                    promise.complete(new NettyResponse(node, response, body));
                    ctx.close();
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            promise.completeExceptionally(cause);
            ctx.close();
        }
    }


    private static CompletableFuture<Response> initResponse() {
        return new CompletableFuture<>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return super.cancel(mayInterruptIfRunning);
            }
        };
    }

    private Bootstrap initBootstrap(NioEventLoopGroup workerGroup, Node node, CompletableFuture<Response> promise) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        if (node.uri().getScheme().equals("https")) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("enabling SSL for uri : [{}]", node.uri());
                            }
                            pipeline.addLast(sslContext.newHandler(ch.alloc()));
                        }
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new ChannelHandler(node, promise));
                    }
                });
        return bootstrap;
    }

    private static String initBasicAuth(NettyTransportHttpClientConfiguration clientConfiguration) {
        if (Strings.isNotEmpty(clientConfiguration.getUsername()) && Strings.isNotEmpty(clientConfiguration.getPassword())) {
            return "Basic " + Base64.getEncoder()
                    .encodeToString((clientConfiguration.getUsername() + ":" + clientConfiguration.getPassword())
                            .getBytes(StandardCharsets.ISO_8859_1));
        } else {
            return Strings.EMPTY;
        }
    }


    private void appendAuthentication(HttpHeaders nettyHeaders, NettyTransportHttpClientConfiguration clientConfiguration) {
        if (Strings.EMPTY.equals(basicAuth)) {
            return;
        }
        nettyHeaders.set(HttpHeaderNames.AUTHORIZATION, basicAuth);
    }

}
