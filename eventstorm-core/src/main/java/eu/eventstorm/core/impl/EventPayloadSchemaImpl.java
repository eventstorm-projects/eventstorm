package eu.eventstorm.core.impl;

import eu.eventstorm.core.EventPayloadSchema;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class EventPayloadSchemaImpl implements EventPayloadSchema {

	private final String name;
	private final int version;
	
	EventPayloadSchemaImpl(String name, int version) {
		this.name = name;
		this.version = version;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getVersion() {
		return this.version;
	}

}
