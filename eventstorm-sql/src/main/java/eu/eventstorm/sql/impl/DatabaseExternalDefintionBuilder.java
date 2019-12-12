package eu.eventstorm.sql.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlSequence;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class DatabaseExternalDefintionBuilder {

    private final Map<Module, List<SqlSequence>> map = new HashMap<>();
    
    public DatabaseExternalDefintionBuilderModule module(Module module) {
        List<SqlSequence> sequences = new ArrayList<>();
        map.put(module, sequences);
        return new DatabaseExternalDefintionBuilderModule(sequences);
    }
    
    public class DatabaseExternalDefintionBuilderModule {
        private final List<SqlSequence> sequences;
        private DatabaseExternalDefintionBuilderModule(List<SqlSequence> sequences) {
            this.sequences = sequences;
        }
        public DatabaseExternalDefintionBuilderModule sequence(SqlSequence sequence) {
            this.sequences.add(sequence);
            return this;
        }
        public DatabaseExternalDefintionBuilder and() {
            return DatabaseExternalDefintionBuilder.this;
        }
     }
    
    public DatabaseExternalDefintion build() {
        return new DatabaseExternalDefintion() {
            @Override
            public void forEachSequence(BiConsumer<Module, SqlSequence> consumer) {
                for (Module module : map.keySet()) {
                    map.get(module).forEach( seq -> consumer.accept(module, seq));
                }
            }
        };
    }

}
