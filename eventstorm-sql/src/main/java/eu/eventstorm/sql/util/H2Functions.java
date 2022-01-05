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

    public static boolean json_exists(String json, String path) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(path);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_exists: json {}, path: {}", json, path);
        }

        Object object = null;
        try {
             object = JsonPath.parse(json).read(path);
        } catch (Exception ex) {
            LOGGER.error("Failed to parse JSON ["+json+"]", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_exists: result {}", object);
        }

        if (object == null) {
            return false;
        }

        JSONArray array = (JSONArray) object;
        return (!array.isEmpty());
    }

    public static boolean json_exists_2(String json, String key, String val) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(val);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("json_exists: json {}, key: [{}] val: [{}]", json, key, val);
        }

        Object object;

        if (key.contains("*")) {
            String predicate =  key.replaceAll("\\*",val);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("json_exists: predicate {}", predicate);
            }

            object = JsonPath.parse(json).read(predicate);
        } else {
            object = null;
        }

        return object != null;

    }

}