package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.model.QueryDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.annotation.CqrsQueryDatabaseView;
import eu.eventstorm.sql.Module;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class QueryDatabaseModuleGenerator {

    private final Logger logger;

	QueryDatabaseModuleGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryDatabaseModuleGenerator.class);
	}

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {

        sourceCode.forEachQueryPackage((pack, descriptors) -> {
            try {
                create(env, pack, descriptors);
            } catch (Exception cause) {
                logger.error("QueryDatabaseModuleGenerator -> Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void create(ProcessingEnvironment env, String pack, List<QueryDescriptor> descriptors) throws IOException {


        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Module");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, QueryDatabaseModuleGenerator.class.getName());

        writer.write("public final class " + "Module" + " extends ");
        writer.write(Module.class.getName());
        writer.write(" { ");
        writeNewLine(writer);

        writeConstructor(writer, env, "Module", descriptors);

        writeNewLine(writer);
        writer.write('}');
        writer.close();
    }

    private static void writeConstructor(Writer writer, ProcessingEnvironment env, String classname, List<QueryDescriptor> descriptors) throws IOException {

        writeNewLine(writer);
        writer.write("     public " + classname + "(String name, String catalog) {");
        writeNewLine(writer);
        writer.write("         super(name, catalog");

        for (QueryDescriptor desc : descriptors) {
            if (desc.element().getAnnotation(CqrsQueryDatabaseView.class) != null) {
                writer.write(", ");
                writer.write(desc.simpleName() + "Descriptor.INSTANCE");
            }
        }
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");
        
        
        writeNewLine(writer);
        writer.write("     public " + classname + "(String name, String catalog, String prefix) {");
        writeNewLine(writer);
        writer.write("         super(name, catalog, prefix");

        for (QueryDescriptor desc : descriptors) {
            writer.write(", ");
            writer.write(desc.simpleName() + "Descriptor.INSTANCE");
        }
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");

    }

}
