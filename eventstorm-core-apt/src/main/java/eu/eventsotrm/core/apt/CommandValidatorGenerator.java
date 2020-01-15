package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
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
import eu.eventstorm.core.annotation.constraint.CustomPropertiesValidator;
import eu.eventstorm.core.annotation.constraint.CustomPropertiesValidators;
import eu.eventstorm.core.annotation.constraint.CustomPropertyValidator;
import eu.eventstorm.core.annotation.constraint.InstantiatorType;
import eu.eventstorm.core.annotation.constraint.NotEmpty;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.core.validation.PropertyValidators;
import eu.eventstorm.core.validation.Validator;
import eu.eventstorm.util.tuple.Tuple2;
import eu.eventstorm.util.tuple.Tuples;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class CommandValidatorGenerator {

	private Logger logger;
	
	private final List<Tuple2<String,CommandPropertyDescriptor>> variables = new ArrayList<Tuple2<String,CommandPropertyDescriptor>>();
	
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
		writeConstructor(writer, descriptor);
		writeMethodValidate(writer, descriptor);
		writer.write("}");
		writer.close();
       
    }
	
    private static void writeHeader(Writer writer, ProcessingEnvironment env, CommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString()+ ".validator");
       
        writeNewLine(writer);
        writer.write("import "+ Validator.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ ConstraintViolation.class.getName() +";");        
        writeNewLine(writer);
        writer.write("import "+ PropertyValidators.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ ImmutableList.class.getName() +";");
        writeNewLine(writer);
        writer.write("import org.springframework.stereotype.Component;");
        writeNewLine(writer);
        
        writeGenerated(writer,CommandValidatorGenerator.class.getName());
        writeNewLine(writer);
        writer.write("@Component");
        writeNewLine(writer);
        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Validator" );
        writer.write(" implements Validator<");
        writer.write(descriptor.fullyQualidiedClassName());
        writer.write("> {");
        writeNewLine(writer);
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
        
        if (descriptor.element().getAnnotation(CustomPropertiesValidator.class) != null) {
        	writeMethodPartCustomPropertiesValidator(writer, descriptor, descriptor.element().getAnnotation(CustomPropertiesValidator.class));
        }
        
        if (descriptor.element().getAnnotation(CustomPropertiesValidators.class) != null) {
        	for (CustomPropertiesValidator cpv : descriptor.element().getAnnotation(CustomPropertiesValidators.class).value()) {
        		writeMethodPartCustomPropertiesValidator(writer, descriptor, cpv);
        	}
        }
        
        writer.write("        return builder.build();");
        writeNewLine(writer);
        writer.write("    }");
    	writeNewLine(writer);

	}

    
    private void writeMethodPartCustomPropertiesValidator(Writer writer, CommandDescriptor descriptor, CustomPropertiesValidator cpv) throws IOException {
    	writeNewLine(writer);
        writer.write("        // validate properties " + Arrays.toString(cpv.properties()) + " from " + cpv.toString());
    	writeNewLine(writer);
		writer.write("        this.validatorCustomProperties" + Helper.firstToUpperCase(cpv.name()));
    	writer.write(".validate(PROPERTIES_" + Helper.toUpperCase(cpv.name()));
        writer.write(", ");
        writer.write(Tuples.class.getName() + ".of(");
        for(int i = 0; i < cpv.properties().length ; i++) {
        	writer.write("command.get" + Helper.firstToUpperCase(cpv.properties()[i]) + "()");
        	if (i + 1 < cpv.properties().length) {
        		writer.write(",");
        	}
        }
        writer.write("), builder);");
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
        writeNewLine(writer);
        writer.write("        // validate property " + ppd.name() + " from " + am.toString());
    	writeNewLine(writer);
    	writer.write("        PropertyValidators.isEmpty().validate(PROPERTIES_");
        writer.write(Helper.toUpperCase(ppd.name())+", ");
        writer.write("command." + ppd.getter().getSimpleName().toString() + "(), builder);");
    	writeNewLine(writer);
    }

    
    private void writeMethodPartCustomPropertyValidator(Writer writer, CommandDescriptor descriptor, CommandPropertyDescriptor ppd, AnnotationMirror am) throws IOException {
        writeNewLine(writer);
        writer.write("        // validate property " + ppd.name() + " from " + am.toString());
    	writeNewLine(writer);
		CustomPropertyValidator cpv = ppd.getter().getAnnotation(CustomPropertyValidator.class);
		if(InstantiatorType.STATIC == cpv.instantiator()) {
			writer.write("        VALIDATOR_CUSTOM_" + Helper.toUpperCase(Helper.firstToUpperCase(ppd.name())));
		} else {
			writer.write("        this.validatorCustom" + Helper.firstToUpperCase(Helper.firstToUpperCase(ppd.name())));
		}
    	writer.write(".validate(PROPERTIES_");
        writer.write(Helper.toUpperCase(ppd.name()) + ", ");
        writer.write("command." + ppd.getter().getSimpleName().toString() + "(), builder);");
    	writeNewLine(writer);
    }
    
    private void writeConstructor(Writer writer, CommandDescriptor descriptor) throws IOException {
    	writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Validator");
        writer.write("(");
        
        for (int i = 0 ; i < variables.size(); i++) {
        	Tuple2<String, CommandPropertyDescriptor> var = variables.get(i);
        	writer.write(var.getT1());
        	writer.write(" validatorCustom" + Helper.firstToUpperCase(var.getT2().name()));
        	if (i + 1 < variables.size()) {
        		writer.write(",");
                writeNewLine(writer);
                writer.write("                 ");
        	}
        }
        
        // for @CustomPropertiesValidator
    	CustomPropertiesValidator cpv = descriptor.element().getAnnotation(CustomPropertiesValidator.class);
    	if (cpv != null) {
    		if (!variables.isEmpty()) {
    			writer.write(",");
                writeNewLine(writer);
                writer.write("                 ");
    		}
    		writer.write(getClassname(cpv));
        	writer.write(" validatorCustomProperties" + Helper.firstToUpperCase(cpv.name()));
    	} else {
    		// for @CustomPropertiesValidator
        	CustomPropertiesValidators cpvs = descriptor.element().getAnnotation(CustomPropertiesValidators.class);
        	if (cpvs != null) {
        		if (!variables.isEmpty()) {
        			writer.write(",");
                    writeNewLine(writer);
                    writer.write("                 ");
        		}
        		for (int i = 0; i < cpvs.value().length ; i++) {
        			writer.write(getClassname(cpvs.value()[i]));
                	writer.write(" validatorCustomProperties" + Helper.firstToUpperCase(cpvs.value()[i].name()));
                	if (i + 1 < cpvs.value().length) {
                		writer.write(",");
                        writeNewLine(writer);
                        writer.write("                 ");
                	}
        		}
        	}    		
    	}
    	
        
        writer.write(") {");
        writeNewLine(writer);
    	
        for (int i = 0 ; i < variables.size(); i++) {
        	Tuple2<String, CommandPropertyDescriptor> var = variables.get(i);
        	writer.write("        this.validatorCustom" + Helper.firstToUpperCase(var.getT2().name()));
        	writer.write(" = validatorCustom" + Helper.firstToUpperCase(var.getT2().name()) + ";");
            writeNewLine(writer);
        }
        
        // for @CustomPropertiesValidator
    	if (cpv != null) {
        	writer.write("        this.validatorCustomProperties" + Helper.firstToUpperCase(cpv.name()));
        	writer.write(" = validatorCustomProperties" + Helper.firstToUpperCase(cpv.name()) + ";");
            writeNewLine(writer);
    	} else {
    		// for @CustomPropertiesValidator
        	CustomPropertiesValidators cpvs = descriptor.element().getAnnotation(CustomPropertiesValidators.class);
        	if (cpvs != null) {
        		for (int i = 0; i < cpvs.value().length ; i++) {
                	writer.write("        this.validatorCustomProperties" + Helper.firstToUpperCase(cpvs.value()[i].name()));
                	writer.write(" = validatorCustomProperties" + Helper.firstToUpperCase(cpvs.value()[i].name()) + ";");
                    writeNewLine(writer);
        		}
        	}    		
    	}
        
        writer.write("    }");
        writeNewLine(writer);
    }

    private void writeVariables(Writer writer, CommandDescriptor descriptor) throws IOException {
    	
    	// for @CustomPropertyValidator
    	for (CommandPropertyDescriptor ppd : descriptor.properties()) {
    		 for (AnnotationMirror am : ppd.getter().getAnnotationMirrors()) {
                 if (isConstraint(am)) {
         			writer.write("    private static final ImmutableList<String> PROPERTIES_" + Helper.toUpperCase(ppd.name()) + " = ");
         			writer.write(" ImmutableList.of(\""+ ppd.name() + "\");");
        			writeNewLine(writer);
        			break;
                 }
             }
    	}
    	
    	// for @CustomPropertiesValidator
    	CustomPropertiesValidator cpvc = descriptor.element().getAnnotation(CustomPropertiesValidator.class);
    	CustomPropertiesValidators cpvcs = descriptor.element().getAnnotation(CustomPropertiesValidators.class);
    	if (cpvc != null) {
    		writeVariablesCustomPropertiesValidator(writer, cpvc);
    	} else {
    		// for @CustomPropertiesValidator
        	if (cpvcs != null) {
        		for (CustomPropertiesValidator  item : cpvcs.value()) {
        			writeVariablesCustomPropertiesValidator(writer, item);	
        		}
        	}	
    	}
    	
    	if (cpvc != null) {
    		writeVariablesCustomPropertiesValidatorValidator(writer, cpvc);
    	} else if (cpvcs != null) {
    		for (CustomPropertiesValidator  item : cpvcs.value()) {
    			writeVariablesCustomPropertiesValidatorValidator(writer, item);	
    		}
    	}
    	
		writeNewLine(writer);

    	for (CommandPropertyDescriptor ppd : descriptor.properties()) {
    		CustomPropertyValidator cpv = ppd.getter().getAnnotation(CustomPropertyValidator.class);
    		if (cpv != null) {
    			String classname = getClassname(cpv);
    			writeNewLine(writer);
    			if(InstantiatorType.STATIC == cpv.instantiator()) {
        			writer.write("    private static final " + classname +" VALIDATOR_CUSTOM_" + Helper.toUpperCase(ppd.name()) + " = ");
        			writer.write(" new " + classname + "();" );
        	        writeNewLine(writer);
        	        continue;
    			}
    			
    			if(InstantiatorType.CONSTRUCTOR == cpv.instantiator()) {
    				variables.add(Tuples.of(classname, ppd));
        			writer.write("    private final " + classname +" validatorCustom" + Helper.firstToUpperCase(ppd.name()) + ";");
        	        writeNewLine(writer);
        	        continue;
    			}

    			throw new IllegalStateException("Invalid cpv instantiator [" + cpv.instantiator() +"]");
    		}
        }
    }
    
	private void writeVariablesCustomPropertiesValidator(Writer writer, CustomPropertiesValidator cpvc) throws IOException {
		writer.write("    private static final ImmutableList<String> PROPERTIES_" + Helper.toUpperCase(cpvc.name()) + " = ");
		writer.write(" ImmutableList.of(");
		for (int i = 0; i < cpvc.properties().length; i++) {
			writer.write("\"" + cpvc.properties()[i] + "\"");
			if (i + 1 < cpvc.properties().length) {
				writer.write(",");
			}
		}
		writer.write(");");
		writeNewLine(writer);
	}

    private void writeVariablesCustomPropertiesValidatorValidator(Writer writer, CustomPropertiesValidator cpv) throws IOException {
    	writer.write("    private final " + getClassname(cpv) +" validatorCustomProperties" + Helper.firstToUpperCase(cpv.name()) + ";");
        writeNewLine(writer);
    }

    private static String getClassname(CustomPropertyValidator cpv) {
    	try {
    	     return cpv.validateBy().getName();
    	} catch (MirroredTypeException e) {
    	    TypeMirror typeMirror = e.getTypeMirror();
    	    return typeMirror.toString();
    	}
    }
    
    private static String getClassname(CustomPropertiesValidator cpv) {
    	try {
    	     return cpv.validateBy().getName();
    	} catch (MirroredTypeException e) {
    	    TypeMirror typeMirror = e.getTypeMirror();
    	    return typeMirror.toString();
    	}
    }
    
    private static boolean isConstraint(AnnotationMirror am) {
		for (AnnotationMirror annotationMirror : am.getAnnotationType().asElement().getAnnotationMirrors()) {
			if (Constraint.class.getName().equals(annotationMirror.getAnnotationType().asElement().toString())) {
				return true;
			}
		}
		return false;
    }
	

}