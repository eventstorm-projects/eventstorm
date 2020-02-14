package eu.eventstorm.core.id;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class LongAggregateId implements AggregateId {

    private final long id;

    public LongAggregateId(long id) {
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

        if ((obj == null) || LongAggregateId.class != obj.getClass()) {
            return false;
        }

        return this.id == ((LongAggregateId) obj).id;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, false).append("id", id).toString();
    }

    @Override
    public String toStringValue() {
        return String.valueOf(id);
    }

    long getId() {
        return this.id;
    }

}
