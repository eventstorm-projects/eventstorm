package eu.eventstorm.starter;

import eu.eventstorm.sql.Dialect;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConfigurationProperties(prefix = "eu.eventstorm.database")
public class DatabaseProperties {

    private Dialect.Name dialect;
    private TransactionManagerProperties tx = new TransactionManagerProperties();

    public Dialect.Name getDialect() {
        return dialect;
    }

    public void setDialect(Dialect.Name dialect) {
        this.dialect = dialect;
    }

    public TransactionManagerProperties getTx() {
        return tx;
    }

    public void setTx(TransactionManagerProperties tx) {
        this.tx = tx;
    }

    public static class TransactionManagerProperties {
        private TransactionManagerTracingType tracing = TransactionManagerTracingType.NO_OP;

        public TransactionManagerTracingType getTracing() {
            return tracing;
        }

        public void setTracing(TransactionManagerTracingType tracing) {
            this.tracing = tracing;
        }
    }

    public enum TransactionManagerTracingType {
        NO_OP, DEBUG, MICROMETER;
    }
}
