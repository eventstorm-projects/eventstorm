package eu.eventstorm.util;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

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
