package eu.eventstorm.sql.impl;

import java.util.function.BiConsumer;

import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlSequence;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
interface DatabaseExternalConfig {

	void forEachSequence(BiConsumer<Module, SqlSequence> consumer);

}
