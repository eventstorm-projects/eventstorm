package eu.eventstorm.cqrs.els.netty;

import co.elastic.clients.json.JsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransportBase;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.instrumentation.Instrumentation;

import javax.annotation.Nullable;

public class NettyElasticsearchTransport extends ElasticsearchTransportBase {

    public NettyElasticsearchTransport(NettyTransportHttpClient httpClient, TransportOptions options, JsonpMapper jsonpMapper, @Nullable Instrumentation instrumentation) {
        super(httpClient, options, jsonpMapper, instrumentation);
    }

}
