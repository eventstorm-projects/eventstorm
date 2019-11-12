package eu.eventstorm.core.ex001.gen.domain;

import eu.eventstorm.core.AggregateId;

public final class UserAggregateId implements AggregateId {

	@Override
	public String name() {
		return "user";
	}

}
