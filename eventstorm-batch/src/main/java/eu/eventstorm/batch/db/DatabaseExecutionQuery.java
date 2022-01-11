package eu.eventstorm.batch.db;

import eu.eventstorm.annotation.CqrsQueryDatabaseProperty;
import eu.eventstorm.annotation.CqrsQueryDatabaseView;
import eu.eventstorm.annotation.Json;
import eu.eventstorm.sql.annotation.View;
import eu.eventstorm.sql.annotation.ViewColumn;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@CqrsQueryDatabaseView(view = @View(value = "batch_execution", pageable = true))
public interface DatabaseExecutionQuery {

	@CqrsQueryDatabaseProperty(column = @ViewColumn("uuid"))
	String getUuid();

	@CqrsQueryDatabaseProperty(column = @ViewColumn("event"), json = @Json(raw = true))
	String getEvent();

	@CqrsQueryDatabaseProperty(column = @ViewColumn("name"))
	String getName();

	@CqrsQueryDatabaseProperty(column = @ViewColumn("status"))
	String getStatus();

	@CqrsQueryDatabaseProperty(column = @ViewColumn("created_by"))
	String getCreatedBy();

	@CqrsQueryDatabaseProperty(column = @ViewColumn("created_at"))
	Timestamp getCreatedAt();

	@CqrsQueryDatabaseProperty(column = @ViewColumn(value = "started_at", nullable = true))
	Timestamp getStartedAt();

	@CqrsQueryDatabaseProperty(column = @ViewColumn(value = "ended_at", nullable = true))
	Timestamp getEndedAt();

}
