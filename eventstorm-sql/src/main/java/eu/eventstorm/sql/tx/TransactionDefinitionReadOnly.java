package eu.eventstorm.sql.tx;

import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class TransactionDefinitionReadOnly implements TransactionDefinition {

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("isReadOnly", true)
                .toString();
    }

}
