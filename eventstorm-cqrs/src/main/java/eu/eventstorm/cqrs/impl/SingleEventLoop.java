package eu.eventstorm.cqrs.impl;

import eu.eventstorm.cqrs.Command;
import eu.eventstorm.cqrs.EventLoop;
import reactor.core.scheduler.Scheduler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class SingleEventLoop implements EventLoop {

    private final Scheduler validationScheduler;
    private final Scheduler defaultScheduler;

    private final Scheduler postScheduler;

    public SingleEventLoop(Scheduler validationScheduler, Scheduler defaultScheduler, Scheduler postScheduler) {
        this.validationScheduler = validationScheduler;
        this.defaultScheduler = defaultScheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public Scheduler get(Command command) {
        return defaultScheduler;
    }

    @Override
    public Scheduler post() {
        return postScheduler;
    }

    @Override
    public Scheduler validation() {
        return validationScheduler;
    }

}
