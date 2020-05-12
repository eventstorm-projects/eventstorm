package eu.eventstorm.eventbus;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class InMemoryEventBus implements EventBus {

	private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryEventBus.class);

	private final ImmutableList<Consumer<Event>> consumers;

	InMemoryEventBus(ImmutableList<Consumer<Event>> consumers) {
		this.consumers = consumers;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private final ImmutableList.Builder<Consumer<Event>> eventBusBuilder;

		private Builder() {
			this.eventBusBuilder = ImmutableList.builder();
		}

		public Builder add(Consumer<Event> consumer) {
			this.eventBusBuilder.add(consumer);
			return this;
		}

		public InMemoryEventBus build() {
			return new InMemoryEventBus(eventBusBuilder.build());
		}
	}

	@Override
	public void publish(ImmutableList<Event> events) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("InMemoryEventBus.publish({})", events);
		}
		//TODO correlation ...
		events.forEach(e -> consumers.forEach(c -> c.accept(e)));
	}

}
