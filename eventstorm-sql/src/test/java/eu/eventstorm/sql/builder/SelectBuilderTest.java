package eu.eventstorm.sql.builder;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.sql.dialect.Dialects.h2;
import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.RawSqlExecutor;
import eu.eventstorm.sql.SqlQuery;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class SelectBuilderTest {

	private static final SqlTable TABLE_T1 = new SqlTable("T1", "a");
	private static final SqlTable TABLE_T2 = new SqlTable("T2", "b");
	private static final SqlTable TABLE_T3 = new SqlTable("T3", "c");

	private static final SqlColumn COL_T1_01 = new SqlSingleColumn(TABLE_T1, "col_T1_01", false, true, true);
	private static final SqlColumn COL_T1_02 = new SqlSingleColumn(TABLE_T1, "col_T1_02", false, true, true);
	private static final SqlColumn COL_T1_03 = new SqlSingleColumn(TABLE_T1, "col_T1_03", false, true, true);

	private static final SqlColumn COL_T2_01 = new SqlSingleColumn(TABLE_T2, "col_T2_01", false, true, true);
	private static final SqlColumn COL_T2_02 = new SqlSingleColumn(TABLE_T2, "col_T2_02", false, true, true);
	private static final SqlColumn COL_T2_03 = new SqlSingleColumn(TABLE_T2, "col_T2_03", false, true, true);

	private static final SqlColumn COL_T3_01 = new SqlSingleColumn(TABLE_T3, "col_T3_01", false, true, true);
	private static final SqlColumn COL_T3_02 = new SqlSingleColumn(TABLE_T3, "col_T3_02", false, true, true);
	private static final SqlColumn COL_T3_03 = new SqlSingleColumn(TABLE_T3, "col_T3_03", false, true, true);

    private Database database;

    @BeforeEach
    void before() {
        database = Mockito.mock(Database.class);
        when(database.rawSqlExecutor()).thenReturn(Mockito.mock(RawSqlExecutor.class));

        Module module = new Module("test") {
		};
        when(database.dialect()).thenReturn(h2(database));
        when(database.getModule(TABLE_T1)).thenReturn(module);
        when(database.getModule(TABLE_T2)).thenReturn(module);
        when(database.getModule(TABLE_T3)).thenReturn(module);
        when(database.rawSqlExecutor()).thenReturn(Mockito.mock(RawSqlExecutor.class));
        
    }


    @Test
    void testSelect() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1", builder.<SqlQuery>build().sql());
    }

    @Test
    void testSelectLimit() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.limit(5);
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 FETCH FIRST 5 ROWS ONLY", builder.<SqlQuery>build().sql());
    }

    @Test
    void testSelectForUpdate() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.forUpdate();
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 FOR UPDATE", builder.<SqlQuery>build().sql());
    }

    @Test
    void testSelectOrderBy() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.orderBy(Order.asc(COL_T1_01));
        builder.from(TABLE_T1);
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 ORDER BY col_T1_01 ASC", builder.<SqlQuery>build().sql());

        builder.orderBy(Order.desc(COL_T1_02));
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 ORDER BY col_T1_01 ASC,col_T1_02 DESC", builder.<SqlQuery>build().sql());
    }

    @Test
    void testSelectWhere() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.where(eq(COL_T1_02));
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 WHERE col_T1_02=?", builder.<SqlQuery>build().sql());

        builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.where(and(eq(COL_T1_02), eq(COL_T1_03)));
        assertEquals("SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1 WHERE (col_T1_02=? AND col_T1_03=?)", builder.<SqlQuery>build().sql());
    }

    @Test
    void testLeftJoin() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.leftJoin(TABLE_T2, COL_T2_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 a LEFT JOIN T2 b ON b.col_T2_01=a.col_T1_01", builder.<SqlQuery>build().sql());
        builder.leftJoin(TABLE_T3, COL_T3_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 a LEFT JOIN T2 b ON b.col_T2_01=a.col_T1_01 LEFT JOIN T3 c ON c.col_T3_01=a.col_T1_01", builder.<SqlQuery>build().sql());

        builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.leftJoin(TABLE_T2, COL_T2_01, COL_T1_01, eq(COL_T2_02));
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 a LEFT JOIN T2 b ON b.col_T2_01=a.col_T1_01 AND b.col_T2_02=?", builder.<SqlQuery>build().sql());
    }

    @Test
    void testLeftJoinChangeAlias() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1);
        builder.leftJoin(TABLE_T2.as("toto"), COL_T2_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 a LEFT JOIN T2 toto ON toto.col_T2_01=a.col_T1_01", builder.<SqlQuery>build().sql());
        builder.leftJoin(TABLE_T3.as("tutu"), COL_T3_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 a LEFT JOIN T2 toto ON toto.col_T2_01=a.col_T1_01 LEFT JOIN T3 tutu ON tutu.col_T3_01=a.col_T1_01", builder.<SqlQuery>build().sql());
    }

    @Test
    void testLeftJoinAlias() {
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        builder.from(TABLE_T1, TABLE_T2);
        builder.leftJoin(TABLE_T3, COL_T3_01, COL_T1_01);
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03 FROM T1 a, T2 b LEFT JOIN T3 c ON c.col_T3_01=a.col_T1_01", builder.<SqlQuery>build().sql());
    }
    
    @Test
    void testLeftJoinAliasSubSelect() {
    	SubSelect subSelect = SubSelects.from(
    			new SelectBuilder(database, of(COL_T3_01, COL_T3_03, COL_T3_03)).from(TABLE_T3).build(),
    			"toto");
    	
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03, subSelect.column(COL_T3_01)));
        builder.from(TABLE_T1);
        builder.leftJoin(subSelect, COL_T3_01, TABLE_T1, COL_T1_01);
        
        assertEquals("SELECT a.col_T1_01,a.col_T1_02,a.col_T1_03,toto.col_T3_01 FROM T1 a LEFT JOIN (SELECT col_T3_01,col_T3_03,col_T3_03 FROM T3) toto ON toto.col_T3_01=a.col_T1_01", builder.<SqlQuery>build().sql());
    }

    @Test
    void testLeftJoinChecks() {

        // without from
        SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
        SqlBuilderException sbe = assertThrows(SqlBuilderException.class, () -> builder.leftJoin(TABLE_T2, COL_T2_01, COL_T1_01));


        SelectBuilder builder2 = new SelectBuilder(database, of(COL_T1_01, COL_T1_02));
        builder.from(TABLE_T1, TABLE_T2);
        sbe = assertThrows(SqlBuilderException.class, () -> builder2.leftJoin(TABLE_T2, COL_T2_01, TABLE_T2, COL_T2_01));

    }

}