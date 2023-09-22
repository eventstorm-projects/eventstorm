package eu.eventstorm.core.apt.query.els;

import eu.eventstorm.annotation.els.Keyword;
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

        FileObject object = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "",
                //env.getElementUtils().getPackageOf(descriptor.element()).toString(),
                "els/" + descriptor.indice().name() + ".json");
        Writer writer = object.openWriter();

        writer.write("{");
        writeNewLine(writer);
        writer.write("  \"dynamic\": \"STRICT\",");
        writeNewLine(writer);

        appendProperties(writer, descriptor);

        writer.write("}");
        writer.close();

    }

    private void appendProperties(Writer writer, ElsQueryDescriptor descriptor) throws IOException {

        writer.write("  \"properties\": {");
        writeNewLine(writer);

        for (QueryPropertyDescriptor qpd : descriptor.properties()) {
            writer.write("    \"" + qpd.name() + "\" : {");
            writeNewLine(writer);
            //writer.write("         \"type\" : " + qpd.name() + "\" : {");

            writePropertyKeyword(writer, qpd);
            writePropertyText(writer, qpd);

            writeNewLine(writer);
            writer.write("    }");
            writeNewLine(writer);

//			"original_language": {
//			"type": "keyword"
//		},
        }
        writer.write("  }");

    }

    private void writePropertyKeyword(Writer writer, QueryPropertyDescriptor propertyDescriptor) throws IOException {
        Keyword keyword = propertyDescriptor.getter().getAnnotation(Keyword.class);
        if (keyword == null) {
            return;
        }
        writer.write("      \"type\" : \"keyword\"");

    }

    private void writePropertyText(Writer writer, QueryPropertyDescriptor propertyDescriptor) throws IOException {
        Text text = propertyDescriptor.getter().getAnnotation(Text.class);
        if (text == null) {
            return;
        }
        writer.write("      \"type\" : \"keyword\"");
    }


}