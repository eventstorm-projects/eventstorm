package eu.eventstorm.util;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import eu.eventstorm.test.LoggerInstancePostProcessor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ExtendWith(LoggerInstancePostProcessor.class)
class JvmTest {

	@Test
	void testWellFormed() throws Exception {
		assertUtilClassIsWellDefined(Jvm.class);
	}

}
