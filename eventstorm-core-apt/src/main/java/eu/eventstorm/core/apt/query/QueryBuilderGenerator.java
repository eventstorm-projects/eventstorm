package eu.eventstorm.core.apt.query;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.QueryPropertyDescriptor;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryBuilderGenerator {

    private Logger logger;

    public void generateClient(ProcessingEnvironment processingEnv, SourceCode sourceCode) {
        try (Logger logger = Logger.getLogger(processingEnv, "eu.eventstorm.event.query", "QueryBuilderGenerator")) {
            this.logger = logger;
            sourceCode.forEachQueryClient(t -> {
                try {
                    generate(processingEnv, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        sourceCode.forEachQuery(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
                logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void generate(ProcessingEnvironment env, QueryDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Builder") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualidiedClassName() + "Builder" + "]");
            return;
        }
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Builder");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeVariables(writer, descriptor);
        writeMethods(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, QueryDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer, QueryBuilderGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Builder");
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Builder");
        writer.write("() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<QueryPropertyDescriptor> descriptors) throws IOException {
        for (QueryPropertyDescriptor ppd : descriptors) {
            writer.write("    ");
            writer.write(ppd.getter().getReturnType().toString());
            writer.write(" ");
            writer.write(ppd.variable());
            writer.write("$$;");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, QueryDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethod(writer, descriptor);
        writeBuildMethod(writer, descriptor);
    }

    private static void writeBuildMethod(Writer writer, QueryDescriptor ed) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ed.simpleName());
        writer.write(' ');
        writer.write("build() {");
        writeNewLine(writer);
        writer.write("        return new " + ed.simpleName() + "Impl(this);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeMethod(Writer writer, QueryDescriptor descriptor) throws IOException {
        for (QueryPropertyDescriptor ppd : descriptor.properties()) {
            writeMethod(writer, descriptor, ppd);
        }
    }

    private static void writeMethod(Writer writer, QueryDescriptor ed, QueryPropertyDescriptor epd) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ed.simpleName() + "Builder");
        writer.write(" with");
        writer.write(Helper.firstToUpperCase(epd.name()));
        writer.write("(");
        writer.write(epd.getter().getReturnType().toString());
        writer.write(' ');
        writer.write(epd.name());
        writer.write(") {");
        writeNewLine(writer);
        writer.write("        this." + epd.name() + "$$ = " + epd.name() + ";");
        writeNewLine(writer);
        writer.write("        return this;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}