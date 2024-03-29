package eu.eventstorm.eventstore.db;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.sql.Database;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class LocalDatabaseEventStore extends AbstractLocalDatabaseEventStore {


    public LocalDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager) {
        this(database, eventStoreProperties, streamManager, JsonPayloadManager.INSTANCE);
    }

    public LocalDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager, PayloadManager payloadManager) {
        super(database, eventStoreProperties, streamManager, payloadManager);
    }

    @Override
    public <T extends Message> Event appendToStream(EventCandidate<T> candidate, String correlation) {

        Instant instant = Instant.now();

        Integer revision = this.databaseRepository.lastRevision(candidate.getStream(), candidate.getStreamId());

        if (revision == null) {
            revision = 0;
        }

        DatabaseEventBuilder builder = new DatabaseEventBuilder()
                .withStreamId(candidate.getStreamId())
                .withStream(candidate.getStream())
                .withTime(Timestamp.from(instant))
                .withPayload(payloadManager.serialize(candidate))
                .withRevision(revision + 1)
                .withEventType(candidate.getMessage().getClass().getSimpleName());

        if (correlation != null) {
            builder.withCorrelation(correlation);
        }

        this.databaseRepository.insert(builder.build());

        return Event.newBuilder()
                .setStreamId(candidate.getStreamId())
                .setStream(candidate.getStream())
                .setTimestamp(OffsetDateTime.ofInstant(instant, SYSTEM_ZONE_ID).toString())
                .setRevision(revision + 1)
                .setData(Any.pack(candidate.getMessage(), this.eventStoreProperties.getEventDataTypeUrl() + "/" + candidate.getStream() + "/"))
                .build();
    }

}