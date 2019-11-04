package eu.eventstorm.sql.type.common;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
abstract class BlobJsonAdaptee {

	private boolean isModified = false;

	byte[] write() {
		if (isModified) {
            return doWrite();
        } else {
            return null;
        }
    }

    protected final void setModified() {
        this.isModified = true;
    }

	protected abstract  byte[] doWrite();

}