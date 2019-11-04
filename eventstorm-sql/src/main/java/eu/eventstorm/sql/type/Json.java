package eu.eventstorm.sql.type;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Json {

    JsonMap asMap();

    JsonList asList();

    void flush();

}