package eu.eventstorm.sql;

import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Dialect {

    enum Name {
        H2
    }

    String nextVal(SqlSequence sequence);

    void wrap(Appendable appendable, SqlTable table, boolean alias);

    void wrap(Appendable appendable, SqlColumn targetColumn, boolean alias);

    String range(int offset, int limit);

    /*
    SqlXml fromJdbcSqlXml(ResultSet rs, int index) throws SQLException;

    SqlJson fromJdbcSqlJson(ResultSet rs, int index) throws SQLException;

    SqlXml toJdbcSqlXml(FastByteArrayInputStream is);

    SqlJson createSqlJson(Map<String,String> value);

    SqlJson createSqlJson(String value);
    */
}
