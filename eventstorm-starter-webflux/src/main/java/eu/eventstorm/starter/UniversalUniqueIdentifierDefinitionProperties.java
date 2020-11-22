package eu.eventstorm.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import eu.eventstorm.core.id.UniversalUniqueIdentifierDefinition;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@ConfigurationProperties(prefix = "eu.eventstorm.uuid")
public class UniversalUniqueIdentifierDefinitionProperties implements UniversalUniqueIdentifierDefinition {
	
	private short region;
	private short node;
	
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
