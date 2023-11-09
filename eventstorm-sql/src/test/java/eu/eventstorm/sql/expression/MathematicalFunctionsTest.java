package eu.eventstorm.sql.expression;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class MathematicalFunctionsTest {

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
		Tests.assertUtilClassIsWellDefined(MathematicalFunctions.class);
    }

    @Test
    void testAdd() {
        SqlColumn number = new SqlSingleColumn(table, "number", false, true, true);
        assertEquals("a.number+1", MathematicalFunctions.add(number, 1).build(dialect, true));
        assertEquals("number+1", MathematicalFunctions.add(number, 1).build(dialect, false));

    }

}