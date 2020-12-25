package eu.eventstorm.util;

import static eu.eventstorm.test.Tests.assertUtilClassIsWellDefined;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
class BuffersTest {

	@SuppressWarnings("restriction")
	@Test
	void testClean() {

		//jdk.internal.misc.SharedSecrets.getJavaNioAccess().getDirectBufferPool()
       /* sun.misc.JavaNioAccess.BufferPool pool = sun.misc.SharedSecrets.getJavaNioAccess().getDirectBufferPool();

        long start = pool.getMemoryUsed();

        ByteBuffer buffer = ByteBuffer.allocateDirect(1_000_000);

        assertEquals(start+1_000_000 , pool.getMemoryUsed());

        Buffers.releaseDirectByteBuffer(buffer);

        assertEquals(start, pool.getMemoryUsed());*/

	}

	@Test
	void testWellFormed() throws Exception {
		assertUtilClassIsWellDefined(Buffers.class);
	}
}
