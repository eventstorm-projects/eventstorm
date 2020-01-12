package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.getReturnType;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.PackageElement;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.annotation.Constraint;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CommandValidatorGenerator {

	private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

	private final Logger logger;
	
	CommandValidatorGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandValidatorGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
    	// generate Implementation class;
        sourceCode.forEachCommand(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void generate(ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
       // if (env.getElementUtils().getTypeElement(descriptor.fullyQualidiedClassName() + "Impl") != null) {
        //    logger.info("Java SourceCode already exist [" +descriptor.fullyQualidiedClassName() + "Impl" + "]");
        //    return;
        //}

    	
    	
       
    	for (CommandPropertyDescriptor ppd : descriptor.properties()) {
    		for (AnnotationMirror am : ppd.getter().getAnnotationMirrors()) {
    			if (isConstraint(am)) {
    				
    				createValidator(env, am, descriptor, ppd);
    				
    				
    			}
    		}
        }
    	
        //writeHeader(writer, env, descriptor);
        //writeConstructor(writer, descriptor);
        //writeVariables(writer, descriptor);
        //writeMethods(writer, descriptor);
        //writeToStringBuilder(writer, descriptor);

        //writer.write("}");
        //writer.close();
    }

	private void createValidator(ProcessingEnvironment env, AnnotationMirror am, CommandDescriptor descriptor, CommandPropertyDescriptor ppd)
	        throws IOException {

		String fcqn = env.getElementUtils().getPackageOf(descriptor.element()).toString() + ".validator." + descriptor.simpleName() + "_" + Helper.firstToUpperCase(ppd.name()) + "_"
		        + am.getAnnotationType().asElement().getSimpleName().toString();

		JavaFileObject object = env.getFiler().createSourceFile(fcqn);
		Writer writer = object.openWriter();
		writeHeader(writer, env, descriptor, ppd, am);
		writer.write("}");
		writer.close();
	}


	private boolean isConstraint(AnnotationMirror am) {
		for (AnnotationMirror annotationMirror : am.getAnnotationType().asElement().getAnnotationMirrors()) {
			logger.info("isConstraint ? : [" + annotationMirror.getAnnotationType().asElement().toString()+"] [" + Constraint.class.getName() + "]");
			if (Constraint.class.getName().equals(annotationMirror.getAnnotationType().asElement().toString())) {
				return true;
			}
		}
		return false;

    }
	
    private static void writeHeader(Writer writer, ProcessingEnvironment env, CommandDescriptor descriptor, CommandPropertyDescriptor ppd, AnnotationMirror am) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString()+ ".validator");
        writeGenerated(writer,CommandValidatorGenerator.class.getName());

        writer.write("final class ");
        writer.write(descriptor.simpleName() + "_" + Helper.firstToUpperCase(ppd.name()) + "_"
		        + am.getAnnotationType().asElement().getSimpleName().toString());
      //  writer.write(" implements ");
       // writer.write(descriptor.fullyQualidiedClassName());
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, CommandDescriptor descriptor) throws IOException {
    	writeNewLine(writer);
        writer.write("    ");
        writer.write(descriptor.simpleName() + "Impl");
        writer.write("(");
        
        StringBuilder builder = new StringBuilder();
    	for (CommandPropertyDescriptor ppd : descriptor.properties()) {
    		builder.append(getReturnType(ppd.getter()));
    		builder.append(" ");
    		builder.append(ppd.variable());
    		builder.append(",");
        }
    	
    	builder.deleteCharAt(builder.length() -1);
    	writer.write(builder.toString());
    	writer.write(") {");
    	writeNewLine(writer);
        
    	for (CommandPropertyDescriptor ppd : descriptor.properties()) {
    		 writer.write("        this.");
            writer.write(ppd.variable());
            writer.write(" = ");
            writer.write(ppd.variable());
            writer.write(";");
            writeNewLine(writer);
        }
    	
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
            writer.write(";");
            writeNewLine(writer);
        }
    }

    private static void writeMethods(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethods(writer, descriptor.properties());
        writerKeyMethod(writer, descriptor);
    }

    
    private static void writerKeyMethod(Writer writer, CommandDescriptor descriptor) {
		
	}

	private static void writeMethods(Writer writer, List<CommandPropertyDescriptor> descriptors) throws IOException {
        for (CommandPropertyDescriptor ppd : descriptors) {
            writeGetter(writer, ppd);
        }
    }

    private static void writeGetter(Writer writer, CommandPropertyDescriptor ppd) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(getReturnType(ppd.getter()));
        writer.write(' ');
        writer.write(ppd.getter().getSimpleName().toString());
        writer.write("() {");
        writeNewLine(writer);
        writer.write("        return this.");
        writer.write(ppd.variable());
        writer.write(";");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

    private static void writeToStringBuilder(Writer writer, CommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public String toString() {");
        writeNewLine(writer);
        writer.write("        " + TO_STRING_BUILDER + " builder = new " + TO_STRING_BUILDER + "(this);");
        writeNewLine(writer);
       
        for (CommandPropertyDescriptor ppd : descriptor.properties()) {
            writer.write("        builder.append(\"");
            writer.write(ppd.name());
            writer.write("\", this.");
            writer.write(ppd.variable());
            writer.write(");");
            writeNewLine(writer);
        }
        writer.write("        return builder.toString();");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
    }

}