package eu.eventstorm.sql.jdbc;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ResultSetMappers {

    public static final ResultSetMapper<Long> SINGLE_LONG = (dialect, rs) -> {
        long value = rs.getLong(1);
        if (rs.next()) {
            throw new ResultSetMapperException("more than one result");
        }
        return value;
    };

    public static final ResultSetMapper<String> STRING = (dialect, rs) -> {
        return rs.getString(1);
    };

    private ResultSetMappers() {
    }


}
