package eu.eventstorm.sql.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.RawSqlExecutor;
import eu.eventstorm.sql.builder.Order;
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
class AggregateFunctionsTest {

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
		Tests.assertUtilClassIsWellDefined(AggregateFunctions.class);
    }

    @Test
    void testCount() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("count(a.number)", AggregateFunctions.count(number).build(dialect, true));
        assertEquals("count(number)", AggregateFunctions.count(number).build(dialect, false));

    }

    @Test
    void testDistinct() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("distinct(a.number)", AggregateFunctions.distinct(number).build(dialect, true));
        assertEquals("distinct(number)", AggregateFunctions.distinct(number).build(dialect, false));

    }

    @Test
    void testMax() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("max(a.number)", AggregateFunctions.max(number).build(dialect, true));
        assertEquals("max(number)", AggregateFunctions.max(number).build(dialect, false));

    }
    
    @Test
    void testRowNumber() {

        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);

        assertEquals("ROW_NUMBER()", AggregateFunctions.rowNumber().build(dialect, true));
        assertEquals("ROW_NUMBER() OVER (PARTITION BY number) toto", AggregateFunctions.rowNumber(OverPartitions.by(number, "toto")).build(dialect, false));
        assertEquals("ROW_NUMBER() OVER (PARTITION BY a.number) toto", AggregateFunctions.rowNumber(OverPartitions.by(number, "toto")).build(dialect, true));

        assertEquals("ROW_NUMBER() OVER (PARTITION BY number ORDER BY number DESC) toto", AggregateFunctions.rowNumber(OverPartitions.by(number, Order.desc(number), "toto")).build(dialect, false));
        assertEquals("ROW_NUMBER() OVER (PARTITION BY a.number ORDER BY a.number DESC) toto", AggregateFunctions.rowNumber(OverPartitions.by(number, Order.desc(number), "toto")).build(dialect, true));

    }

}