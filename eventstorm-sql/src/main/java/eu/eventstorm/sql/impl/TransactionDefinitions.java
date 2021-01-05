package eu.eventstorm.sql.impl;

import eu.eventstorm.sql.TransactionDefinition;
import eu.eventstorm.sql.TransactionType;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class TransactionDefinitions {

    private TransactionDefinitions() {
    }

    public static final TransactionDefinition READ_ONLY = new TransactionDefinition() {

        @Override
        public int getTimeout() {
            return 10;
        }

        @Override
        public TransactionType getType() {
            return TransactionType.READ_ONLY;
        }
    };

    public static final TransactionDefinition READ_WRITE = new TransactionDefinition() {

        @Override
        public int getTimeout() {
            return 10;
        }

        @Override
        public TransactionType getType() {
            return TransactionType.READ_WRITE;
        }

    };

    public static final TransactionDefinition ISOLATED_READ_WRITE = new TransactionDefinition() {
        @Override
        public int getTimeout() {
            return 10;
        }

        @Override
        public TransactionType getType() {
            return TransactionType.ISOLATED_READ_WRITE;
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
        };
    }
}
