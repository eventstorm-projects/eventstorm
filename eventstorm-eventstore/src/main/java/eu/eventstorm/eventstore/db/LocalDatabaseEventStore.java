package eu.eventstorm.eventstore.db;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventCandidate;
import eu.eventstorm.eventstore.EventStore;
import eu.eventstorm.eventstore.EventStoreProperties;
import eu.eventstorm.eventstore.Statistics;
import eu.eventstorm.eventstore.StreamDefinition;
import eu.eventstorm.eventstore.StreamManager;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.jdbc.ResultSetMapper;
import eu.eventstorm.sql.util.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public class LocalDatabaseEventStore implements EventStore {

    private static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

    private final EventStoreProperties eventStoreProperties;

    private final DatabaseRepository databaseRepository;

    private final TransactionTemplate template;

    private final StreamManager streamManager;

    private final PayloadManager payloadManager;

    public LocalDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager) {
        this(database, eventStoreProperties, streamManager, JsonPayloadManager.INSTANCE);
    }

    public LocalDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager, PayloadManager payloadManager) {
        this.eventStoreProperties = eventStoreProperties;
        this.databaseRepository = new DatabaseRepository(database);
        this.template = new TransactionTemplate(database.transactionManager());
        this.streamManager = streamManager;
        this.payloadManager = payloadManager;
    }

    @Override
    public <T extends Message> Event appendToStream(EventCandidate<T> candidate, String correlation) {

        Instant instant = Instant.now();

        int revision = this.databaseRepository.lastRevision(candidate.getStream(), candidate.getStreamId());

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

        // @formatter:off
        return Event.newBuilder()
                .setStreamId(candidate.getStreamId())
                .setStream(candidate.getStream())
                .setTimestamp(OffsetDateTime.ofInstant(instant, SYSTEM_ZONE_ID).toString())
                .setRevision(revision + 1)
                .setData(Any.pack(candidate.getMessage(), this.eventStoreProperties.getEventDataTypeUrl() + "/" + candidate.getStream() + "/"))
                .build();
        // @formatter:off
    }

    @Override
    public Stream<Event> readStream(String stream, String streamId) {
        StreamDefinition definition = streamManager.getDefinition(stream);
        return template.stream(() ->
                this.databaseRepository.findAllByStreamAndStreamId(stream, streamId, new EventResultSetMapper(streamId, definition)));

    }

    @Override
    public Stream<Event> readRawStream(String stream, String streamId) {
        StreamDefinition definition = streamManager.getDefinition(stream);
        return template.stream(() ->
                this.databaseRepository.findAllByStreamAndStreamId(stream, streamId, new EventRawResultSetMapper(stream, definition)));

    }

    @Override
    public Statistics stat(String stream) {
        // TODO Auto-generated method stub
        return null;
    }

    private static final class EventRawResultSetMapper implements ResultSetMapper<Event> {

        private final StreamDefinition definition;
        private final String streamId;

        private EventRawResultSetMapper(String streamId, StreamDefinition definition) {
            this.streamId = streamId;
            this.definition = definition;
        }

        @Override
        public Event map(Dialect dialect, ResultSet rs) throws SQLException {
            return Event.newBuilder()
                    .setStreamId(streamId)
                    .setStream(definition.getName())
                    .setTimestamp(OffsetDateTime.ofInstant(rs.getTimestamp(1).toInstant(), ZoneId.systemDefault()).toString())
                    .setRevision(rs.getInt(2))
                    .setData(Any.newBuilder()
                            .setTypeUrl(definition.getStreamEventDefinition(rs.getString(4)).getEventType())
                            .setValue(ByteString.copyFromUtf8(rs.getString(3)))
                            .build())
                    .build();
        }

    }

    private static final class EventResultSetMapper implements ResultSetMapper<Event> {

        private final StreamDefinition definition;
        private final String streamId;

        private EventResultSetMapper(String streamId, StreamDefinition definition) {
            this.streamId = streamId;
            this.definition = definition;
        }

        @Override
        public Event map(Dialect dialect, ResultSet rs) throws SQLException {
            Message message = definition.getStreamEventDefinition(rs.getString(4)).jsonParse(rs.getString(3));
            return Event.newBuilder()
                    .setStreamId(streamId)
                    .setStream(definition.getName())
                    .setTimestamp(OffsetDateTime.ofInstant(rs.getTimestamp(1).toInstant(), ZoneId.systemDefault()).toString())
                    .setRevision(rs.getInt(2))
                    .setData(Any.pack(message, "event"))
                    .build();
        }
    }
//
//	@Override
//	public Statistics stat(String stream) {
//		return null;
//	}

}