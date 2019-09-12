package eu.eventstorm.sql.id;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;

/**
 * Created by jmilitello on 03/04/2017.
 */
public final class SequenceGenerator4Long extends SequenceGenerator<Long> {


    public SequenceGenerator4Long(Database database, SqlSequence sequence) {
        super(database, sequence);
    }

    @Override
    protected Long extractResult(ResultSet rs) throws SQLException {
        return rs.getLong(1);
    }
}
