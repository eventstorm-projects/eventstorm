package eu.eventstorm.sql.apt;

import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.ViewDescriptor;
import eu.eventstorm.sql.jdbc.ResultSetMapper;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class ViewMapperFactoryGenerator implements Generator {

    private Logger logger;

	public ViewMapperFactoryGenerator() {
	}

    public void generate(ProcessingEnvironment processingEnv, SourceCode sourceCode) {
        try (Logger l = Logger.getLogger(processingEnv, "eu.eventstorm.sql.generator", "ViewMapperFactoryGenerator")) {
            this.logger = l;
            sourceCode.forEachViewByPackage((pack, descriptors) -> {
                try {
                    create(processingEnv, pack, descriptors);
                } catch (Exception cause) {
                    logger.error("ViewMapperFactoryGenerator -> Exception for [" + pack + "] -> [" + cause.getMessage() + "]", cause);
                }
            });
        }
    }

    public void create(ProcessingEnvironment env, String pack, List<ViewDescriptor> descriptors) throws IOException {

        JavaFileObject object = env.getFiler().createSourceFile(pack + ".Mappers");
        Writer writer = object.openWriter();

        Helper.writePackage(writer, pack);
        Helper.writeGenerated(writer, ViewMapperFactoryGenerator.class.getName());

        writer.write("public final class Mappers {");
        Helper.writeNewLine(writer);

        for (ViewDescriptor desc : descriptors) {
            Helper.writeNewLine(writer);
            writer.write("    public static final ");
            writer.write(ResultSetMapper.class.getName());
            writer.write("<");
            writer.write(desc.element().toString());
            writer.write("> ");

            writer.write(Helper.toUpperCase(desc.element().getSimpleName().toString()));

            writer.write(" = new ");
            writer.write(desc.element().getSimpleName().toString());
            writer.write("Mapper();");
            Helper.writeNewLine(writer);

        }

        Helper.writeNewLine(writer);
        writer.write('}');
        writer.close();
    }

}
