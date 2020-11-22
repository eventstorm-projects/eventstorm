package eu.eventstorm.core.id;

import java.util.UUID;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 * 
 * @see https://tools.ietf.org/html/draft-peabody-dispatch-new-uuid-format-00
 */
public abstract class UniversalUniqueIdentifier {

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

}
