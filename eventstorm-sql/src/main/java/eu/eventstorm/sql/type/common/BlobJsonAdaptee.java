package eu.eventstorm.sql.type.common;

import eu.eventstorm.sql.JsonMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class BlobJsonAdaptee {

	private boolean isModified = false;

	protected final void setModified() {
		this.isModified = true;
	}

	protected final boolean isModified() {
		return this.isModified ;
	}

	abstract byte[] write(JsonMapper mapper);

}