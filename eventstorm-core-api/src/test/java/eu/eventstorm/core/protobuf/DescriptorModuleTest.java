package eu.eventstorm.core.protobuf;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.TypeRegistry;
import eu.eventstorm.core.Event;
import eu.eventstorm.test.LoggerInstancePostProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(LoggerInstancePostProcessor.class)
class DescriptorModuleTest {

    @Test
    void buildRegistry() {
        TypeRegistry typeRegistry = TypeRegistryFactory.make(ImmutableList.of(new DescriptorModule("Event", ImmutableList.of(Event.getDescriptor()))));
        assertNotNull(typeRegistry.find(Event.class.getName()));
    }

}