package eu.eventsotrm.core.apt.query;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.DatabaseQueryDescriptor;
import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryJacksonModuleGenerator {

	private final Logger logger;

	public QueryJacksonModuleGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryJacksonModuleGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
		// generate Implementation class;
		sourceCode.forEachDatabaseQueryPackage((pack, list) -> {
			try {
				generate(processingEnvironment, pack, list);
			} catch (Exception cause) {
				logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

	}

    private void generate(ProcessingEnvironment env, String pack, ImmutableList<DatabaseQueryDescriptor> descriptors) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".json.EventPayloadModule") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".json.EventPayloadModule"+ "]");
            return;
        }

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".json.QueryModule");
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

		writeGenerated(writer, QueryJacksonModuleGenerator.class.getName());
	    writer.write("@SuppressWarnings(\"serial\")");
	    writeNewLine(writer);
		writer.write("public final class QueryModule extends SimpleModule {");
		writeNewLine(writer);
	}

	
	
	private static void writeConstructor(Writer writer, ImmutableList<DatabaseQueryDescriptor> descriptors) throws IOException {
		writeNewLine(writer);
		writer.write("    public QueryModule() {");
		writeNewLine(writer);
		writer.write("        super();");
		writeNewLine(writer);
		for (DatabaseQueryDescriptor ed : descriptors) {
			// addDeserializer(CreateUserCommand.class, new CreateUserCommandStdDeserializer());
			//writer.write("        addDeserializer(" + ed.fullyQualidiedClassName() + ".class, new " + ed.simpleName() + "StdDeserializer());");
			//writeNewLine(writer);
			writer.write("        addSerializer(" + ed.fullyQualidiedClassName() + ".class, new " + ed.simpleName() + "StdSerializer());");
			writeNewLine(writer);
		}
		
		writer.write("    }");
		writeNewLine(writer);
	}


}