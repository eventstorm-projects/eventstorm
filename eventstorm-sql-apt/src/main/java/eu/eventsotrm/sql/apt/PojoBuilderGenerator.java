package eu.eventsotrm.sql.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;


import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.PojoPropertyDescriptor;
import eu.eventstorm.sql.annotation.CreateTimestamp;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.annotation.UpdateTimestamp;


/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class PojoBuilderGenerator implements Generator {

	private final Logger logger;

	PojoBuilderGenerator() {
		logger = LoggerFactory.getInstance().getLogger(PojoImplementationGenerator.class);
	}

    @Override
    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
        // generate Implementation class;
        sourceCode.forEach(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

     private void generate(ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {
        JavaFileObject object = env.getFiler().createSourceFile(descriptor.fullyQualidiedClassName() + "Builder");
        Writer writer = object.openWriter();

        writeHeader(writer, env, descriptor);
        writeVariables(writer, descriptor);
        writeConstructor(writer, descriptor);
        writeMethods(writer, descriptor);
        writeBuildMethod(writer, descriptor);

        writer.write("}");
        writer.close();
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, PojoDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer,PojoBuilderGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName().concat("Builder"));
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName().concat("Builder"));
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        this.");
        writer.write(descriptor.simpleName().toLowerCase());
        writer.write("$ = new ");
        writer.write(descriptor.fullyQualidiedClassName().concat("Impl"));
        writer.write("();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeVariables(Writer writer, PojoDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    private final ");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write(' ');
        writer.write(descriptor.simpleName().toLowerCase());
        writer.write("$;");
        writeNewLine(writer);
    }

    private static void writeMethods(Writer writer, PojoDescriptor descriptor) throws IOException {


        for (PojoPropertyDescriptor ppd : descriptor.properties()) {

        	if (ppd.getter().getAnnotation(CreateTimestamp.class) != null) {
        		// skip
        		continue;
        	}

        	if (ppd.getter().getAnnotation(UpdateTimestamp.class) != null) {
        		// skip
        		continue;
        	}

            writeMethodProperty(writer, descriptor, ppd);
        }

        for (PojoPropertyDescriptor ppd : descriptor.ids()) {
        	if (ppd.getter().getAnnotation(Sequence.class) != null) {
        		// skip
        		continue;
        	}
        	writeMethodProperty(writer, descriptor, ppd);
        }

    }

    private static void writeBuildMethod(Writer writer, PojoDescriptor descriptor) throws IOException {
    	 writeNewLine(writer);
         writer.write("    public ");
         writer.write(descriptor.fullyQualidiedClassName());
         writer.write(" build() {" );
         writeNewLine(writer);

         writer.write("        return ");
         writer.write(descriptor.simpleName().toLowerCase());
         writer.write("$;");

         writeNewLine(writer);
         writer.write("    }");
         writeNewLine(writer);
    }

    private static void writeMethodProperty(Writer writer, PojoDescriptor descriptor, PojoPropertyDescriptor ppd) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName().concat("Builder"));
        writer.write(' ');
        writer.write(ppd.name());
        writer.write('(');
        writer.write(ppd.getter().getReturnType().toString());
        writer.write(' ');
        writer.write(ppd.name());
        writer.write(") {");
        writeNewLine(writer);

        writer.write("        ");
        writer.write(descriptor.simpleName().toLowerCase());
        writer.write("$.");
        writer.write(ppd.setter().getSimpleName().toString());
        writer.write('(');
        writer.write(ppd.name());
        writer.write(");");
        writeNewLine(writer);

        writer.write("        return this;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}