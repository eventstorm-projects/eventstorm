package eu.eventstorm.batch.db;

import eu.eventstorm.batch.BatchStatus;
import eu.eventstorm.page.Page;
import eu.eventstorm.page.PageRequest;
import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.SqlQueryPageable;
import eu.eventstorm.sql.builder.Order;
import eu.eventstorm.sql.expression.Expressions;

import java.sql.Date;
import java.time.LocalDate;
import java.util.stream.Stream;

import static eu.eventstorm.batch.db.DatabaseExecutionQueryDescriptor.ALL;
import static eu.eventstorm.batch.db.DatabaseExecutionQueryDescriptor.NAME;
import static eu.eventstorm.batch.db.DatabaseExecutionQueryDescriptor.STARTED_AT;
import static eu.eventstorm.batch.db.DatabaseExecutionQueryDescriptor.STATUS;
import static eu.eventstorm.batch.db.DatabaseExecutionQueryDescriptor.VIEW;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.expression.Expressions.ge;
import static eu.eventstorm.sql.expression.Expressions.le;
import static eu.eventstorm.sql.expression.Expressions.lt;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseExecutionQueryRepository extends eu.eventstorm.sql.Repository {

    private final SqlQuery findById;
    private final SqlQuery findLastStartedAtByName;
    private final SqlQuery findAllByDate;
    private final SqlQueryPageable pageable;

    public DatabaseExecutionQueryRepository(Database database) {
        super(database);
        this.findById = select(ALL).from(VIEW).where(eq(DatabaseExecutionQueryDescriptor.UUID)).build();
        this.findLastStartedAtByName = select(ALL).from(VIEW)
                .where(and(eq(NAME), eq(STATUS)))
                .orderBy(Order.desc(STARTED_AT))
                .limit(1)
                .build();

        this.findAllByDate = select(ALL).from(VIEW)
                .where(and(ge(STARTED_AT), lt(STARTED_AT)))
                .orderBy(Order.desc(STARTED_AT))
                .build();

        this.pageable = select(ALL).from(VIEW).pageable().build();
    }

    public DatabaseExecutionQuery findLastStartedAtByName(String name, BatchStatus status) {
        return executeSelect(this.findLastStartedAtByName, ps -> {
            ps.setString(1, name);
            ps.setString(2, status.name());
        }, QueryViewMappers.DATABASE_EXECUTION_QUERY);
    }

    public Stream<DatabaseExecutionQuery> findAllByDate(LocalDate date) {
        return stream(this.findAllByDate, ps -> {
            ps.setDate(1, Date.valueOf(date));
            ps.setDate(2, Date.valueOf(date.plusDays(1)));
        }, QueryViewMappers.DATABASE_EXECUTION_QUERY);
    }

    public DatabaseExecutionQuery findById(java.lang.String uuid) {
        return executeSelect(this.findById, ps -> {
            ps.setString(1, uuid);
        }, QueryViewMappers.DATABASE_EXECUTION_QUERY);
    }

    public Page<DatabaseExecutionQuery> findBy(PageRequest pageRequest) {
        return executeSelectPage(pageable, QueryViewMappers.DATABASE_EXECUTION_QUERY, pageRequest);
    }

}
