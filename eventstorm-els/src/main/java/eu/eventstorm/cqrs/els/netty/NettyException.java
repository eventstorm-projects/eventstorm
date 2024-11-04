package eu.eventstorm.cqrs.els.netty;

public final class NettyException extends RuntimeException {

    NettyException(String message, Throwable cause) {
        super(message, cause);
    }

}
