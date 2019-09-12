package eu.eventstorm.sql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.model.Pojos;

class ModuleTest {

    @Test
    void testToString() {
        Module module = new Module("theName", Pojos.DESCRIPTOR_FOLDER) {
        };
        Assertions.assertNotNull(module.toString());
    }

}
