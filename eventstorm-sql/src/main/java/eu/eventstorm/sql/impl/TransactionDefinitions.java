package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.TransactionType;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionDefinitions {

    private TransactionDefinitions() {
    }

    public static final TransactionDefinition READ_ONLY = new TransactionDefinition() {

        @Override
        public int getTimeout() {
            return -1;
        }

        @Override
        public TransactionType getType() {
            return TransactionType.READ_ONLY;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(false).append("type", TransactionType.READ_ONLY)
                    .append("timeout", -1)
                    .toString();
        }
    };

    public static final TransactionDefinition READ_WRITE = new TransactionDefinition() {

        @Override
        public int getTimeout() {
            return -1;
        }

        @Override
        public TransactionType getType() {
            return TransactionType.READ_WRITE;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(false).append("type", TransactionType.READ_WRITE)
                    .append("timeout", -1)
                    .toString();
        }

    };

    public static final TransactionDefinition ISOLATED_READ_WRITE = new TransactionDefinition() {
        @Override
        public int getTimeout() {
            return -1;
        }

        @Override
        public TransactionType getType() {
            return TransactionType.ISOLATED_READ_WRITE;
        }

        @Override
        public String toString() {
            return new ToStringBuilder(false).append("type", TransactionType.ISOLATED_READ_WRITE)
                    .append("timeout", -1)
                    .toString();
        }
    };

    public static TransactionDefinition readOnly(int timeout) {
        return new TransactionDefinition() {
            @Override
            public int getTimeout() {
                return timeout;
            }
            @Override
            public TransactionType getType() {
                return TransactionType.READ_ONLY;
            }
            @Override
            public String toString() {
                return new ToStringBuilder(false).append("type", TransactionType.READ_ONLY)
                        .append("timeout", timeout)
                        .toString();
            }
        };
    }

    public static TransactionDefinition readWrite(int timeout) {
        return new TransactionDefinition() {
            @Override
            public int getTimeout() {
                return timeout;
            }
            @Override
            public TransactionType getType() {
                return TransactionType.READ_WRITE;
            }
            @Override
            public String toString() {
                return new ToStringBuilder(false).append("type", TransactionType.READ_WRITE)
                        .append("timeout", timeout)
                        .toString();
            }
        };
    }
}
