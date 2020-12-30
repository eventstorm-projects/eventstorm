package eu.eventstorm.batch.rest;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseExecutionNotFoundException extends RuntimeException {

    private final String uuid;

    public DatabaseExecutionNotFoundException(String uuid) {
        super("uuid [" + uuid + "] not found");
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

}
