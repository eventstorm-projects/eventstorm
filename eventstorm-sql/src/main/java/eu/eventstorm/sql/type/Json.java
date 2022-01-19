package eu.eventstorm.sql.type;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface Json {

    JsonMap asMap();

    <T> JsonList<T> asList(Class<T> type);

	byte[] write();

    String writeAsString();

}