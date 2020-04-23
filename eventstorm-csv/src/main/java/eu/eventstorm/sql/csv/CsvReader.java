package eu.eventstorm.sql.csv;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface CsvReader extends AutoCloseable {

    CsvLine line();
    
    void close();

}
