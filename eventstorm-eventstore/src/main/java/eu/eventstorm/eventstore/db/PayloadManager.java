package eu.eventstorm.eventstore.db;

import com.google.protobuf.Message;
import eu.eventstorm.core.EventCandidate;

public interface PayloadManager {

    <T extends Message> String serialize(EventCandidate<T> candidate);

    void deserialize(String payload);

}
