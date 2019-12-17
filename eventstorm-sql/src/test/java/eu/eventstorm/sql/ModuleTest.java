package eu.eventstorm.sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.desc.SqlTable;
import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class ModuleTest {

	@Test
	void testWithPrefix() {
		Module module = new Module("name", "catalog") {
		};

		SqlSequence sequence = new SqlSequence("sequence");
		SqlTable table = new SqlTable("table", "a");

		assertEquals("catalog.sequence", module.getSequenceName(sequence));
		assertEquals("catalog.table", module.getTableName(table));

		module = new Module("name", "") {
		};

		assertEquals("sequence", module.getSequenceName(sequence));
		assertEquals("table", module.getTableName(table));
		
		module = new Module("name", "catalog", "prefix_") {
		};
		
		assertEquals("catalog.prefix_sequence", module.getSequenceName(sequence));
		assertEquals("catalog.prefix_table", module.getTableName(table));
		
		module = new Module("name", "", "prefix_") {
		};
		
		assertEquals("prefix_sequence", module.getSequenceName(sequence));
		assertEquals("prefix_table", module.getTableName(table));
	}

}
