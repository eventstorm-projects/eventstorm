package eu.eventstorm.sql.type.common;

import eu.eventstorm.sql.JsonMapper;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public abstract class JsonAdapter {

    private boolean isModified = false;

    protected final void setModified() {
        this.isModified = true;
    }

    public final boolean isModified() {
        return this.isModified;
    }

    public abstract byte[] write(JsonMapper mapper);

}