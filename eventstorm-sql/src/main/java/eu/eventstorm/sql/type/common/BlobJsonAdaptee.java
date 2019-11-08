package eu.eventstorm.sql.type.common;

import eu.eventstorm.sql.JsonMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class BlobJsonAdaptee {

	private boolean isModified = false;

	byte[] write(JsonMapper mapper) {
		if (isModified) {
			return doWrite(mapper);
		} else {
			return null;
		}
	}

	protected final void setModified() {
		this.isModified = true;
	}

	protected abstract byte[] doWrite(JsonMapper mapper);

}