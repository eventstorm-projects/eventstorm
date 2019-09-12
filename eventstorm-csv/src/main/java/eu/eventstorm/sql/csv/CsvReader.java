package eu.eventstorm.sql.csv;

public interface CsvReader extends AutoCloseable {

    CsvLine line();

}
