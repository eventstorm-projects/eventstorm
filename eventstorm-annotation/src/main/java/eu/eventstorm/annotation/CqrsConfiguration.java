package eu.eventstorm.annotation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@Target({TYPE})
@Retention(SOURCE)
public @interface CqrsConfiguration {

    OpenAPIDefinition openAPIDefinition() default @OpenAPIDefinition;

    /**
     * to generate SpringConfiguration, Streams, ...
     */
    String basePackage();


    /**
     * evolution base url : @see https://developers.google.com/protocol-buffers/docs/proto3#any
     */
    String evolutionDataTypeBaseUrl() default "";


    /**
     * identifier for this configuration
     */
    String id() default "";

}