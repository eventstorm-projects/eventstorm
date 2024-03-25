package eu.eventstorm.eventstore.db;

import com.google.common.collect.ImmutableMap;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.eventstore.EventStoreException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class JsonPayloadManager implements PayloadManager {

    public static final PayloadManager INSTANCE = new JsonPayloadManager();

    private static final JsonFormat.Printer PRINTER = JsonFormat.printer().omittingInsignificantWhitespace();

    private JsonPayloadManager() {
    }

    @Override
    public <T extends Message> String serialize(EventCandidate<T> candidate) {

        try {
            return PRINTER.print(candidate.getMessage());
        } catch (Exception cause) {
            throw new EventStoreException(EventStoreException.Type.FAILED_TO_SERIALIZE, ImmutableMap.of(
                    "stream", candidate.getStream(),
                    "streamId", candidate.getStreamId(),
                    "message", candidate.getMessage()), cause);
        }

    }

    @Override
    public void deserialize(String payload) {

    }
}
