package eu.eventstorm.batch.db;

import java.sql.Blob;

import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.FlywayRef;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Table;
import eu.eventstorm.sql.type.Json;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Table(value = "batch_resource", flywayRef = @FlywayRef(version = "1.0.0"))
public interface DatabaseResource {

	@PrimaryKey(value = "id", length = 36)
	String getId();

	void setId(String uuid);
	
	@Column(value = "meta", length = 400)
	Json getMeta();

	void setMeta(Json value);
	
	@Column(value = "content")
	Blob getContent();

	void setContent(Blob content);
	
}