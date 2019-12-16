package eu.eventstorm.core.id;

import com.google.common.base.Objects;

import eu.eventstorm.core.AggregateId;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ComposeAggregateId implements AggregateId{

    private final AggregateId id1;
    private final AggregateId id2;
    
    ComposeAggregateId(AggregateId id1, AggregateId id2) {
        this.id1 = id1;
        this.id2 = id2;
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

        if ((obj == null) || ComposeAggregateId.class != obj.getClass()) {
            return false;
        }

        ComposeAggregateId cai = (ComposeAggregateId)obj;
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
