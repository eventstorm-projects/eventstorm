package eu.eventstorm.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConfigurationProperties(prefix = "eu.eventstorm.eventstore")
public class EventStoreProperties {

    private boolean tracing = true;

    public boolean isTracing() {
        return tracing;
    }

    public void setTracing(boolean tracing) {
        this.tracing = tracing;
    }

}
