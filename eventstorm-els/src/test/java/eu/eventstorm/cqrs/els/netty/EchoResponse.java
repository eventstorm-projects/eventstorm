package eu.eventstorm.cqrs.els.netty;

import java.util.List;
import java.util.Map;

public record EchoResponse(Map<String, List<String>> headers, String body) {
}