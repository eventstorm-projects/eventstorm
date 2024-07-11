package eu.eventstorm.sql.apt;

import eu.eventstorm.sql.Module;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.PojoDescriptor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class ModuleGenerator implements Generator {

    private Logger logger;

    ModuleGenerator() {
    }

    public void generate(ProcessingEnvironment processingEnv, SourceCode sourceCode) {

        try (Logger l = Logger.getLogger(processingEnv, "eu.eventstorm.sql.generator", "ModuleGenerator")) {
            this.logger = l;
            sourceCode.forEachByPackage((pack, descriptors) -> {
                try {
                    create(processingEnv, pack, descriptors);
                } catch (Exception cause) {
                    logger.error("Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }

    }

    private void create(ProcessingEnvironment env, String pack, List<PojoDescriptor> descriptors) throws IOException {


        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Module");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, ModuleGenerator.class.getName());

        writer.write("public final class Module extends ");
        writer.write(Module.class.getName());
        writer.write(" { ");
        writeNewLine(writer);

        writeConstructor(writer, env, "Module", descriptors);

        writeNewLine(writer);
        writer.write('}');
        writer.close();
    }

    private static void writeConstructor(Writer writer, ProcessingEnvironment env, String classname, List<PojoDescriptor> descriptors) throws IOException {

        writeNewLine(writer);
        writer.write("    public " + classname + "(String name) {");
        writeNewLine(writer);
        writer.write("         super(name");

        for (PojoDescriptor desc : descriptors) {
            writer.write(", ");
            writer.write(desc.simpleName() + "Descriptor.INSTANCE");
        }
        writer.write(");");
        writeNewLine(writer);
        writer.write("    }");

        writeNewLine(writer);
        writer.write("    public " + classname + "(String name, String catalog) {");
        writeNewLine(writer);
        writer.write("         super(name, catalog");

        for (PojoDescriptor desc : descriptors) {
            writer.write(", ");
            writer.write(desc.simpleName() + "Descriptor.INSTANCE");
        }
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");


        writeNewLine(writer);
        writer.write("    public " + classname + "(String name, String catalog, String prefix) {");
        writeNewLine(writer);
        writer.write("         super(name, catalog, prefix");

        for (PojoDescriptor desc : descriptors) {
            writer.write(", ");
            writer.write(desc.simpleName() + "Descriptor.INSTANCE");
        }
        writer.write(");");

        writeNewLine(writer);
        writer.write("    }");

    }

}
