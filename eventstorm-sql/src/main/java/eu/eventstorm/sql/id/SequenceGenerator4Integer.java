package eu.eventstorm.sql.id;

import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;

/**
 * Created by jmilitello on 03/04/2017.
 */
public final class SequenceGenerator4Integer extends SequenceGenerator<Integer> {


    public SequenceGenerator4Integer(Database database, SqlSequence sequence) {
        super(database, sequence);
    }

    @Override
    protected Integer extractResult(ResultSet rs) throws SQLException {
        return Integer.valueOf(rs.getInt(1));
    }
}
