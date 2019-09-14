package eu.eventstorm.util;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class JvmTest {

	@Test
	void testWellFormed() throws Exception {
		assertUtilClassIsWellDefined(Jvm.class);
	}
	
}
