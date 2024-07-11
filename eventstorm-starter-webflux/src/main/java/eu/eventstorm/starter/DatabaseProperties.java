package eu.eventstorm.starter;

import eu.eventstorm.sql.Dialect;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConfigurationProperties(prefix = "eu.eventstorm.database")
public class DatabaseProperties {

    private Dialect.Name dialect;
    private TransactionProperties tx = new TransactionProperties();

    public Dialect.Name getDialect() {
        return dialect;
    }

    public void setDialect(Dialect.Name dialect) {
        this.dialect = dialect;
    }

    public TransactionProperties getTx() {
        return tx;
    }

    public void setTx(TransactionProperties tx) {
        this.tx = tx;
    }

    public static class TransactionProperties {
        private boolean tracing = false;

        public boolean isTracing() {
            return tracing;
        }

        public void setTracing(boolean tracing) {
            this.tracing = tracing;
        }
    }
}
