package eu.eventstorm.batch.db;

import java.sql.Blob;
import java.sql.Timestamp;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.CreateTimestamp;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.type.Json;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Table(value = "batch_resource")
public interface DatabaseResource {

	@PrimaryKey(value = "id", length = 36)
	String getId();

	void setId(String uuid);
	
	@Column(value = "meta", length = 2000)
	Json getMeta();

	void setMeta(Json value);
	
	@Column(value = "content")
	Blob getContent();

	void setContent(Blob content);
	
	@Column(value = "created_by", length = 64)
	String getCreatedBy();

	void setCreatedBy(String createdBy);
	
	@Column(value = "created_at")
	@CreateTimestamp
	Timestamp getCreatedAt();

	void setCreatedAt(Timestamp createdAt);

	
}