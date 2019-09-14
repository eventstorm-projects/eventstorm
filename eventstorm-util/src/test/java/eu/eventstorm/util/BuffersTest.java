package eu.eventstorm.util;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class BuffersTest {

	@Test
	void testClean() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		Buffers.releaseDirectByteBuffer(buffer);
	}
	
	@Test
	void testWellFormed() throws Exception {
		assertUtilClassIsWellDefined(Buffers.class);
	}
}
