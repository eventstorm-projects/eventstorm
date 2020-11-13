package eu.eventstorm.cqrs;

import reactor.core.scheduler.Scheduler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface EventLoop {

	Scheduler get(Command command);

	Scheduler post();

}
