package eu.eventstorm.sql.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.dialect.Dialects;
import eu.eventstorm.test.Tests;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class ExpressionsTest {

    private SqlTable table = new SqlTable("table_01", "a");
    private Dialect dialect = Dialects.h2(Mockito.mock(Database.class));


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
}
