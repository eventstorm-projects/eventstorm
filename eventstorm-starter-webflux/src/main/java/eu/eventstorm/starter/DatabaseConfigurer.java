package eu.eventstorm.starter;

import eu.eventstorm.sql.impl.DatabaseBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public interface DatabaseConfigurer {

    void configure(DatabaseBuilder builder);

}
