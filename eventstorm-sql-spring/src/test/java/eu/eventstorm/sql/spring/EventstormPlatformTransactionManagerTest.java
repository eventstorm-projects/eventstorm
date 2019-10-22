package eu.eventstorm.sql.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = EventstormPlatformTransactionManagerConfigurationTest.class)
class EventstormPlatformTransactionManagerTest {

	@Test
	void test() {
		
	}
}
