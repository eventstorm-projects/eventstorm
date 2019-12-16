package eu.eventstorm.core.id;

import static eu.eventstorm.core.id.AggregateIdExtractors.extractInteger;
import static eu.eventstorm.core.id.AggregateIdExtractors.extractLong;
import static eu.eventstorm.core.id.AggregateIds.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
}
