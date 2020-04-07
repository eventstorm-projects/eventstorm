package eu.eventstorm.core.id;

import eu.eventstorm.core.StreamId;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class IntegerStreamId implements StreamId {

	private final int id;

	public IntegerStreamId(int id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || IntegerStreamId.class != obj.getClass()) {
			return false;
		}
		
		return this.id == ((IntegerStreamId) obj).id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, false).append("id", id).toString();
	}

	@Override
	public String toStringValue() {
		return String.valueOf(id);
	}

    int getId() {
        return this.id;
    }
	
}
