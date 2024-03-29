package eu.eventstorm.core.apt.command;

import com.google.common.collect.ImmutableMap;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.CommandDescriptor;
import eu.eventstorm.cqrs.CommandException;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandExceptionGenerator {

    private Logger logger;

    public CommandExceptionGenerator() {
    }

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.generator", "CommandExceptionGenerator")) {
            this.logger = logger;
            sourceCode.forEachCommand(t -> {
                try {
                    generate(processingEnvironment, t);
                } catch (Exception cause) {
                    logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }
    }

    private void generate(ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Exception") != null) {
            logger.info("Java SourceCode already exist [" + descriptor.fullyQualidiedClassName() + "Exception" + "]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Exception");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());

        writer.write("import " + ImmutableMap.class.getName() + ";");
        writeNewLine(writer);

        writeGenerated(writer, CommandExceptionGenerator.class.getName());
        writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Exception");
        writer.write(" extends ");
        writer.write(CommandException.class.getName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Exception");
        writer.write("(" + descriptor.simpleName() + " command, String message) {");
        writeNewLine(writer);
        writer.write("        super(command, message, ImmutableMap.of());");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Exception");
        writer.write("(" + descriptor.simpleName() + " command, String message, Throwable cause) {");
        writeNewLine(writer);
        writer.write("        super(command, message, cause, ImmutableMap.of());");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);

        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Exception");
        writer.write("(" + descriptor.simpleName() + " command, String message, Throwable cause, ImmutableMap<String, Object> parameters) {");
        writeNewLine(writer);
        writer.write("        super(command, message, cause, parameters);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}