package eu.eventstorm.core.id;

import static eu.eventstorm.core.id.StreamIdExtractors.extractInteger;
import static eu.eventstorm.core.id.StreamIdExtractors.extractLong;
import static eu.eventstorm.core.id.StreamIdExtractors.extractComposePart1;

import static eu.eventstorm.core.id.StreamIdExtractors.extractComposePart2;
import static eu.eventstorm.core.id.StreamIds.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import eu.eventstorm.core.StreamId;

class StreamIdExtractorsTest {

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
        
    	StreamId compose = StreamIds.compose(from(123), from(456l));
    	
        assertEquals(from(123), extractComposePart1(compose));
        assertEquals(from(456l), extractComposePart2(compose));
        
        assertThrows(IllegalStateException.class, () -> extractComposePart1(from(123)));
        assertThrows(IllegalStateException.class, () -> extractComposePart2(from("123")));

    }
}
