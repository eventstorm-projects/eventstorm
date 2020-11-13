package eu.eventstorm.sql.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import eu.eventstorm.sql.Database;
import eu.eventstorm.sql.Dialect;
import eu.eventstorm.sql.TransactionManager;
import eu.eventstorm.sql.desc.SqlSequence;
import eu.eventstorm.sql.model.json.Module;
import eu.eventstorm.test.LoggerInstancePostProcessor;

@ExtendWith(LoggerInstancePostProcessor.class)
class DatabaseBuilderTest {

	@Test
	void testBuilderWithExternalConfig() {
		
		Module module = new Module("fake", "");
		SqlSequence sqlSequence = new SqlSequence("toto");
		
		Database db = DatabaseBuilder.from(Dialect.Name.H2)
			.withTransactionManager(Mockito.mock(TransactionManager.class))
			.withModuleAndExternalConfig(module)
			.withSequence(sqlSequence)
			.and().build();
		
		assertEquals(module, db.getModule(sqlSequence));
		
		db = DatabaseBuilder.from(Dialect.Name.H2)
				.withTransactionManager(Mockito.mock(TransactionManager.class))
				.withModule(module)
				.build();
		
		assertNull(db.getModule(sqlSequence));
	}
	
}
