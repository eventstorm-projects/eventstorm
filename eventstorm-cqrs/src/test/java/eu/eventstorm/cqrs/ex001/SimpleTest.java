package eu.eventstorm.cqrs.ex001;

import org.junit.jupiter.api.Test;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class SimpleTest {

	@Test
	void x() {
		
		Mono.just(10)
		.doOnEach(t -> System.out.println("A-->" + Thread.currentThread().getName()))
		.publishOn(Schedulers.single())
		.doOnEach(t -> System.out.println("B-->" + Thread.currentThread().getName()))
		.toProcessor()
		.doOnEach(t -> System.out.println("E-->" + Thread.currentThread().getName()))
		.map(i -> i * 2)
		.doOnEach(t -> System.out.println("E-->" + Thread.currentThread().getName()))
		.doOnEach(t -> System.out.println("E-->" + Thread.currentThread().getName()))
		
		.block();
	}
}
