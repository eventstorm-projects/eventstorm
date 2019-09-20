package eu.eventstorm.sql.id;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.tx.TransactionSynchronizationManager;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class SequenceGenerator<T> implements Identifier<T> {

    private final Database database;

    private final String sequence;

    public SequenceGenerator(Database database, SqlSequence sequence) {
        this.database = database;
        this.sequence = database.dialect().nextVal(sequence);
    }

    public final T next() {
        PreparedStatement ps = TransactionSynchronizationManager.current(database).read(this.sequence);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return extractResult(rs);
            } else {
                throw new IdentifierException("NO SEQUENCE");
            }
        } catch (SQLException cause) {
            throw new SequenceGeneratorException("SequenceGenerator next fails", cause);
        }
    }

    protected abstract T extractResult(ResultSet rs) throws SQLException;

    protected String getSequenceAsSql() {
        return this.sequence;
    }
}
