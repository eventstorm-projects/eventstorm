package eu.eventstorm.core.id;

import static eu.eventstorm.core.id.AggregateIdExtractors.extractInteger;
import static eu.eventstorm.core.id.AggregateIdExtractors.extractLong;
import static eu.eventstorm.core.id.AggregateIdExtractors.extractComposePart1;

import static eu.eventstorm.core.id.AggregateIdExtractors.extractComposePart2;
import static eu.eventstorm.core.id.AggregateIds.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import eu.eventstorm.core.AggregateId;

class AggregateIdExtractorsTest {

    @Test
    void testExtractInteger() {
        
        assertEquals(123, extractInteger(from(123)));
        
        assertThrows(IllegalStateException.class, () -> extractInteger(from(123l)));
        assertThrows(IllegalStateException.class, () -> extractInteger(from("123")));

    }
    
    @Test
    void testExtractLong() {
        
        assertEquals(123L, extractLong(from(123L)));
        
        assertThrows(IllegalStateException.class, () -> extractLong(from(123)));
        assertThrows(IllegalStateException.class, () -> extractLong(from("123")));

    }
    
    @Test
    void testExtractCompose() {
        
    	AggregateId compose = AggregateIds.compose(from(123), from(456l));
    	
        assertEquals(from(123), extractComposePart1(compose));
        assertEquals(from(456l), extractComposePart2(compose));
        
        assertThrows(IllegalStateException.class, () -> extractComposePart1(from(123)));
        assertThrows(IllegalStateException.class, () -> extractComposePart2(from("123")));

    }
}
