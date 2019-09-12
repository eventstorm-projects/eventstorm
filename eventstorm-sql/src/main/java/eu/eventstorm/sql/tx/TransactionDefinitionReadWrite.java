package eu.eventstorm.sql.tx;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionDefinitionReadWrite implements TransactionDefinition {

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("isReadOnly", false)
                .toString();
    }
}
