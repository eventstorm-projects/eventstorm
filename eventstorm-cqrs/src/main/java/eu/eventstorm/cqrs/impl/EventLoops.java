package eu.eventstorm.cqrs.impl;

import com.google.common.collect.ImmutableMap;

import eu.eventstorm.cqrs.EventLoop;
import reactor.core.scheduler.Scheduler;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EventLoops {

	private EventLoops() {
	}
	
	public static EventLoop single(Scheduler validationScheduler, Scheduler scheduler, Scheduler postScheduler) {
		return new SingleEventLoop(validationScheduler, scheduler, postScheduler);
	}
	
	public static EventLoop multiple(Scheduler scheduler, ImmutableMap<String, Scheduler> others, Scheduler postScheduler, Scheduler validationScheduler) {
		return new MultipleEventLoop(validationScheduler, scheduler, others, postScheduler);
	}
	
	public static Builder newBuilder() {
		return new Builder();
	}
	
	public static class Builder {

		private Scheduler validation;
		private Scheduler scheduler;
		private Scheduler post;
		private final ImmutableMap.Builder<String, Scheduler> others = ImmutableMap.builder();
		
		private Builder() {
		}
		
		public Builder withDefault(Scheduler scheduler) {
			this.scheduler = scheduler;
			return this;
		}
		
		public Builder withPost(Scheduler scheduler) {
			this.post = scheduler;
			return this;
		}

		public Builder withValidation(Scheduler scheduler) {
			this.validation = scheduler;
			return this;
		}
		
		public Builder with(String classname, Scheduler scheduler) {
			this.others.put(classname, scheduler);
			return this;
		}
		
		public EventLoop build() {
			ImmutableMap<String, Scheduler> all = this.others.build();
			if (all.size() == 0) {
				return new SingleEventLoop(validation, this.scheduler, post);
			} else {
				return new MultipleEventLoop(validation, this.scheduler, all, post);
			}
		}
	}
}
