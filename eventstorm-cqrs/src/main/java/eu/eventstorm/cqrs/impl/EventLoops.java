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
	
	public static EventLoop single(Scheduler scheduler, Scheduler postScheduler) {
		return new SingleEventLoop(scheduler, postScheduler);
	}
	
	public static EventLoop multiple(Scheduler scheduler, ImmutableMap<String, Scheduler> others, Scheduler postScheduler) {
		return new MultipleEventLoop(scheduler, others, postScheduler);
	}
	
	public static Builder newBuider() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Scheduler scheduler;
		private Scheduler post;
		private ImmutableMap.Builder<String, Scheduler> others = ImmutableMap.builder();
		
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
		
		public Builder with(String classname, Scheduler scheduler) {
			this.others.put(classname, scheduler);
			return this;
		}
		
		public EventLoop build() {
			ImmutableMap<String, Scheduler> others = this.others.build();
			if (others.size() == 0) {
				return new SingleEventLoop(this.scheduler, post);
			} else {
				return new MultipleEventLoop(this.scheduler, others, post);
			}
		}
	}
}
