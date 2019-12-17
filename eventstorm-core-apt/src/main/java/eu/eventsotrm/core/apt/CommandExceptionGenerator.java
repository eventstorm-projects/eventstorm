package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.CommandException;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CommandExceptionGenerator {

	private final Logger logger;

	CommandExceptionGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandExceptionGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        // generate Implementation class;
        sourceCode.forEachCommand(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });


    }

    private void generate(ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Exception");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeConstructor(writer, descriptor);
        writeVariables(writer, descriptor);
        writeMethods(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        
        writer.write("import " + ImmutableMap.class.getName() + ";");
        writeNewLine(writer);
        
        writeGenerated(writer,CommandExceptionGenerator.class.getName());
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
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Exception");
        writer.write("(String message, " + descriptor.simpleName() + " command) {");
        writeNewLine(writer);
        writer.write("        super(message, ImmutableMap.of());");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
        
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Exception");
        writer.write("(String message, Throwable cause," + descriptor.simpleName() + " command) {");
        writeNewLine(writer);
        writer.write("        super(message, cause, ImmutableMap.of());");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
        
        writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Exception");
        writer.write("(String message, Throwable cause," + descriptor.simpleName() + " command, ImmutableMap<String, Object> parameters) {");
        writeNewLine(writer);
        writer.write("        super(message, cause, parameters);");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private ");
        writer.write(descriptor.simpleName());
        writer.write(" command;");
    }

    private static void writeMethods(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeGetter(writer, descriptor);
     
    }

    private static void writeGetter(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName());
        writer.write(" getCommand() {");
        writeNewLine(writer);
        writer.write("        return this.command;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}