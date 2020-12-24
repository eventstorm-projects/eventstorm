package eu.eventstorm.core.protobuf;

import com.google.protobuf.TypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class TypeRegistryFactory {

    private TypeRegistryFactory() {
    }

    public static TypeRegistry make(List<DescriptorModule> descriptorModules) {
        Logger logger = LoggerFactory.getLogger(TypeRegistryFactory.class);
        TypeRegistry.Builder builder = TypeRegistry.newBuilder();
        descriptorModules.forEach(dm -> {
            logger.info("append DescriptorModule[{}]", dm);
            dm.appendTo(builder);
        });
        return builder.build();
    }

}
