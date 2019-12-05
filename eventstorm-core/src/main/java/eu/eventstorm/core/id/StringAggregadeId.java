package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.util.ToStringBuilder;

final class StringAggregadeId implements AggregateId {

	private final String id;

	public StringAggregadeId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (StringAggregadeId.class == obj.getClass()) {
			return this.id.equals(((StringAggregadeId)obj).id);
		}
		if (AggregateId.class.isAssignableFrom(obj.getClass())) {
			return this.id.equals(((AggregateId)obj).toStringValue());
		}
		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, false).append("id", id).toString();
	}

	@Override
	public String toStringValue() {
		return this.id;
	}
	
}
