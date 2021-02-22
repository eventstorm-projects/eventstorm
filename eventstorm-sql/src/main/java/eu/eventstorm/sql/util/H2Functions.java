package eu.eventstorm.sql.util;

import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
        return JsonPath.parse(json).read(key) != null;
    }

}