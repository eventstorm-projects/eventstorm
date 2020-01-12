package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.getReturnType;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CommandBuilderGenerator {

	private final Logger logger;

	CommandBuilderGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandBuilderGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        sourceCode.forEachCommand(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void generate(ProcessingEnvironment env, CommandDescriptor cd) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(cd.fullyQualidiedClassName() + "Builder") != null) {
            logger.info("Java SourceCode already exist [" +cd.fullyQualidiedClassName() + "Builder" + "]");
            return;
        }
        
        JavaFileObject object = env.getFiler().createSourceFile(cd.fullyQualidiedClassName() + "Builder");
        Writer writer = object.openWriter();

        writeHeader(writer, env, cd);
        writeConstructor(writer, cd);
        writeVariables(writer, cd);
        writeMethods(writer, cd);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer,CommandBuilderGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Builder");
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Builder");
        writer.write("() {");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeVariables(writer, descriptor.properties());
    }

    private static void writeVariables(Writer writer, List<CommandPropertyDescriptor> descriptors) throws IOException {
    	for (CommandPropertyDescriptor ppd : descriptors) {
            writer.write("    private ");
            writer.write(getReturnType(ppd.getter()));
            writer.write(" ");
            writer.write(ppd.variable());
            writer.write("$$;");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethod(writer, descriptor);
        writeBuildMethod(writer, descriptor);
    }

	private static void writeBuildMethod(Writer writer, CommandDescriptor ed) throws IOException {
		writeNewLine(writer);
        writer.write("    public ");
        writer.write(ed.simpleName());
        writer.write(' ');
        writer.write("build() {");
        writeNewLine(writer);
        writer.write("        return CommandFactory.new" + ed.simpleName() + '(');
        writeNewLine(writer);
        for (int i = 0; i < ed.properties().size(); i++) {
            writer.write("            " + ed.properties().get(i).name() + "$$");
            if (i+1 < ed.properties().size()) {
            	writer.write(",");
            }
            writeNewLine(writer);
        }
        writer.write("            );");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
	}

	private static void writeMethod(Writer writer, CommandDescriptor descriptor) throws IOException {
        for (CommandPropertyDescriptor ppd : descriptor.properties()) {
            writeMethod(writer, descriptor, ppd);
        }
    }

    private static void writeMethod(Writer writer,CommandDescriptor ed, CommandPropertyDescriptor epd) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(ed.simpleName() + "Builder");
        writer.write(" with");
        writer.write(Helper.firstToUpperCase(epd.name()));
        writer.write("(");
        writer.write(getReturnType(epd.getter()));
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