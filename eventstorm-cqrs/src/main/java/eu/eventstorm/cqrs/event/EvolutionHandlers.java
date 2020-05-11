package eu.eventstorm.cqrs.event;

import com.google.common.collect.ImmutableList;

import eu.eventstorm.core.Event;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class EvolutionHandlers implements EvolutionHandler {

	private final ImmutableList<EvolutionHandler> handlers;

	public EvolutionHandlers(ImmutableList<EvolutionHandler> handlers) {
		this.handlers = handlers;
	}

	@Override
	public void on(Event event) {
		
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
