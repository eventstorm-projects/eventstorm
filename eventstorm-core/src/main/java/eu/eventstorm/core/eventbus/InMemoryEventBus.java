package eu.eventstorm.core.eventbus;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventBus;
import eu.eventstorm.core.EventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventBus implements EventBus {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventBus.class);

	private final ImmutableList<Consumer<Event<? extends EventPayload>>> consumers;

	InMemoryEventBus(ImmutableList<Consumer<Event<? extends EventPayload>>> consumers) {
		this.consumers = consumers;
	}

	@Override
	public void publish(Event<EventPayload> event) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("InMemoryEventBus.publish({})", event);
		}
		this.consumers.forEach(consumer -> consumer.accept(event));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final ImmutableList.Builder<Consumer<Event<? extends EventPayload>>> eventBusBuilder;

		private Builder() {
			this.eventBusBuilder = ImmutableList.builder();
		}

		public Builder add(Consumer<Event<? extends EventPayload>> consumer) {
			this.eventBusBuilder.add(consumer);
			return this;
		}

		public InMemoryEventBus build() {
			return new InMemoryEventBus(eventBusBuilder.build());
		}
	}

}
