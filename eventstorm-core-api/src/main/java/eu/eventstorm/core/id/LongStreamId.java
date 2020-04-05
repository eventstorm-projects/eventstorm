package eu.eventstorm.core.id;

import eu.eventstorm.core.StreamId;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class LongStreamId implements StreamId {

    private final long id;

    public LongStreamId(long id) {
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

        if ((obj == null) || LongStreamId.class != obj.getClass()) {
            return false;
        }

        return this.id == ((LongStreamId) obj).id;
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
