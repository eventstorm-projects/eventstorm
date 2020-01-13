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
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.google.common.collect.ImmutableList;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandPropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.annotation.Constraint;
import eu.eventstorm.core.annotation.constrain.CustomPropertyValidator;
import eu.eventstorm.core.annotation.constrain.NotEmpty;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.ConstraintViolationImpl;
import eu.eventstorm.core.validation.PropertyValidators;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.util.ToStringBuilder;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CommandValidatorGenerator {

	private static final String TO_STRING_BUILDER = ToStringBuilder.class.getName();

	private static Logger logger;
	
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
		writeVariables(writer, descriptor);
		writeMethodValidate(writer, descriptor);
		writer.write("}");
		writer.close();
    	
       
        //writeHeader(writer, env, descriptor);
        //writeConstructor(writer, descriptor);
        //writeVariables(writer, descriptor);
        //writeMethods(writer, descriptor);
        //writeToStringBuilder(writer, descriptor);

        //writer.write("}");
        //writer.close();
    }

	
	private static boolean isConstraint(AnnotationMirror am) {
		for (AnnotationMirror annotationMirror : am.getAnnotationType().asElement().getAnnotationMirrors()) {
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
        writer.write("import "+ ConstraintViolationImpl.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ PropertyValidators.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ ImmutableList.class.getName() +";");
        writeNewLine(writer);
        
        writeNewLine(writer);
        writer.write("import static "+ ImmutableList.class.getName() +".of;");
        writeNewLine(writer);
        
        writeGenerated(writer,CommandValidatorGenerator.class.getName());

       
        writer.write("public final class ");
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
        
        writer.write("        ImmutableList.Builder<ConstraintViolation> builder = ImmutableList.builder();");
        writeNewLine(writer);
        
        for (CommandPropertyDescriptor ppd : descriptor.properties()) {
            for (AnnotationMirror am : ppd.getter().getAnnotationMirrors()) {
                if (isConstraint(am)) {
                    writeMethodPart(writer, descriptor, ppd, am);
                }
            }
        }
        
        writer.write("        return builder.build();");
        writeNewLine(writer);
        writer.write("    }");
    	writeNewLine(writer);

	}

    
    private void writeMethodPart(Writer writer, CommandDescriptor descriptor, CommandPropertyDescriptor ppd, AnnotationMirror am) throws IOException {
       
        if (NotEmpty.class.getName().equals(am.getAnnotationType().asElement().toString())) {
            writeMethodPartNotEmpty(writer, descriptor, ppd, am);
            return;
        }

        if (CustomPropertyValidator.class.getName().equals(am.getAnnotationType().asElement().toString())) {
            writeMethodPartCustomPropertyValidator(writer, descriptor, ppd, am);
            return;
        }
        
    }

    private void writeMethodPartNotEmpty(Writer writer, CommandDescriptor descriptor, CommandPropertyDescriptor ppd, AnnotationMirror am) throws IOException {
    	writer.write("        // validate property " + ppd.name() + " from " + am.toString());
    	writeNewLine(writer);
    	writer.write("        PropertyValidators.isEmpty().validate(");
        writer.write("\"" + ppd.name() + "\",");
        writer.write("command." + ppd.getter().getSimpleName().toString() + "(), builder);");
    	writeNewLine(writer);
    }

    
    private void writeMethodPartCustomPropertyValidator(Writer writer, CommandDescriptor descriptor, CommandPropertyDescriptor ppd, AnnotationMirror am) throws IOException {
    	writer.write("        // validate property " + ppd.name() + " from " + am.toString());
    	writeNewLine(writer);
    	writer.write("        VALIDATOR_CUSTOM_" + Helper.toUpperCase(Helper.firstToUpperCase(ppd.name())) +".validate(");
        writer.write("\"" + ppd.name() + "\",");
        writer.write("command." + ppd.getter().getSimpleName().toString() + "(), builder);");
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
    	for (CommandPropertyDescriptor ppd : descriptor.properties()) {
    		CustomPropertyValidator cpv = ppd.getter().getAnnotation(CustomPropertyValidator.class);
    		if (cpv != null) {
    	    	writeNewLine(writer);
    			String classname = getClassname(cpv);
    			writer.write("    private static final " + classname +" VALIDATOR_CUSTOM_" + Helper.toUpperCase(ppd.name()) + " = ");
    			writer.write(" new " + classname + "();" );
    	        writeNewLine(writer);
    		}
        }
    }


    private static String getClassname(CustomPropertyValidator cpv) {
    	try {
    	     return cpv.validateBy().getName();
    	} catch (MirroredTypeException e) {
    	    TypeMirror typeMirror = e.getTypeMirror();
    	    return typeMirror.toString();
    	}
    }
    
    

}