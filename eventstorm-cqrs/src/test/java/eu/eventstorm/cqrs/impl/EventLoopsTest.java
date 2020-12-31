package eu.eventstorm.cqrs.impl;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.EventLoop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

class EventLoopsTest {

    @Test
    void multipleTest() throws InterruptedException {

        EventLoop eventLoop = EventLoops.newBuider()
                .withDefault(Schedulers.newSingle("default"))
                .withPost(Schedulers.newSingle("post"))
                .with(C01.class.getName(), Schedulers.newSingle("c01"))
                .build();

        Scheduler scheduler = eventLoop.get(new C01());
        scheduler.schedule(() -> {
            Assertions.assertEquals("c01-1", Thread.currentThread().getName());
        });
        scheduler = eventLoop.get(new C02());
        scheduler.schedule(() -> {
            Assertions.assertEquals("default", Thread.currentThread().getName().substring(0,7));
        });

        Thread.sleep(100);
    }

    private class C01 implements Command {

    }

    private class C02 implements Command {

    }
}
