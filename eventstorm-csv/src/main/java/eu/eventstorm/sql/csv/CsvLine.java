package eu.eventstorm.sql.csv;

import java.util.function.Function;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CsvLine {

	 int line();

	 int columns();

	 byte[] get(int col);

	 <T> T get(int col, Function<byte[], T> converter);
}