package eu.eventstorm.sql.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.desc.SqlSequence;

class DatabaseExternalDefintionBuilderTest {
    
    @Test
    void testEmptyExternalDefinition() {
        DatabaseExternalDefintionBuilder builder = new DatabaseExternalDefintionBuilder();
        DatabaseExternalDefintion def = builder.build();
        def.forEachSequence((module, sequence) -> {
            fail();
        });
    }

    @Test
    void testSimpleExternalDefinition() {
        
        Module module = new Module("") {
        };
        SqlSequence sequence = new SqlSequence("toto");
        
        DatabaseExternalDefintionBuilder builder = new DatabaseExternalDefintionBuilder();
        DatabaseExternalDefintion def = builder.module(module).sequence(sequence).and().build();
        
        AtomicInteger integer = new AtomicInteger();
        def.forEachSequence((m, s) -> {
            assertEquals(module, m);
            assertEquals(sequence, s);
            integer.incrementAndGet();
        });
        assertEquals(1, integer.get());
    }
}
