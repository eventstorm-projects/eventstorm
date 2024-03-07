package eu.eventstorm.cqrs.ex001.gen.evolution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.eventstorm.core.Event;
import eu.eventstorm.cqrs.event.EvolutionHandler;

public class UserEvolutionHandler implements EvolutionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserEvolutionHandler.class);
	@Override
	public void on(Event event) {
		LOGGER.info("{}", event);
	}

	@Override
	public boolean isForMe(String stream) {
		return true;
	}

}
