package eu.eventstorm.eventstore.db;

import java.sql.Timestamp;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.ColumnFormat;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Table;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Table(value = "event_store")
public interface DatabaseEvent {

	@PrimaryKey(value = "stream", length = 64)
	String getStream();

	void setStream(String stream);
	
	@PrimaryKey(value = "stream_id", length = 36, format = ColumnFormat.UUID)
	String getStreamId();

	void setStreamId(String streamId);
	
	@PrimaryKey(value = "revision")
	int getRevision();
	
	void setRevision(int revision);
	
	@Column(value = "correlation", length = 36, nullable = true, format = ColumnFormat.UUID)
	String getCorrelation();

	void setCorrelation(String correlation);
	
	@Column(value = "time")
	Timestamp getTime();
	
	void setTime(Timestamp time);
	
	@Column(value = "event_type", length = 128)
	String getEventType();

	void setEventType(String type);
	
	@Column(value = "payload")
	String getPayload();
	
	void setPayload(String payload);
	
}