package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class LongAggreateId implements AggregateId {

	private final long id;

	public LongAggreateId(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return (int) id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if ((obj == null) || LongAggreateId.class  != obj.getClass()) {
			return false;
		}
		
		return this.id == ((LongAggreateId) obj).id;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, false).append("id", id).toString();
	}
	
	
	
}
