package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class UpdateColumns {

    public enum MathematicalOperator {
        ADD('+'), SUB('-'), MUL('*'), DIV('/');

        private final char op;
        MathematicalOperator(char op) {
            this.op = op;
        }
    }

    private UpdateColumns(){}

    public static SqlColumn math(SqlSingleColumn column, MathematicalOperator operator, Number value) {
       return new UpdateColumn(column) {
           @Override
           String updateSql(Dialect dialect) {
               StringBuilder builder = new StringBuilder();
               dialect.wrap(builder, column, false);
               builder.append(operator.op);
               builder.append(value);
               return builder.toString();
           }
       };
    }

}
