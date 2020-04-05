package eu.eventstorm.eventstore.db;

import eu.eventstorm.sql.annotation.BusinessKey;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.PrimaryKey;
import eu.eventstorm.sql.annotation.Table;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Table(value = "event_definition")
interface DatabaseEventDefinition {

	@PrimaryKey("id")
	int getId();

	void setId(int id);

	@BusinessKey
	@Column(value = "aggregate_type", length = 128)
	String getAggregateType();

	void setAggregateType(String type);
	
	@BusinessKey
	@Column(value = "version")
	int getVersion();
	
	void setVersion(int revision);
	
	
	
}