package eu.eventstorm.batch.rest;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public final class DatabaseResourceQuery {

	private final String id;
	private final String meta;
	private final String createdBy;
	private final OffsetDateTime createdAt;
	public DatabaseResourceQuery(String id, String meta, String createdBy, Timestamp createdAt) {
		this.id = id;
		this.meta = meta;
		this.createdBy = createdBy;
		this.createdAt = OffsetDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault());
	}
	
	public String getId() {
		return id;
	}
	
	public String getMeta() {
		return meta;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	
}
