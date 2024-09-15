package eu.eventstorm.core.apt.query.els;

import com.google.common.collect.ImmutableMap;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.cqrs.PageQueryDescriptor;
import eu.eventstorm.cqrs.PageQueryDescriptors;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ElasticPageQueryDescriptorsGenerator {

    private Logger logger;

    public ElasticPageQueryDescriptorsGenerator() {
    }

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
        try (Logger logger = Logger.getLogger(env, "eu.eventstorm.event.query.els", "PageQueryDescriptorsGenerator")) {
            this.logger = logger;

            String fcqn = sourceCode.getCqrsConfiguration().basePackage() + ".EventstormElsPageQueryDescriptors";

            // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
            if (env.getElementUtils().getTypeElement(fcqn) != null) {
                logger.info("Java SourceCode already exist [" + fcqn + "]");
                return;
            }

            AtomicInteger counter = new AtomicInteger(0);
            sourceCode.forEachElasticSearchQuery(item -> counter.incrementAndGet());

            if (counter.get() == 0) {
                logger.info("No ELS Queries found => skip");
                return;
            } else {
                logger.info("ELS Queries found => [" + counter.get() + "]");

            }

            try {
                JavaFileObject object = env.getFiler().createSourceFile(fcqn);
                Writer writer = object.openWriter();

                writeHeader(writer, sourceCode.getCqrsConfiguration().basePackage());
                writeStatic(writer, sourceCode);
                writeMethod(writer);

                writer.write("}");
                writer.close();


            } catch (Exception cause) {
                logger.error("Exception for [" + cause.getMessage() + "]", cause);
            }

        }


    }

    private static void writeHeader(Writer writer, String pack) throws IOException {
        writePackage(writer, pack);

        writer.write("import " + ImmutableMap.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + PageQueryDescriptor.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import " + PageQueryDescriptors.class.getName() + ";");
        writeNewLine(writer);
        writer.write("import eu.eventstorm.cqrs.els.page.ElsPageQueryDescriptor;");
        writeNewLine(writer);

        writeGenerated(writer, ElasticPageQueryDescriptorsGenerator.class.getName());
        writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
        writer.write("final class EventstormElsPageQueryDescriptors implements PageQueryDescriptors {");
        writeNewLine(writer);
    }


    private void writeStatic(Writer writer, SourceCode sourceCode) throws IOException {

        writeNewLine(writer);
        writer.write("    private static final ImmutableMap<String, PageQueryDescriptor> DESCRIPTORS = ImmutableMap.<String, PageQueryDescriptor>builder() ");
        writeNewLine(writer);

        sourceCode.forEachElasticSearchQuery(query -> {
            try {
                writer.write("        .put(\"" + query.fullyQualidiedClassName() + "\", new ElsPageQueryDescriptor(");
                writer.write("\n            new " + query.fullyQualidiedClassName() + "ElsPageRequestDescriptor()))");
                writeNewLine(writer);
            } catch (IOException cause) {
                logger.error("failed to generate [" + query + "]", cause);
            }
        });

        writer.write("        .build();");
        writeNewLine(writer);
    }

    private void writeMethod(Writer writer) throws IOException {
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public PageQueryDescriptor get(String fcqn) {");
        writeNewLine(writer);
        writer.write("        return DESCRIPTORS.get(fcqn);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}