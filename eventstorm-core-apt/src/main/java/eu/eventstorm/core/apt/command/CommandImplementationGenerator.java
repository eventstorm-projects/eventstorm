package eu.eventstorm.core.apt.command;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.AbstractCommandDescriptor;
import eu.eventstorm.core.apt.model.PropertyDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.util.ToStringBuilder;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static eu.eventstorm.sql.apt.Helper.getReturnType;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandImplementationGenerator {

    private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

    private Logger logger;

    public void generateCommand(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.generator", "CommandImplementationGenerator")) {
            this.logger = logger;

            sourceCode.forEachCommand(t -> {
                try {
                    generate(processingEnvironment, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
            sourceCode.forEachSagaCommand(t -> {
                try {
                    generate(processingEnvironment, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });

        }


    }

    public void generateEmbeddedCommand(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        // generate Implementation class;
        sourceCode.forEachEmbeddedCommand(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
                logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void generate(ProcessingEnvironment env, AbstractCommandDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Impl") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualidiedClassName() + "Impl" + "]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Impl");

        try (Writer writer = object.openWriter()) {
            writeHeader(writer, env, descriptor);
            writeConstructor(writer, descriptor);
            writeVariables(writer, descriptor);
            writeMethods(writer, descriptor);
            writeToStringBuilder(writer, descriptor);
            writer.write("}");
        }
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, AbstractCommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer, CommandImplementationGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write(" implements ");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write("(");

        StringBuilder builder = new StringBuilder();
        for (PropertyDescriptor ppd : descriptor.properties()) {
            builder.append(getReturnType(ppd.getter()));
            builder.append(" ");
            builder.append(ppd.variable());
            builder.append(",");
        }

        if (descriptor.properties().size() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        writer.write(builder.toString());
        writer.write(") {");
        writeNewLine(writer);

        for (PropertyDescriptor ppd : descriptor.properties()) {
            writer.write("        this.");
            writer.write(ppd.variable());
            writer.write(" = ");
            writer.write(ppd.variable());
            writer.write(";");
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<PropertyDescriptor> descriptors) throws IOException {
        for (PropertyDescriptor ppd : descriptors) {
            writer.write("    private ");
            writer.write(getReturnType(ppd.getter()));
            writer.write(" ");
            writer.write(ppd.variable());
            writer.write(";");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethods(writer, descriptor.properties());
        writerKeyMethod(writer, descriptor);
    }


    private static void writerKeyMethod(Writer writer, AbstractCommandDescriptor descriptor) {

    }

    private static void writeMethods(Writer writer, List<PropertyDescriptor> descriptors) throws IOException {
        for (PropertyDescriptor ppd : descriptors) {
            writeGetter(writer, ppd);
        }
    }

    private static void writeGetter(Writer writer, PropertyDescriptor ppd) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(getReturnType(ppd.getter()));
        writer.write(' ');
        writer.write(ppd.getter().getSimpleName().toString());
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        return this.");
        writer.write(ppd.variable());
        writer.write(";");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeToStringBuilder(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public String toString() {");
        writeNewLine(writer);
        writer.write("        " + TO_STRING_BUILDER + " builder = new " + TO_STRING_BUILDER + "(this);");
        writeNewLine(writer);

        for (PropertyDescriptor ppd : descriptor.properties()) {
            writer.write("        builder.append(\"");
            writer.write(ppd.name());
            writer.write("\", this.");
            writer.write(ppd.variable());
            writer.write(");");
            writeNewLine(writer);
        }
        writer.write("        return builder.toString();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}