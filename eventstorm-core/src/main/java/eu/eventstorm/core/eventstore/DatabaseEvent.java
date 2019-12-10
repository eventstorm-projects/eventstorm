package eu.eventstorm.core.eventstore;

import java.sql.Timestamp;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.type.Json;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Table(value = "event_store")
interface DatabaseEvent {

	@Sequence("seq__event_store")
	@PrimaryKey("id")
	long getId();

	void setId(long id);

	@Column(value = "aggregate_type", length = 128)
	String getAggregateType();

	void setAggregateType(String type);
	
	@Column(value = "aggregate_id", length = 128)
	String getAggregateId();

	void setAggregateId(String aggregateId);
	
	@Column(value = "time")
	Timestamp getTime();
	
	void setTime(Timestamp time);
	
	@Column(value = "revision")
	int getRevision();
	
	void setRevision(int revision);

	@Column(value = "payload_schema", length = 64)
	String getPayloadSchema();

	void setPayloadSchema(String schema);
	
	@Column(value = "payload_schema_version")
	int getPayloadSchemaVersion();

	void setPayloadSchemaVersion(int version);

	
	@Column(value = "payload")
	Json getPayload();
	
	void setPayload(Json payload);
	
}