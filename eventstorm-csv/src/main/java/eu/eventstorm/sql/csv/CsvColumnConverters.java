package eu.eventstorm.sql.csv;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import eu.eventstorm.util.Dates;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CsvColumnConverters {

    private CsvColumnConverters() {
    }

    public static final Function<byte[], String> RAW_STRING = data -> new String(data, StandardCharsets.UTF_8);
    
    public static final Function<byte[], Integer> RAW_INTEGER = data -> {
    		String value = new String(data, StandardCharsets.UTF_8);
    		if (Strings.isEmpty(value)) {
    			return null;
    		}
    		return Integer.parseInt(value);
    };

    public static final Function<byte[], LocalDate> RAW_LOCAL_DATE = data -> Dates.parseLocalDate(new String(data, StandardCharsets.UTF_8));

    public static Function<byte[], LocalDate> localDate(DateTimeFormatter formatter) {
        return data -> formatter.parse(new String(data, StandardCharsets.UTF_8), LocalDate::from);
    }

}
