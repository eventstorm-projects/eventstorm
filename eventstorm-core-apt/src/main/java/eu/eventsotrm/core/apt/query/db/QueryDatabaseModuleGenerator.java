package eu.eventsotrm.core.apt.query.db;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.DatabaseViewQueryDescriptor;
import eu.eventsotrm.core.apt.model.QueryDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsQueryDatabaseView;
import eu.eventstorm.sql.Module;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class QueryDatabaseModuleGenerator {

    private final Logger logger;

    public QueryDatabaseModuleGenerator() {
		logger = LoggerFactory.getInstance().getLogger(QueryDatabaseModuleGenerator.class);
	}

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {

        sourceCode.forEachDatabaseViewQueryPackage((pack, descriptors) -> {
            try {
                create(env, pack, descriptors);
            } catch (Exception cause) {
                logger.error("QueryDatabaseModuleGenerator -> Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void create(ProcessingEnvironment env, String pack, List<DatabaseViewQueryDescriptor> descriptors) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(pack + ".Module") != null) {
            logger.info("Java SourceCode already exist [" + pack + ".Module" + "]");
            return;
        }
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

    private static void writeConstructor(Writer writer, ProcessingEnvironment env, String classname, List<DatabaseViewQueryDescriptor> descriptors) throws IOException {

        writeNewLine(writer);
        writer.write("     public " + classname + "(String name, String catalog) {");
        writeNewLine(writer);
        writer.write("         super(name, catalog");

        for (QueryDescriptor desc : descriptors) {
            if (desc.element().getAnnotation(CqrsQueryDatabaseView.class) != null) {
                if (((TypeElement)desc.element()).getInterfaces().size() == 0) {
                    writer.write(", ");
                    writer.write(desc.simpleName() + "Descriptor.INSTANCE");
                }
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
