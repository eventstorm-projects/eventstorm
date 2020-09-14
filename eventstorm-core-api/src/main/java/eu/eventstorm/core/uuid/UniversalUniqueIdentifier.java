package eu.eventstorm.core.uuid;

import java.util.UUID;

import eu.eventstorm.core.StreamId;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 * 
 * @see https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format-00
 */
public abstract class UniversalUniqueIdentifier implements StreamId {

	private final UUID uuid;

    protected UniversalUniqueIdentifier(UUID uuid) {
		this.uuid = uuid;
	}
    
	public final UUID getUuid() {
		return uuid;
	}
	
	@Override
	public String toString() {
		return this.uuid.toString();
	}

	@Override
	public final String toStringValue() {
		return this.uuid.toString();
	}
    
}
