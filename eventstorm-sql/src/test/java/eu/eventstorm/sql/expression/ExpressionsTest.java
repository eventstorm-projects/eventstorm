package eu.eventstorm.sql.expression;

import static eu.eventstorm.sql.expression.Expressions.and;
import static eu.eventstorm.sql.expression.Expressions.eq;
import static eu.eventstorm.sql.expression.Expressions.or;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.RawSqlExecutor;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.dialect.Dialects;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import eu.eventstorm.test.Tests;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class ExpressionsTest {

    private SqlTable table = new SqlTable("table_01", "a");
    
    private Dialect dialect;

    @BeforeEach
    void beforeEach() {
    	Database database = Mockito.mock(Database.class);
    	Mockito.when(database.rawSqlExecutor()).thenReturn(Mockito.mock(RawSqlExecutor.class));
    	dialect = Dialects.h2(database);
    }

    @Test
	void testConstructor() throws Exception {
		Tests.assertUtilClassIsWellDefined(Expressions.class);
    }

    @Test
    void testEq() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number=123", Expressions.eq(number, 123).build(dialect, true));
        assertEquals("number=123", Expressions.eq(number, 123).build(dialect, false));
        assertEquals("number=123", Expressions.eq(number, 123).toString());

        assertEquals("a.number='ABCD'", Expressions.eq(number, "ABCD").build(dialect, true));
        assertEquals("number='ABCD'", Expressions.eq(number, "ABCD").build(dialect, false));
        assertEquals("number='ABCD'", Expressions.eq(number, "ABCD").toString());

        assertEquals("a.number=true", Expressions.eq(number, true).build(dialect, true));
        assertEquals("number=false", Expressions.eq(number, false).build(dialect, false));
        assertEquals("number=false", Expressions.eq(number, false).toString());

        assertEquals("a.number=?", Expressions.eq(number).build(dialect, true));
        assertEquals("number=?", Expressions.eq(number).build(dialect, false));
        assertEquals("number=?", Expressions.eq(number).toString());

    }

    @Test
    void testNotEq() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number<>123", Expressions.notEq(number, 123).build(dialect, true));
        assertEquals("number<>123", Expressions.notEq(number, 123).build(dialect, false));
        assertEquals("number<>123", Expressions.notEq(number, 123).toString());

        assertEquals("a.number<>'ABCD'", Expressions.notEq(number, "ABCD").build(dialect, true));
        assertEquals("number<>'ABCD'", Expressions.notEq(number, "ABCD").build(dialect, false));
        assertEquals("number<>'ABCD'", Expressions.notEq(number, "ABCD").toString());

        assertEquals("a.number<>true", Expressions.notEq(number, true).build(dialect, true));
        assertEquals("number<>false", Expressions.notEq(number, false).build(dialect, false));
        assertEquals("number<>false", Expressions.notEq(number, false).toString());

        assertEquals("a.number<>?", Expressions.notEq(number).build(dialect, true));
        assertEquals("number<>?", Expressions.notEq(number).build(dialect, false));
        assertEquals("number<>?", Expressions.notEq(number).toString());

    }

    @Test
    void testGe() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number>=123", Expressions.ge(number, 123).build(dialect, true));
        assertEquals("number>=123", Expressions.ge(number, 123).build(dialect, false));
        assertEquals("number>=123", Expressions.ge(number, 123).toString());

        assertEquals("a.number>=?", Expressions.ge(number).build(dialect, true));
        assertEquals("number>=?", Expressions.ge(number).build(dialect, false));
        assertEquals("number>=?", Expressions.ge(number).toString());

    }

    @Test
    void testLe() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number<=123", Expressions.le(number, 123).build(dialect, true));
        assertEquals("number<=123", Expressions.le(number, 123).build(dialect, false));
        assertEquals("number<=123", Expressions.le(number, 123).toString());

        assertEquals("a.number<=?", Expressions.le(number).build(dialect, true));
        assertEquals("number<=?", Expressions.le(number).build(dialect, false));
        assertEquals("number<=?", Expressions.le(number).toString());

    }

    @Test
    void testGt() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number>123", Expressions.gt(number, 123).build(dialect, true));
        assertEquals("number>123", Expressions.gt(number, 123).build(dialect, false));
        assertEquals("number>123", Expressions.gt(number, 123).toString());

        assertEquals("a.number>?", Expressions.gt(number).build(dialect, true));
        assertEquals("number>?", Expressions.gt(number).build(dialect, false));
        assertEquals("number>?", Expressions.gt(number).toString());

    }

    @Test
    void testLt() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number<123", Expressions.lt(number, 123).build(dialect, true));
        assertEquals("number<123", Expressions.lt(number, 123).build(dialect, false));
        assertEquals("number<123", Expressions.lt(number, 123).toString());

        assertEquals("a.number<?", Expressions.lt(number).build(dialect, true));
        assertEquals("number<?", Expressions.lt(number).build(dialect, false));
        assertEquals("number<?", Expressions.lt(number).toString());

    }

    @Test
    void testIn() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number IN (?)", Expressions.in(number, 1).build(dialect, true));
        assertEquals("number IN (?)", Expressions.in(number, 1).build(dialect, false));
        assertEquals("a.number IN (?,?,?)", Expressions.in(number, 3).build(dialect, true));
        assertEquals("number IN (?,?,?)", Expressions.in(number, 3).build(dialect, false));
        assertEquals("number IN (?,?,?)", Expressions.in(number, 3).toString());

    }

    @Test
    void testLike() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("a.number LIKE ?", Expressions.like(number).build(dialect, true));
        assertEquals("number LIKE ?", Expressions.like(number).build(dialect, false));
        assertEquals("number LIKE ?", Expressions.like(number).toString());

    }

    @Test
    void testAnd() {
        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);
        assertEquals("(a.number=123 AND a.number=124)", and(eq(number, 123),eq(number, 124)).build(dialect, true));
        assertEquals("(a.number=123 AND a.number=124 AND a.number=125)", and(eq(number, 123),eq(number, 124),eq(number, 125)).build(dialect, true));
        assertEquals("(number=123 AND number=124)", and(eq(number, 123),eq(number, 124)).build(dialect, false));
        assertEquals("(number=123 AND number=124)", and(eq(number, 123),eq(number, 124)).toString());
        assertEquals("(number=123 AND number=124 AND number=125)", and(eq(number, 123),eq(number, 124),eq(number, 125)).toString());
    }

    @Test
    void testOr() {
        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);
        assertEquals("(a.number=123 OR a.number=124)", or(eq(number, 123),eq(number, 124)).build(dialect, true));
        assertEquals("(number=123 OR number=124)", or(eq(number, 123),eq(number, 124)).build(dialect, false));
        assertEquals("(number=123 OR number=124)", or(eq(number, 123),eq(number, 124)).toString());
    }

}
