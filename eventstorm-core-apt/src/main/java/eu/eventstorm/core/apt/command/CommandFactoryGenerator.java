package eu.eventstorm.core.apt.command;

import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.AbstractCommandDescriptor;
import eu.eventstorm.core.apt.model.PropertyDescriptor;
import eu.eventstorm.sql.apt.log.Logger;

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
public final class CommandFactoryGenerator {

    private Logger logger;

    public CommandFactoryGenerator() {
    }

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.generator", "CommandFactoryGenerator")) {
            this.logger = logger;
        }
        sourceCode.forEachCommandPackage((pack, list) -> {
            try {
                generate(processingEnvironment, pack, list);
            } catch (Exception cause) {
                logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void generate(ProcessingEnvironment env, String pack, ImmutableList<? extends AbstractCommandDescriptor> descriptors) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".CommandFactory") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".CommandFactory" + "]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".CommandFactory");
        try (Writer writer = object.openWriter()) {
            writeHeader(writer, pack, descriptors);
            writeConstructor(writer);
            writeMethods(writer, descriptors);
            writer.write("}");
        }

    }


    private static void writeHeader(Writer writer, String pack, ImmutableList<? extends AbstractCommandDescriptor> descriptors) throws IOException {
        writePackage(writer, pack);

        for (AbstractCommandDescriptor descriptor : descriptors) {
            writer.write("import ");
            writer.write(descriptor.fullyQualidiedClassName());
            writer.write(";");
            writeNewLine(writer);
        }

        writeGenerated(writer, CommandFactoryGenerator.class.getName());
        writer.write("public final class CommandFactory {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer) throws IOException {
        writeNewLine(writer);
        writer.write("    private CommandFactory");
        writer.write("(){}");
        writeNewLine(writer);
    }

    private static void writeMethods(Writer writer, List<? extends AbstractCommandDescriptor> descriptors) throws IOException {
        for (AbstractCommandDescriptor descriptor : descriptors) {
            writeNewLine(writer);
            writer.write("    public static ");
            writer.write(descriptor.simpleName());
            writer.write(" new");
            writer.write(descriptor.simpleName());
            writer.write("(");

            StringBuilder builder = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();

            // a command with fields 
            if (descriptor.properties().size() > 0) {
                for (PropertyDescriptor prop : descriptor.properties()) {
                    builder.append(getReturnType(prop.getter()));
                    builder.append(' ');
                    builder.append(prop.name());
                    builder.append(", ");

                    builder2.append(prop.name());
                    builder2.append(", ");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.deleteCharAt(builder.length() - 1);
                builder2.deleteCharAt(builder2.length() - 1);
                builder2.deleteCharAt(builder2.length() - 1);
            } else {
                // skip, it's a command with no parameters.
            }


            writer.write(builder.toString());
            writer.write(") {");

            writeNewLine(writer);
            writer.write("        return new ");
            writer.write(descriptor.simpleName());
            writer.write("Impl(");
            writer.write(builder2.toString());
            writer.write(");");
            writeNewLine(writer);
            writer.write("    }");
            writeNewLine(writer);
        }
        writeNewLine(writer);
    }


}