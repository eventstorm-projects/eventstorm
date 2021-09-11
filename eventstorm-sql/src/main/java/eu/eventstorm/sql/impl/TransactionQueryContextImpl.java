package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.EventstormSqlException;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.util.ToStringBuilder;

import java.sql.PreparedStatement;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionQueryContextImpl implements TransactionQueryContext {

    private final PreparedStatement ps;

    public TransactionQueryContextImpl(PreparedStatement ps) {
        this.ps = ps;
    }

    @Override
    public void close() {
    }

    @Override
    public PreparedStatement preparedStatement() {
        return this.ps;
    }

    @Override
    public <T extends EventstormSqlException> T exception(T cause) {
        return cause;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(false)
                .append("preparedStatement", ps)
                .toString();
    }


}
