package eu.eventstorm.sql.desc;

public final class Projections {

    private Projections() {
    }

    public static SqlColumn count(SqlColumn column) {
        return new SqlFunctionColumn("COUNT", column);
    }


}
