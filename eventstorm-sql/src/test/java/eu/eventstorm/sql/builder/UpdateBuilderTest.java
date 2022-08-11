package eu.eventstorm.sql.builder;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.RawSqlExecutor;
import eu.eventstorm.sql.desc.SqlPrimaryKey;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.sql.builder.UpdateColumns.MathematicalOperator.ADD;
import static eu.eventstorm.sql.builder.UpdateColumns.MathematicalOperator.DIV;
import static eu.eventstorm.sql.builder.UpdateColumns.MathematicalOperator.MUL;
import static eu.eventstorm.sql.builder.UpdateColumns.MathematicalOperator.SUB;
import static eu.eventstorm.sql.builder.UpdateColumns.math;
import static eu.eventstorm.sql.dialect.Dialects.h2;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(LoggerInstancePostProcessor.class)
class UpdateBuilderTest {

    private static final SqlTable TABLE_T1 = new SqlTable("T1", "a");

    private static final SqlPrimaryKey COL_T1_01 = new SqlPrimaryKey(TABLE_T1, null, "col_T1_01");
    private static final SqlSingleColumn COL_T1_02 = new SqlSingleColumn(TABLE_T1, "col_T1_02", false, true, true);
    private static final SqlSingleColumn COL_T1_03 = new SqlSingleColumn(TABLE_T1, "col_T1_03", true, true, true);
    private static final SqlSingleColumn COL_T1_04 = new SqlSingleColumn(TABLE_T1, "col_T1_04", true, false, true);
    private static final SqlSingleColumn COL_T1_05 = new SqlSingleColumn(TABLE_T1, "col_T1_05", true, true, false);


    private Database database;

    @Test
    void testUpdate() {
        database = mock(Database.class);
        Module module = new Module("test") {
        };
        when(database.dialect()).thenReturn(h2(database));
        when(database.getModule(TABLE_T1)).thenReturn(module);
        when(database.rawSqlExecutor()).thenReturn(Mockito.mock(RawSqlExecutor.class));

        UpdateBuilder builder = new UpdateBuilder(database, TABLE_T1, of(COL_T1_02));
        builder.where(eq(COL_T1_01));
        assertEquals("UPDATE T1 SET col_T1_02=? WHERE col_T1_01=?", builder.build().sql());
    }

    @Test
    void testUpdateMathIncrement() {
        database = mock(Database.class);
        Module module = new Module("test") {
        };
        when(database.dialect()).thenReturn(h2(database));
        when(database.getModule(TABLE_T1)).thenReturn(module);
        when(database.rawSqlExecutor()).thenReturn(Mockito.mock(RawSqlExecutor.class));

        UpdateBuilder builder = new UpdateBuilder(database, TABLE_T1, of(math(COL_T1_02, ADD, 1)));
        builder.where(eq(COL_T1_01));
        assertEquals("UPDATE T1 SET col_T1_02=col_T1_02+1 WHERE col_T1_01=?", builder.build().sql());

        builder = new UpdateBuilder(database, TABLE_T1, of(math(COL_T1_02, DIV, 1.1)));
        builder.where(eq(COL_T1_01));
        assertEquals("UPDATE T1 SET col_T1_02=col_T1_02/1.1 WHERE col_T1_01=?", builder.build().sql());

        builder = new UpdateBuilder(database, TABLE_T1, of(math(COL_T1_02, SUB, 1234567891)));
        builder.where(eq(COL_T1_01));
        assertEquals("UPDATE T1 SET col_T1_02=col_T1_02-1234567891 WHERE col_T1_01=?", builder.build().sql());

        builder = new UpdateBuilder(database, TABLE_T1, of(math(COL_T1_02, MUL, 8)));
        builder.where(eq(COL_T1_01));
        assertEquals("UPDATE T1 SET col_T1_02=col_T1_02*8 WHERE col_T1_01=?", builder.build().sql());
    }

}