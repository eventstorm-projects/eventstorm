package eu.eventstorm.core.apt.command;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;
import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.AbstractCommandDescriptor;
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
public final class CommandJacksonModuleGenerator {

    private Logger logger;

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

        try (Logger logger = Logger.getLogger(processingEnvironment, "eu.eventstorm.event.generator", "CommandJacksonModuleGenerator")) {
            this.logger = logger;
            sourceCode.forEachAllCommandPackage((pack, list) -> {
                try {
                    generate(processingEnvironment, pack, list);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }


    }

    private void generate(ProcessingEnvironment env, String pack, ImmutableList<AbstractCommandDescriptor> descriptors) throws IOException {
        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".json.CommandModule") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".json.CommandModule" + "]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".json.CommandModule");
        Writer writer = object.openWriter();

        writeHeader(writer, pack + ".json");
        writeConstructor(writer, descriptors);

        writer.write("}");
        writer.close();
    }

    private static void writeHeader(Writer writer, String pack) throws IOException {
        writePackage(writer, pack);

        writeNewLine(writer);
        writeNewLine(writer);
        writer.write("import " + SimpleModule.class.getName() + ";");

        writeNewLine(writer);


        writeGenerated(writer, CommandJacksonModuleGenerator.class.getName());
        writer.write("@SuppressWarnings(\"serial\")");
        writeNewLine(writer);
        writer.write("public final class CommandModule extends SimpleModule {");
        writeNewLine(writer);
    }


    private static void writeConstructor(Writer writer, ImmutableList<AbstractCommandDescriptor> descriptors) throws IOException {
        writeNewLine(writer);
        writer.write("    public CommandModule() {");
        writeNewLine(writer);
        writer.write("        super();");
        writeNewLine(writer);
        for (AbstractCommandDescriptor cd : descriptors) {
            // addDeserializer(CreateUserCommand.class, new CreateUserCommandStdDeserializer());
            writer.write("        addDeserializer(" + cd.fullyQualidiedClassName() + ".class, new " + cd.simpleName() + "StdDeserializer());");
            writeNewLine(writer);
            writer.write("        addSerializer(" + cd.fullyQualidiedClassName() + ".class, new " + cd.simpleName() + "StdSerializer());");
            writeNewLine(writer);
            writeNewLine(writer);
        }

        writer.write("    }");
        writeNewLine(writer);
    }


}