package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class IntegerAggreateId implements AggregateId {

	private final int id;

	public IntegerAggreateId(int id) {
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
		if ((obj == null) || getClass() != obj.getClass()) {
			return false;
		}
		
		return this.id == ((IntegerAggreateId) obj).id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, false).append("id", id).toString();
	}
	
	
	
}
