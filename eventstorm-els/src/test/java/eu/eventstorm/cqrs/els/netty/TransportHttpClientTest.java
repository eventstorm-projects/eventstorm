package eu.eventstorm.cqrs.els.netty;

import co.elastic.clients.transport.DefaultTransportOptions;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.http.HeaderMap;
import co.elastic.clients.transport.http.TransportHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class TransportHttpClientTest<Client extends TransportHttpClient> extends Assertions {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportHttpClientTest.class);

    protected static HttpServer server;
    protected final Client httpClient;

    @BeforeAll
    public static void startEchoServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);

        server.createContext("/root/echo", exchange -> {

            byte[] bytes = exchange.getRequestBody().readAllBytes();
            exchange.getRequestBody().close();

            Headers requestHeaders = exchange.getRequestHeaders();

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, 0);

            var response = new EchoResponse(
                    requestHeaders, new String(bytes, StandardCharsets.UTF_8)
            );

            OutputStream out = exchange.getResponseBody();
            new ObjectMapper().writeValue(out, response);
            out.close();
        });

        server.start();
    }

    @AfterAll
    public static void stopEchoServer() {
        server.stop(0);
    }

    public TransportHttpClientTest(Client httpClient) {
        this.httpClient = httpClient;
    }

    @Test
    public void testClient() throws Exception {

        List<ByteBuffer> requestBody = List.of(
                ByteBuffer.wrap("Hello world\n".getBytes(StandardCharsets.UTF_8)),
                ByteBuffer.wrap("Hello universe\n".getBytes(StandardCharsets.UTF_8))
        );

        TransportHttpClient.Node node = new TransportHttpClient.Node(
                "http://" + server.getAddress().getHostString() + ":" + server.getAddress().getPort() + "/"
        );

        TransportOptions options = new DefaultTransportOptions.Builder()
                .addHeader("X-Options-Header", "options value")
                .build();

        HeaderMap headers = new HeaderMap();
        headers.put("Content-Type", "text/plain");
        headers.put("X-Request-Header", "request value");
        TransportHttpClient.Response response = httpClient.performRequest(
                "foo",
                node,
                new TransportHttpClient.Request("POST", "/root/echo", Map.of(), headers, requestBody),
                options
        );

        assertEquals("application/json", response.body().contentType());

        EchoResponse echoResponse = new ObjectMapper().readValue(response.body().asInputStream(), EchoResponse.class);

        var echoHeaders = normalizeHeaders(echoResponse.headers());
        assertEquals("text/plain", echoHeaders.get("content-type"));
        assertEquals("options value", echoHeaders.get("x-options-header"));
        assertEquals("request value", echoHeaders.get("x-request-header"));

        dump(echoHeaders);

        assertEquals("Hello world\nHello universe\n", echoResponse.body());
    }

    /**
     * Set all header names to lowercase and only keep the 1st header value
     */
    private static Map<String, String> normalizeHeaders(Map<String, List<String>> headers) {
        var result = new HashMap<String, String>();
        headers.forEach((k, v) -> {
            if (v.size() != 1) {
                fail("Header '" + k + "' should have a single value, but was: " + headers);
            }
            result.put(k.toLowerCase(), v.get(0));
        });
        return result;
    }

    private static void dump(Map<String, String> map) {
        StringBuilder builder = new StringBuilder();
        map.forEach((k, v) -> builder.append(k).append("=").append(v).append("\n"));
        LOGGER.info("***** DUMP *****\n{}****************", builder);
    }
}