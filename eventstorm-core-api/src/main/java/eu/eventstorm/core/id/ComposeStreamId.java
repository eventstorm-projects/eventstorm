package eu.eventstorm.core.id;

import com.google.common.base.Objects;

import eu.eventstorm.core.StreamId;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ComposeStreamId implements StreamId{

    private final StreamId id1;
    private final StreamId id2;
    
    ComposeStreamId(StreamId id1, StreamId id2) {
        this.id1 = id1;
        this.id2 = id2;
    }
    
    public StreamId getId1() {
        return id1;
    }

    public StreamId getId2() {
        return id2;
    }

    @Override
    public int hashCode() {
        return id1.hashCode() * id2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || ComposeStreamId.class != obj.getClass()) {
            return false;
        }

        ComposeStreamId cai = (ComposeStreamId)obj;
        return Objects.equal(cai.id1, id1) && Objects.equal(cai.id2, id2);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, false).append("id1", id1).append("id2", id2).toString();
    }

    @Override
    public String toStringValue() {
        return id1.toStringValue() + "__" + id2.toStringValue();
    }

}
