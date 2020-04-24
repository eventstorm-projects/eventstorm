package eu.eventsotrm.core.apt.command;

import static eu.eventsotrm.sql.apt.Helper.getReturnType;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.PropertyDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandFactoryGenerator {

	private final Logger logger;

	public CommandFactoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandFactoryGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        // generate Implementation class;
        sourceCode.forEachCommandPackage((pack, list) -> {
            try {
                generate(processingEnvironment, pack, list);
            } catch (Exception cause) {
            	logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
            }
        });


    }

    private void generate(ProcessingEnvironment env, String pack, ImmutableList<CommandDescriptor> descriptors) throws IOException {
        
        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".CommandFactory") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".CommandFactory" + "]");
            return;
        }
        
        JavaFileObject object = env.getFiler().createSourceFile(pack + ".CommandFactory");
        Writer writer = object.openWriter();

        writeHeader(writer, pack, descriptors);
        writeConstructor(writer);
        writeMethods(writer, descriptors);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, String pack, ImmutableList<CommandDescriptor> descriptors) throws IOException {
        writePackage(writer, pack);
        
        for (CommandDescriptor descriptor : descriptors) {
        	 writer.write("import ");
             writer.write(descriptor.fullyQualidiedClassName());
             writer.write(";");
             writeNewLine(writer);
        }
        
        writeGenerated(writer,CommandFactoryGenerator.class.getName());
        writer.write("public final class CommandFactory {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer) throws IOException {
        writeNewLine(writer);
        writer.write("    private CommandFactory");
        writer.write("(){}");
        writeNewLine(writer);
    }

    private static void writeMethods(Writer writer, List<CommandDescriptor> descriptors) throws IOException {
        for (CommandDescriptor descriptor : descriptors) {
        	writeNewLine(writer);
            writer.write("    public static ");
            writer.write(descriptor.simpleName());
            writer.write(" new");
            writer.write(descriptor.simpleName());
            writer.write("(");
            
            StringBuilder builder = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            for (PropertyDescriptor prop : descriptor.properties()) {
            	builder.append(getReturnType(prop.getter()));
            	builder.append(' ');
            	builder.append(prop.name());
            	builder.append(", ");
            	
            	builder2.append(prop.name());
            	builder2.append(", ");
            }
            builder.deleteCharAt(builder.length() -1);
            builder.deleteCharAt(builder.length() -1);
            builder2.deleteCharAt(builder2.length() -1);
            builder2.deleteCharAt(builder2.length() -1);
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