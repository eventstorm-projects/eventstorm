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

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.annotation.Constraint;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.Validator;
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

    	
    	String fcqn = env.getElementUtils().getPackageOf(descriptor.element()).toString() + ".validator." + descriptor.simpleName() + "Validator" ;

		
		
        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(fcqn) != null) {
            logger.info("Java SourceCode already exist [" +fcqn + "]");
            return;
        }

    	JavaFileObject object = env.getFiler().createSourceFile(fcqn);
		Writer writer = object.openWriter();
		writeHeader(writer, env, descriptor);
		writeMethodValidate(writer, descriptor);
		writer.write("}");
		writer.close();
    	
       
    	for (CommandPropertyDescriptor ppd : descriptor.properties()) {
    		for (AnnotationMirror am : ppd.getter().getAnnotationMirrors()) {
    			if (isConstraint(am)) {
    				
    			//	createValidator(env, am, descriptor, ppd);
    				
    				
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

	
	private boolean isConstraint(AnnotationMirror am) {
		for (AnnotationMirror annotationMirror : am.getAnnotationType().asElement().getAnnotationMirrors()) {
			logger.info("isConstraint ? : [" + annotationMirror.getAnnotationType().asElement().toString()+"] [" + Constraint.class.getName() + "]");
			if (Constraint.class.getName().equals(annotationMirror.getAnnotationType().asElement().toString())) {
				return true;
			}
		}
		return false;

    }
	
    private static void writeHeader(Writer writer, ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString()+ ".validator");
       
        writeNewLine(writer);
        writer.write("import "+ Validator.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ ConstraintViolation.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ ImmutableList.class.getName() +";");
        writeNewLine(writer);
        writeNewLine(writer);
        writer.write("import static "+ ImmutableList.class.getName() +".of;");
        writeNewLine(writer);
        
        writeGenerated(writer,CommandValidatorGenerator.class.getName());

       
        writer.write("final class ");
        writer.write(descriptor.simpleName() + "Validator" );
        writer.write(" implements Validator<");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write("> {");
        writeNewLine(writer);
    }

    private void writeMethodValidate(Writer writer, CommandDescriptor descriptor) throws IOException {
    	writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ImmutableList<ConstraintViolation> validate("+ descriptor.fullyQualidiedClassName()+" command) {");
        writeNewLine(writer);
        writer.write("        return of();");
        writeNewLine(writer);
        writer.write("    }");
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