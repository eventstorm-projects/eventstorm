package eu.eventstorm.cqrs.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EvolutionHandlers implements EvolutionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EvolutionHandlers.class);
	
	private final ImmutableList<EvolutionHandler> handlers;

	public EvolutionHandlers(ImmutableList<EvolutionHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public void on(Event event) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("on ({})", event);
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("handlers -> ", handlers);
		}
		
		this.handlers.forEach(h -> h.on(event));

	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private final ImmutableList.Builder<EvolutionHandler> builder;

		private Builder() {
			this.builder = ImmutableList.builder();
		}

		public Builder add(EvolutionHandler evolutionHandler) {
			this.builder.add(evolutionHandler);
			return this;
		}

		public EvolutionHandlers build() {
			return new EvolutionHandlers(builder.build());
		}

	}

}
