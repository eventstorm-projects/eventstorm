package eu.eventstorm.core.id;

import eu.eventstorm.core.StreamId;
import eu.eventstorm.util.ToStringBuilder;

public final class StringStreamId implements StreamId {

	private final String id;

	StringStreamId(String id) {
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
		if (StringStreamId.class == obj.getClass()) {
			return this.id.equals(((StringStreamId)obj).id);
		}
		if (StreamId.class.isAssignableFrom(obj.getClass())) {
			return this.id.equals(((StreamId)obj).toStringValue());
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
