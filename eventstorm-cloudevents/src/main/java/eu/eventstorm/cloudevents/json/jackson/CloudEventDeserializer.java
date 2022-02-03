package eu.eventstorm.cloudevents.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.TypeRegistry;
import com.google.protobuf.util.JsonFormat;
import eu.eventstorm.cloudevents.CloudEvent;
import eu.eventstorm.cloudevents.CloudEventBuilder;
import eu.eventstorm.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.google.common.collect.ImmutableMap.of;
import static eu.eventstorm.cloudevents.json.jackson.CloudEventDeserializerException.Type.INVALID_FIELD_VALUE;
import static eu.eventstorm.cloudevents.json.jackson.CloudEventDeserializerException.Type.PARSE_ERROR;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CloudEventDeserializer extends StdDeserializer<CloudEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventDeserializer.class);

    public static final String FIELD = "field";

    private static final ImmutableMap<String, BiConsumer<JsonParser, CloudEventBuilder>> CONFIG;

    private final transient TypeRegistry registry;
    private final transient JsonFormat.Parser parser;
    private final transient ConcurrentReferenceHashMap<String, Supplier<Method>> BUILDER_FACTORY_CACHE = new ConcurrentReferenceHashMap<>(256);

    static {
        CONFIG = ImmutableMap.<String, BiConsumer<JsonParser, CloudEventBuilder>>builder()
                // @formatter:off
                .put("specversion", (parser, builder) -> builder.withSpecVersion(parseString(parser, "specversion")))
                .put("type", (parser, builder) -> builder.withAggregateType(parseString(parser, "type")))
                .put("time", (parser, builder) -> builder.withTimestamp(parseString(parser, "time")))
                .put("id", (parser, builder) -> builder.withAggregateId(parseString(parser, "id")))
                .put("version", (parser, builder) -> {
                    try {
                        builder.withVersion(parser.nextIntValue(0));
                    } catch (IOException cause) {
                        throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, "version"), cause);
                    }
                })
                .put("subject", (parser, builder) -> builder.withSubject(parseString(parser, "subject")))
                .put("datacontenttype", (parser, builder) -> builder.withDataContentType(parseString(parser, "datacontenttype")))
                // @formatter:on
                .build();
    }

    CloudEventDeserializer(TypeRegistry registry) {
        super(CloudEvent.class);
        this.registry = registry;
        this.parser = JsonFormat.parser().usingTypeRegistry(registry).ignoringUnknownFields();

    }

    @Override
    public CloudEvent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        CloudEventBuilder builder = new CloudEventBuilder();
        JsonToken t = p.getCurrentToken();

        if (t == JsonToken.START_OBJECT) {
            p.nextToken();
        } else {
            // exception.
        }

        while (t != JsonToken.END_OBJECT) {
            t = p.getCurrentToken();
            if (t == JsonToken.FIELD_NAME) {
                String fieldName = p.getText();

                BiConsumer<JsonParser, CloudEventBuilder> consumer = CONFIG.get(fieldName);
                if (consumer == null) {
                    if ("data".equals(fieldName)) {
                        try {
                            // go to inside of data
                            p.nextToken();
                            if (Strings.isEmpty(builder.getSubject())) {
                                builder.withPayload(p.readValueAs(Map.class));
                            } else {
                                Message.Builder messageOrBuilder = get(builder.getSubject());
                                if (messageOrBuilder == null) {
                                    builder.withPayload(p.readValueAs(Map.class));
                                } else {
                                    parser.merge(p.readValueAsTree().toString(), messageOrBuilder);
                                    builder.withPayload(messageOrBuilder.build());
                                }
                            }
                        } catch (IOException cause) {
                            throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, "data"), cause);
                        }
                    } else {
                        throw new UnsupportedOperationException("field [" + fieldName + "] not supported for cloudEvent");
                    }
                } else {
                    consumer.accept(p, builder);
                }
                t = p.nextToken();
            }
        }
        return builder.build();
    }

    private static String parseString(JsonParser parser, String field) {
        try {
            if (parser.nextToken() == JsonToken.VALUE_STRING) {
                return parser.getText();
            } else if (parser.currentToken() == JsonToken.VALUE_NULL) {
                return null;
            } else {
                throw new CloudEventDeserializerException(INVALID_FIELD_VALUE, of(FIELD, field, "jsonToken", parser.currentToken()));
            }
        } catch (IOException cause) {
            throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, field), cause);
        }
    }

    private Message.Builder get(String subject) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("subject[{}]", subject);
        }

        Supplier<Method> supplier = BUILDER_FACTORY_CACHE.get(subject);

        if (supplier != null) {
            Method method = supplier.get();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found subject [{}] in cache with method [{}]", subject, method);
            }
            if (method == null) {
                // no event for the given subject
                return null;
            } else {
                return (Message.Builder) ReflectionUtils.invokeMethod(method, null);
            }
        }

        Descriptors.Descriptor descriptor;
        try {
            descriptor = registry.getDescriptorForTypeUrl(subject);
        } catch (InvalidProtocolBufferException cause) {
            throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, "data"), cause);
        }

        if (descriptor == null) {
            BUILDER_FACTORY_CACHE.put(subject, () -> null);
        } else {
            try {
                String fcqn = descriptor.getFullName();
                if (!fcqn.contains(".")) {
                    fcqn = descriptor.getFile().toProto().getOptions().getJavaPackage();
                    fcqn += "." + descriptor.getFullName();

                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("try fcqn =[{}]", fcqn);
                    }

                }
                Method method = ReflectionUtils.findMethod(ClassUtils.forName(fcqn, Thread.currentThread().getContextClassLoader()), "newBuilder");
                BUILDER_FACTORY_CACHE.put(subject, () -> method);
            } catch (ClassNotFoundException cause) {
                throw new CloudEventDeserializerException(PARSE_ERROR, of(FIELD, "data"), cause);
            }
        }
        return get(subject);
    }

}
