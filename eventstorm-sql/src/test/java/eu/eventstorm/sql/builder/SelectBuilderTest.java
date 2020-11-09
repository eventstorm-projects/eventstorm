package eu.eventstorm.sql.builder;

import static com.google.common.collect.ImmutableList.of;
import static eu.eventstorm.sql.dialect.Dialects.h2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlColumn;
import eu.eventstorm.sql.desc.SqlSingleColumn;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.sql.expression.AggregateFunction;
import eu.eventstorm.sql.expression.AggregateFunctions;
import eu.eventstorm.sql.expression.Expression;
import eu.eventstorm.sql.expression.Expressions;
import eu.eventstorm.sql.expression.OverPartition;
import eu.eventstorm.sql.expression.OverPartitions;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class SelectBuilderTest {

	private static final SqlTable TABLE_T1 = new SqlTable("T1", "a");
	private static final SqlTable TABLE_T2 = new SqlTable("T2", "b");
	private static final SqlTable TABLE_T3 = new SqlTable("T3", "c");

	private static final SqlColumn COL_T1_01 = new SqlSingleColumn(TABLE_T1, "col_T1_01", false, true, true);
	private static final SqlColumn COL_T1_02 = new SqlSingleColumn(TABLE_T1, "col_T1_02", false, true, true);
	private static final SqlColumn COL_T1_03 = new SqlSingleColumn(TABLE_T1, "col_T1_03", false, true, true);

	private Database database;

	@BeforeEach
	void before() {
		database = Mockito.mock(Database.class);

		Module module = new Module("test") {
		};
		when(database.dialect()).thenReturn(h2(database));
		when(database.getModule(TABLE_T1)).thenReturn(module);
		when(database.getModule(TABLE_T2)).thenReturn(module);
		when(database.getModule(TABLE_T3)).thenReturn(module);
	}

	@Test
	void testSelect() {
		SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02, COL_T1_03));
		builder.from(TABLE_T1);

		SelectBuilderFromSubSelect sbuilder = new SelectBuilderFromSubSelect(database,
				SubSelects.from(builder.build()));

		assertEquals("SELECT * FROM (SELECT col_T1_01,col_T1_02,col_T1_03 FROM T1)", sbuilder.build().sql());
	}

	@Test
	void testSelectWithOver() {
		SelectBuilder builder = new SelectBuilder(database, of(COL_T1_01, COL_T1_02),
				AggregateFunctions.rowNumber(OverPartitions.by(COL_T1_03, "maxRowNumber")));
		builder.from(TABLE_T1);

		SelectBuilderFromSubSelect sbuilder = new SelectBuilderFromSubSelect(database,
				SubSelects.from(builder.build()));

		assertEquals(
				"SELECT * FROM (SELECT col_T1_01,col_T1_02,ROW_NUMBER() OVER (PARTITION BY col_T1_03) maxRowNumber FROM T1)",
				sbuilder.build().sql());

		sbuilder.where(Expressions.raw("maxRowNumber = 1"));
		
		assertEquals(
				"SELECT * FROM (SELECT col_T1_01,col_T1_02,ROW_NUMBER() OVER (PARTITION BY col_T1_03) maxRowNumber FROM T1) WHERE maxRowNumber = 1",
				sbuilder.build().sql());
	}

}