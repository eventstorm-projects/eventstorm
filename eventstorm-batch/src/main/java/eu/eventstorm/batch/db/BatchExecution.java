package eu.eventstorm.batch.db;

import java.sql.Timestamp;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.CreateTimestamp;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.type.Json;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Table(value = "batch_execution")
public interface BatchExecution {

	@PrimaryKey("id")
	long getId();

	void setId(long id);
	
	@Column(value = "uuid", length = 36)
	String getUuid();

	void setUuid(String uuid);
	
	@Column(value = "uuid", length = 36)
	String getResource();

	void setResource(String uuid);

	@Column(value = "name", length = 255)
	String getName();

	void setName(String value);

	@Column(value = "status")
	byte getStatus();

	void setStatus(byte status);

	@Column(value = "created_at")
	@CreateTimestamp
	Timestamp getCreatedAt();

	void setCreatedAt(Timestamp createdAt);

	@Column(value = "started_at", nullable = true)
	Timestamp getStartedAt();

	void setStartedAt(Timestamp startedAt);

	@Column(value = "ended_at", nullable = true)
	Timestamp getEndedAt();

	void setEndedAt(Timestamp endedAt);
	
	@Column(value = "log")
	Json getLog();
	
	void setLog(Json json);

}
