package eu.eventstorm.core.apt.command;

import static eu.eventstorm.sql.apt.Helper.getReturnType;
import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

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

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.AbstractCommandDescriptor;
import eu.eventstorm.core.apt.model.PropertyDescriptor;
import eu.eventstorm.sql.apt.Helper;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.Constraint;
import eu.eventstorm.annotation.CqrsCommand;
import eu.eventstorm.annotation.CqrsCommandType;
import eu.eventstorm.annotation.constraint.CustomPropertiesValidator;
import eu.eventstorm.annotation.constraint.CustomPropertiesValidators;
import eu.eventstorm.annotation.constraint.CustomPropertyValidator;
import eu.eventstorm.annotation.constraint.InstantiatorType;
import eu.eventstorm.annotation.constraint.NotEmpty;
import eu.eventstorm.annotation.constraint.NotNull;
import eu.eventstorm.core.validation.ConstraintViolation;
import eu.eventstorm.cqrs.CommandContext;
import eu.eventstorm.cqrs.validation.PropertyValidators;
import eu.eventstorm.cqrs.validation.Validator;
import eu.eventstorm.util.tuple.Tuple2;
import eu.eventstorm.util.tuple.Tuples;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandValidatorGenerator {

	private Logger logger;
	
	private final List<Tuple2<String,PropertyDescriptor>> variables = new ArrayList<Tuple2<String,PropertyDescriptor>>();
	
	public CommandValidatorGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandValidatorGenerator.class);
	}

    public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
    	// generate Implementation class;
        sourceCode.forEachCommand(t -> {
            try {
            	if (CqrsCommandType.CLIENT == t.element().getAnnotation(CqrsCommand.class).type()) {
            		// no validator for client -> skip
            		return;
            	}
            	this.variables.clear();
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }
    
    public void generateEmbedded(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
    	// generate Implementation class;
        sourceCode.forEachEmbeddedCommand(t -> {
            try {
            	this.variables.clear();
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }

    private void generate(ProcessingEnvironment env, AbstractCommandDescriptor descriptor) throws IOException {

    	String fcqn = env.getElementUtils().getPackageOf(descriptor.element()).toString() + ".validator." + descriptor.simpleName() + "Validator" ;
		
    	// check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(fcqn) != null) {
            logger.info("Java SourceCode already exist [" + fcqn + "]");
            return;
        }

    	JavaFileObject object = env.getFiler().createSourceFile(fcqn);
		try (Writer writer = object.openWriter()) {
			writeHeader(writer, env, descriptor);
			writeVariables(writer, descriptor);
			writeConstructor(writer, descriptor);
			writeMethodValidate(writer, descriptor);
			writer.write("}");	
		}
       
    }
	
    private static void writeHeader(Writer writer, ProcessingEnvironment env, AbstractCommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString()+ ".validator");
       
        writeNewLine(writer);
        writer.write("import "+ Validator.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ ConstraintViolation.class.getName() +";");        
        writeNewLine(writer);
        writer.write("import "+ PropertyValidators.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ CommandContext.class.getName() +";");
        writeNewLine(writer);
        writer.write("import "+ ImmutableList.class.getName() +";");
        writeNewLine(writer);
        writer.write("import org.springframework.stereotype.Component;");
        writeNewLine(writer);
        
        writeGenerated(writer,CommandValidatorGenerator.class.getName());
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

    private void writeMethodValidate(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
    	writeNewLine(writer);
        writer.write("    /** {@inheritDoc} */");
        writeNewLine(writer);
        writer.write("    @Override");
        writeNewLine(writer);
        writer.write("    public ImmutableList<ConstraintViolation> validate(CommandContext context, "+ descriptor.fullyQualidiedClassName()+" command) {");
        writeNewLine(writer);
        
        writer.write("        ImmutableList.Builder<ConstraintViolation> builder = ImmutableList.builder();");
        writeNewLine(writer);
        
        for (PropertyDescriptor ppd : descriptor.properties()) {
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
        
        for (PropertyDescriptor ppd : descriptor.properties()) {
            String returnType = Helper.getReturnType(ppd.getter());
            if (returnType.startsWith(List.class.getName())) {
            	String targetClass = returnType.substring(15,returnType.length() - 1);
    			
    			if (Helper.isString(targetClass)) {
    				
    			} else {
    				writeListImtepValidator(writer, descriptor, ppd);	
    			}
            	
            }
        }
        
        writer.write("        return builder.build();");
        writeNewLine(writer);
        writer.write("    }");
    	writeNewLine(writer);

	}

    
	private void writeMethodPartCustomPropertiesValidator(Writer writer, AbstractCommandDescriptor descriptor, CustomPropertiesValidator cpv) throws IOException {
    	writeNewLine(writer);
        writer.write("        // validate properties " + Arrays.toString(cpv.properties()) + " from " + cpv.toString());
    	writeNewLine(writer);
		writer.write("        this.validatorCustomProperties" + Helper.firstToUpperCase(cpv.name()));
    	writer.write(".validate(PROPERTY_" + Helper.toUpperCase(cpv.name()));
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

	private void writeMethodPart(Writer writer, AbstractCommandDescriptor descriptor, PropertyDescriptor ppd, AnnotationMirror am) throws IOException {
       
        if (NotEmpty.class.getName().equals(am.getAnnotationType().asElement().toString())) {
            writeMethodPartNotEmpty(writer, descriptor, ppd, am);
            return;
        }
        
        if (NotNull.class.getName().equals(am.getAnnotationType().asElement().toString())) {
            writeMethodPartNotNull(writer, descriptor, ppd, am);
            return;
        }

        if (CustomPropertyValidator.class.getName().equals(am.getAnnotationType().asElement().toString())) {
            writeMethodPartCustomPropertyValidator(writer, descriptor, ppd, am);
            return;
        }
        
    }

    private void writeMethodPartNotEmpty(Writer writer, AbstractCommandDescriptor descriptor, PropertyDescriptor ppd, AnnotationMirror am) throws IOException {
        writeNewLine(writer);
        writer.write("        // validate property " + ppd.name() + " from " + am.toString());
    	writeNewLine(writer);
    	
    	String type = getReturnType(ppd.getter());
    	if (String.class.getName().equals(type)) {
    		writer.write("        PropertyValidators.notEmpty().validate(PROPERTY_");
    	} else if (type.startsWith(List.class.getName())) {
    		writer.write("        PropertyValidators.listNotEmpty().validate(PROPERTY_");
    	} else {
    		logger.error("@notEmptyNot supported for type [" + type + "]");
    	}
    	
    	writer.write(Helper.toUpperCase(ppd.name())+", ");
        writer.write("command." + ppd.getter().getSimpleName().toString() + "(), builder);");
    	writeNewLine(writer);
    }
    
    private void writeMethodPartNotNull(Writer writer, AbstractCommandDescriptor descriptor, PropertyDescriptor ppd, AnnotationMirror am) throws IOException {
        writeNewLine(writer);
        writer.write("        // validate property " + ppd.name() + " from " + am.toString());
    	writeNewLine(writer);
    	writer.write("        PropertyValidators.notNull().validate(PROPERTY_");
    	writer.write(Helper.toUpperCase(ppd.name())+", ");
        writer.write("command." + ppd.getter().getSimpleName().toString() + "(), builder);");
    	writeNewLine(writer);
    }

    
    private void writeMethodPartCustomPropertyValidator(Writer writer, AbstractCommandDescriptor descriptor, PropertyDescriptor ppd, AnnotationMirror am) throws IOException {
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
    
    private void writeConstructor(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
    	writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName() + "Validator");
        writer.write("(");
        
        for (int i = 0 ; i < variables.size(); i++) {
        	Tuple2<String, PropertyDescriptor> var = variables.get(i);
        	
        	if (Helper.getReturnType(var.getT2().getter()).startsWith(List.class.getName())) {
        		writer.write(var.getT1());
            	writer.write(" $$listValidator" + Helper.firstToUpperCase(var.getT2().name()));
        	} else {
        		writer.write(var.getT1());
            	writer.write(" validatorCustom" + Helper.firstToUpperCase(var.getT2().name()));
            	if (i + 1 < variables.size()) {
            		writer.write(",");
                    writeNewLine(writer);
                    writer.write("                 ");
            	}	
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
        	Tuple2<String, PropertyDescriptor> var = variables.get(i);
        	if (Helper.getReturnType(var.getT2().getter()).startsWith(List.class.getName())) {
            	writer.write("        this.$$listValidator" + Helper.firstToUpperCase(var.getT2().name()));
            	writer.write(" = $$listValidator" + Helper.firstToUpperCase(var.getT2().name()) + ";");
        	} else {
        		writer.write("        this.validatorCustom" + Helper.firstToUpperCase(var.getT2().name()));
            	writer.write(" = validatorCustom" + Helper.firstToUpperCase(var.getT2().name()) + ";");
        	}
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

    private void writeVariables(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
    	
    	// for @CustomPropertyValidator
    	for (PropertyDescriptor ppd : descriptor.properties()) {
    		 for (AnnotationMirror am : ppd.getter().getAnnotationMirrors()) {
                 if (isConstraint(am)) {
         			//writer.write("    private static final ImmutableList<String> PROPERTIES_" + Helper.toUpperCase(ppd.name()) + " = ");
         			//writer.write(" ImmutableList.of(\""+ ppd.name() + "\");");
					 writer.write("    private static final String PROPERTY_" + Helper.toUpperCase(ppd.name()) + " = ");
					 writer.write(" \""+ ppd.name() + "\";");
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

    	for (PropertyDescriptor ppd : descriptor.properties()) {
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
    	
    	//String fcqn = env.getElementUtils().getPackageOf(descriptor.element()).toString() + ".validator." + descriptor.simpleName() + "Validator" ;
		
    	
    	for (PropertyDescriptor ppd : descriptor.properties()) {
    		String returnType = Helper.getReturnType(ppd.getter());
    		if (returnType.startsWith(List.class.getName())) {
    			
    			String targetClass = returnType.substring(15,returnType.length() - 1);
    			
    			if (Helper.isString(targetClass)) {
    				
    			} else {
    				String validatorClassName = returnType.substring(15, returnType.lastIndexOf(".")) + ".validator." + 
        					returnType.substring(returnType.lastIndexOf(".")+1, returnType.length()-1) + "Validator";
        			variables.add(Tuples.of(validatorClassName, ppd));
        			writer.write("    private final " + validatorClassName +" $$listValidator" + Helper.firstToUpperCase(ppd.name()) + ";");
    			}
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
	
    
    private void writeListImtepValidator(Writer writer, AbstractCommandDescriptor descriptor, PropertyDescriptor ppd) throws IOException {
    	writer.write("        if (command." + ppd.getter().getSimpleName().toString() + "() != null) {");
    	writeNewLine(writer);
    	
    	String returnType = Helper.getReturnType(ppd.getter());
    	String targetType = returnType.substring(15, returnType.length() -1); 
    	
    	writer.write("            for (" + targetType + " item : command." + ppd.getter().getSimpleName().toString()+"()) {" );
    	writeNewLine(writer);
    	
    	writer.write("                builder.addAll(this.$$listValidator" + Helper.firstToUpperCase(ppd.name()) +".validate(context, item));");
    	writeNewLine(writer);
    	
    	
    	writer.write("            }");
    	writeNewLine(writer);
    	writer.write("        }");
    	writeNewLine(writer);
	}
    

}