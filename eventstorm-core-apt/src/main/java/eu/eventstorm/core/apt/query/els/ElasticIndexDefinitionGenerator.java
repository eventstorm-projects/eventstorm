package eu.eventstorm.core.apt.query.els;

import eu.eventstorm.annotation.els.Date;
import eu.eventstorm.annotation.els.Id;
import eu.eventstorm.annotation.els.Keyword;
import eu.eventstorm.annotation.els.Nested;
import eu.eventstorm.annotation.els.Number;
import eu.eventstorm.annotation.els.Text;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.ElsQueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.util.ToStringBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;

import static eu.eventstorm.sql.apt.Helper.writeNewLine;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElasticIndexDefinitionGenerator {

    private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

    private Logger logger;

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.query.els", "ElasticIndexDefinitionGenerator")) {
            this.logger = logger;
            sourceCode.forEachElasticSearchQuery(t -> {
                try {
                    generate(processingEnvironment, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }
    }

    private void generate(ProcessingEnvironment env, ElsQueryDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Impl") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualidiedClassName() + "Impl" + "]");
            return;
        }

        FileObject object = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "els",
                //env.getElementUtils().getPackageOf(descriptor.element()).toString(),
                descriptor.indice().name() + ".json");
        Writer writer = object.openWriter();

        writer.write("{");
        writeNewLine(writer);
        writer.write("  \"mappings\": {");
        writeNewLine(writer);
        writer.write("    \"dynamic\": \"false\",");
        writeNewLine(writer);


        appendProperties(writer, descriptor);

        writer.write("  }");
        writeNewLine(writer);
        writer.write("}");
        writer.close();

    }

    private void appendProperties(Writer writer, ElsQueryDescriptor descriptor) throws IOException {

        writer.write("    \"properties\": {");
        writeNewLine(writer);

        for (int i = 0; i < descriptor.properties().size(); i++) {
            QueryPropertyDescriptor qpd = descriptor.properties().get(i);
            writer.write("      \"" + qpd.name() + "\": {");
            writeNewLine(writer);
            writeProperty(writer, qpd);
            writeNewLine(writer);
            writer.write("      }");

            if (i + 1 < descriptor.properties().size()) {
                writer.write(",");
            }
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);


    }

    private void writeProperty(Writer writer, QueryPropertyDescriptor propertyDescriptor) throws IOException {
        Keyword keyword = propertyDescriptor.getter().getAnnotation(Keyword.class);
        if (keyword != null) {
            writer.write("        \"type\": \"text\",");
            writeNewLine(writer);
            writer.write("        \"fields\": {");
            writeNewLine(writer);
            writer.write("          \"keyword\": {");
            writeNewLine(writer);

            writer.write("            \"type\": \"keyword\",");
            writeNewLine(writer);
            writer.write("            \"ignore_above\": 256");
            writeNewLine(writer);
            writer.write("          }");
            writeNewLine(writer);
            writer.write("        }");

            return;
        }
        Text text = propertyDescriptor.getter().getAnnotation(Text.class);
        if (text != null) {
            writer.write("        \"type\": \"text\"");
            return;
        }
        Number number = propertyDescriptor.getter().getAnnotation(Number.class);
        if (number != null) {
            writer.write("        \"type\": \"" + number.type().getValue() + "\"");
            return;
        }
        Nested nested = propertyDescriptor.getter().getAnnotation(Nested.class);
        if (nested != null) {
            writer.write("        \"dynamic\": \"true\",");
            writeNewLine(writer);
            writer.write("        \"type\": \"nested\"");
            return;
        }
        Id id = propertyDescriptor.getter().getAnnotation(Id.class);
        if (id != null) {
            writer.write("        \"type\": \"keyword\"");
            return;
        }
        Date date = propertyDescriptor.getter().getAnnotation(Date.class);
        if (date != null) {
            writer.write("        \"type\": \"date\"");
            return;
        }
    }

}