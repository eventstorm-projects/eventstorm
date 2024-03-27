package eu.eventstorm.eventstore.db;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import eu.eventstorm.core.Event;
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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class AbstractLocalDatabaseEventStore implements EventStore {

    protected static final ZoneId SYSTEM_ZONE_ID = ZoneId.systemDefault();

    protected final EventStoreProperties eventStoreProperties;

    protected final DatabaseRepository databaseRepository;

    protected final TransactionTemplate template;

    protected final StreamManager streamManager;

    protected final PayloadManager payloadManager;

    public AbstractLocalDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager) {
        this(database, eventStoreProperties, streamManager, JsonPayloadManager.INSTANCE);
    }

    public AbstractLocalDatabaseEventStore(Database database, EventStoreProperties eventStoreProperties, StreamManager streamManager, PayloadManager payloadManager) {
        this.eventStoreProperties = eventStoreProperties;
        this.databaseRepository = new DatabaseRepository(database);
        this.template = new TransactionTemplate(database.transactionManager());
        this.streamManager = streamManager;
        this.payloadManager = payloadManager;
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

    private record EventResultSetMapper(String streamId,
                                        StreamDefinition definition) implements ResultSetMapper<Event> {

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

}