package eu.eventstorm.starter;

import eu.eventstorm.core.id.UniversalUniqueIdentifierDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConfigurationProperties(prefix = "eu.eventstorm.uuid")
public class UniversalUniqueIdentifierDefinitionProperties implements UniversalUniqueIdentifierDefinition {

    private boolean enabled = true;
    private short region;
    private short node;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public short getRegion() {
        return this.region;
    }

    @Override
    public short getNode() {
        return this.node;
    }

    public void setRegion(short region) {
        this.region = region;
    }

    public void setNode(short node) {
        this.node = node;
    }

}
