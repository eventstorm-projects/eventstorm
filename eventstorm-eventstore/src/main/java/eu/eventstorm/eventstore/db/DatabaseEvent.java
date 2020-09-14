package eu.eventstorm.eventstore.db;

import java.sql.Timestamp;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.annotation.Table;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Table(value = "event_store")
interface DatabaseEvent {

	@Sequence("seq__event_store")
	@PrimaryKey("id")
	long getId();

	void setId(long id);

	@Column(value = "stream", length = 64)
	String getStream();

	void setStream(String stream);
	
	@Column(value = "stream_id", length = 64)
	String getStreamId();

	void setStreamId(String streamId);
	
	@Column(value = "correlation", length = 36, nullable = true)
	String getCorrelation();

	void setCorrelation(String correlation);
	
	@Column(value = "time")
	Timestamp getTime();
	
	void setTime(Timestamp time);
	
	@Column(value = "revision")
	int getRevision();
	
	void setRevision(int revision);
	
	@Column(value = "event_type", length = 128)
	String getEventType();

	void setEventType(String type);
	
	@Column(value = "payload")
	String getPayload();
	
	void setPayload(String payload);
	
}