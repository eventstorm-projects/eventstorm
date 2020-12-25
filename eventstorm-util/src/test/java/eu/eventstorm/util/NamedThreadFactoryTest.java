package eu.eventstorm.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

class NamedThreadFactoryTest {

	@Test
	void test() throws InterruptedException {
		NamedThreadFactory factory = new NamedThreadFactory("junit");
		ExecutorService es = Executors.newFixedThreadPool(1, factory);

		CountDownLatch countDown = new CountDownLatch(1);
		
		es.execute(() -> {
			assertEquals("junit-1", Thread.currentThread().getName());
			countDown.countDown();
		});
		
		countDown.await(1000, TimeUnit.MILLISECONDS);
	}
}
