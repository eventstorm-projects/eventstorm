package eu.eventstorm.cqrs.els.netty;

class NettyClientTest  extends TransportHttpClientTest<NettyTransportHttpClient> {

    public NettyClientTest() throws Exception {
        super(new NettyTransportHttpClient(new NettyTransportHttpClientConfiguration()));
    }

}
