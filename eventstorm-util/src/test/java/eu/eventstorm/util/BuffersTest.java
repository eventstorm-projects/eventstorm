package eu.eventstorm.util;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class BuffersTest {

	@Test
	void testClean() {

        sun.misc.JavaNioAccess.BufferPool pool = sun.misc.SharedSecrets.getJavaNioAccess().getDirectBufferPool();

        long start = pool.getMemoryUsed();

        ByteBuffer buffer = ByteBuffer.allocateDirect(1_000_000);

        Assertions.assertEquals(start+1_000_000 , pool.getMemoryUsed());

        Buffers.releaseDirectByteBuffer(buffer);

        Assertions.assertEquals(start, pool.getMemoryUsed());

	}

	@Test
	void testWellFormed() throws Exception {
		assertUtilClassIsWellDefined(Buffers.class);
	}
}
