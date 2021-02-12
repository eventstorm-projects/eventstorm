package eu.eventstorm.sql.apt;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.sql.apt.model.PojoDescriptor;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoFactoryGenerator implements Generator {


    private final Logger logger;

	PojoFactoryGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PojoMapperFactoryGenerator.class);
	}

    public void generate(ProcessingEnvironment env, SourceCode sourceCode) {

        sourceCode.forEachByPackage((pack, descriptors) -> {
            try {
                create(env, pack, descriptors);
            } catch (Exception cause) {
                logger.error("PojoFactoryGenerator -> Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }



    private void create(ProcessingEnvironment env, String pack, List<PojoDescriptor> descriptors) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Factory");
        Writer writer = object.openWriter();

        writePackage(writer, pack);
        writeGenerated(writer, PojoFactoryGenerator.class.getName());

        writer.write("public final class Factory {");
        writeNewLine(writer);

        for (PojoDescriptor desc : descriptors) {
            writeNewLine(writer);
            writer.write("    public static final ");
            writer.write(desc.element().toString());
            writer.write(" new");
            writer.write(desc.element().getSimpleName().toString());
            writer.write("() {");
            writeNewLine(writer);
            writer.write("        ");
            writer.write("return new ");
            writer.write(desc.element().getSimpleName().toString() + "Impl");
            writer.write("();");
            writeNewLine(writer);
            writer.write("    }");
            writeNewLine(writer);
        }

        writeNewLine(writer);
        writer.write('}');
        writer.close();
    }
}
