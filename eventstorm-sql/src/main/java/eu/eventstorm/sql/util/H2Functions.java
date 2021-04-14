package eu.eventstorm.sql.util;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class H2Functions {

    private static final Logger LOGGER = LoggerFactory.getLogger(H2Functions.class);

    private H2Functions() {
    }

    public static String json_value(String json, String key) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_value: json {}, key: {}", json, key);
        }

        Object value = JsonPath.parse(json).read(key);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_value: key {}, value: {}", key, value);
        }

        return String.valueOf(value);
    }

    public static boolean json_exists(String json, String key) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(key);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_exists: json {}, key: {}", key, json);
        }

        String[] jsonPart = key.split("==");

        Object object = JsonPath.parse(json).read(jsonPart[0]);

        if (object == null) {
            return false;
        }
        if (object instanceof JSONArray) {
            JSONArray array = (JSONArray) object;
            return array.contains(jsonPart[1]);
        }
        throw new IllegalStateException();
    }

    public static boolean json_exists(String json, String key, String val) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(key);
        Objects.requireNonNull(val);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_exists: json {}, key: {}, val: {}", key, json, val);
        }


        String predicate = key.replaceAll("\\*", val);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_exists: predicate {}", predicate);
        }

        Object object = JsonPath.parse(json).read(predicate);

        if (object == null) {
            return false;
        } else {
            return true;
        }

    }

}