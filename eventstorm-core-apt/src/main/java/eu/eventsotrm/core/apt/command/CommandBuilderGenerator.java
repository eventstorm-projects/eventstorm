package eu.eventsotrm.core.apt.command;

import static eu.eventsotrm.sql.apt.Helper.getReturnType;
import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.AbstractCommandDescriptor;
import eu.eventsotrm.core.apt.model.EmbeddedCommandDescriptor;
import eu.eventsotrm.core.apt.model.PropertyDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.util.Strings;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandBuilderGenerator {

	private final Logger logger;
	
	private ProcessingEnvironment env; 
	private SourceCode code;
	
	private final Map<String, String> holders;

	public CommandBuilderGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandBuilderGenerator.class);
		this.holders = new HashMap<>();
	}

    public void generateCommand(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
    	this.env = processingEnvironment;
    	this.code = sourceCode;
        sourceCode.forEachCommand(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }
    
    public void generateEmbeddedCommand(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {
    	this.env = processingEnvironment;
    	this.code = sourceCode;
        sourceCode.forEachEmbeddedCommand(t -> {
            try {
                generate(processingEnvironment, t);
            } catch (Exception cause) {
            	logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
            }
        });
    }
    
    private void generate(ProcessingEnvironment env, AbstractCommandDescriptor cd) throws IOException {

        // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
        if (env.getElementUtils().getTypeElement(cd.fullyQualidiedClassName() + "Builder") != null) {
            logger.info("Java SourceCode already exist [" +cd.fullyQualidiedClassName() + "Builder" + "]");
            return;
        }
        
        JavaFileObject object = env.getFiler().createSourceFile(cd.fullyQualidiedClassName() + "Builder");
        Writer writer = object.openWriter();
        writeHeader(writer, env, cd);
        writeConstructor(writer, cd);
        writeVariables(writer, cd, cd.fullyQualidiedClassName() + "Builder");
       
        writeMethods(writer, cd);

        writer.write("}");
        writer.close();
        
    }


    private static void writeHeader(Writer writer, ProcessingEnvironment env, AbstractCommandDescriptor descriptor) throws IOException {

        writePackage(writer, env.getElementUtils().getPackageOf(descriptor.element()).toString());
        writeGenerated(writer,CommandBuilderGenerator.class.getName());

        writer.write("public final class ");
        writer.write(descriptor.simpleName() + "Builder");
        
        writer.write(" {");
        writeNewLine(writer);
    }

    private static void writeConstructor(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writer.write("    public ");
        writer.write(descriptor.simpleName());
        writer.write("Builder() {");
        writeNewLine(writer);	
        writer.write("    }");
        writeNewLine(writer);
    }

    private void writeVariables(Writer writer,  AbstractCommandDescriptor cd, String parent) throws IOException {
    	writeNewLine(writer);
    	for (PropertyDescriptor ppd : cd.properties()) {
    		
            writer.write("    private ");
            
            String returnType = getReturnType(ppd.getter());
            String classname = null;
            
            if (returnType.startsWith("java.util.List")) {
            	String target =  returnType.substring(15, returnType.length()-1);
            	classname =  cd.simpleName() + "__" + target.substring(target.lastIndexOf('.') + 1) + "__Builder<"+ parent + ">";
            	writer.write(classname);
            } else {
                writer.write(getReturnType(ppd.getter()));            	
            }

            writer.write(" ");
            writer.write(ppd.variable());
            writer.write("$$");
            
            if (classname != null) {
            	writer.write(" = new " + classname + "(this);");
            } else {
                writer.write(";");            	
            }
            
            
            writeNewLine(writer);
        }
    }

    private void writeMethods(Writer writer, AbstractCommandDescriptor descriptor) throws IOException {
        writeNewLine(writer);
        writeMethod(writer, descriptor);
    	writeBuildMethod(writer, descriptor);
    }

	private static void writeBuildMethod(Writer writer, AbstractCommandDescriptor ed) throws IOException {
		writeNewLine(writer);
        writer.write("    public ");
        writer.write(ed.simpleName());
        writer.write(' ');
        writer.write("build() {");
        writeNewLine(writer);
        
        if (ed instanceof EmbeddedCommandDescriptor) {
        	writer.write("        return new " + ed.fullyQualidiedClassName() + "Impl(");
        	writeNewLine(writer);
        } else {
        	writer.write("        return CommandFactory.new" + ed.simpleName() + '(');
            writeNewLine(writer);
        }
        for (int i = 0; i < ed.properties().size(); i++) {
            writer.write("            " + ed.properties().get(i).variable() + "$$");
            if (getReturnType(ed.properties().get(i).getter()).startsWith("java.util.List")) {
            	writer.write(".build()");
            }
            if (i+1 < ed.properties().size()) {
            	writer.write(",");
            }
            writeNewLine(writer);
        }
        writer.write("            );");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
	}

	private void writeMethod(Writer writer, AbstractCommandDescriptor cd) throws IOException {
        for (PropertyDescriptor ppd : cd.properties()) {
            writeMethod(writer, cd, ppd, cd.simpleName() + "Builder");
        }
    }

    private void writeMethod(Writer writer,AbstractCommandDescriptor cd, PropertyDescriptor cpd, String returnType) throws IOException {
    	
        writeNewLine(writer);
        writer.write("    public ");
        String type = getReturnType(cpd.getter());

        if (type.startsWith("java.util.List")) {
        	
        	String newBuilder = genereteJoinBuilder(cd, cpd, type.substring(15, type.length()-1));
        	
        	writer.write(newBuilder);
        	writer.write("<" + returnType);
        	writer.write("> with");
            writer.write(Helper.firstToUpperCase(cpd.name()));
        	writer.write("() {");
        	writeNewLine(writer);
        	writer.write("        return " +  cpd.name() + "$$;");
        	
        } else {
            writer.write(returnType);
            writer.write(" with");
            writer.write(Helper.firstToUpperCase(cpd.name()));
            writer.write("(");
        	writer.write(type);
	    	writer.write(' ');
	        writer.write(cpd.variable());
	        writer.write(") {");
	        writeNewLine(writer);
	        writer.write("        this." + cpd.variable() + "$$ = " + cpd.variable() + ";");
	        writeNewLine(writer);
	        writer.write("        return this;");
        }
        
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
       
    }
    
	
	private String genereteJoinBuilder(AbstractCommandDescriptor cd, PropertyDescriptor cpd, String target)
			throws IOException {

		String name = cd.fullyQualidiedClassName() + "__" + target.substring(target.lastIndexOf('.') + 1) + "__Builder";
		String classname = holders.get(name);
		
		if (!Strings.isEmpty(classname)) {
			return  classname + "<" + cd.fullyQualidiedClassName() + "Builder>";
		}

		classname =  cd.simpleName() + "__" + target.substring(target.lastIndexOf('.') + 1) + "__Builder";
		
		JavaFileObject object = env.getFiler().createSourceFile(name);
		try (Writer writer = object.openWriter()) {
			writePackage(writer, env.getElementUtils().getPackageOf(cd.element()).toString());
			writeGenerated(writer, CommandBuilderGenerator.class.getName());
			writer.write("public final class " + classname + "<T> {");
			writeNewLine(writer);
			writeNewLine(writer);
	        writer.write("    private final T parent;");
	        writeNewLine(writer);
	
	        // constructor
	        writer.write("    " + classname + "(T parent) {");
	        writeNewLine(writer);
	        writer.write("        this.parent = parent;");
	        writeNewLine(writer);
	        writer.write("    }");
	        writeNewLine(writer);
		
			EmbeddedCommandDescriptor ecd = this.code.getEmbeddedCommandDescriptor(target);
			// variables
			writeVariables(writer, ecd, classname + "<T>");
			writer.write("    private final java.util.List<" + target + "> $$list$$ = new java.util.ArrayList<>();");
	   	    writeNewLine(writer);
			
			for (PropertyDescriptor ppd : ecd.properties()) {
				
				writeNewLine(writer);
		        
		        String type = getReturnType(ppd.getter());

		        if (type.startsWith("java.util.List")) {
		        	
		        	// skip ...
			   
		        } else {
		        	writer.write("    public ");
		            writer.write(classname + "<T>");
		            writer.write(" with");
		            writer.write(Helper.firstToUpperCase(ppd.name()));
		            writer.write("(");
		        	writer.write(type);
			    	writer.write(' ');
			        writer.write(ppd.variable());
			        writer.write(") {");
			        writeNewLine(writer);
			        writer.write("        this." + ppd.variable() + "$$ = " + ppd.variable() + ";");
			        writeNewLine(writer);
			        writer.write("        return this;");
			        writeNewLine(writer);
			        writer.write("    }");
			        writeNewLine(writer);
		        }	
				
		    }
			writeEmbeddedMethods(writer, cd, classname, ecd, target);
			
			writer.write("}");
		}
		
		
		this.holders.put(name, classname);

		return classname; // + "<" + cd.simpleName() + "Builder>";
	}
	
	private static void writeEmbeddedMethods(Writer writer, AbstractCommandDescriptor cd, String classname, EmbeddedCommandDescriptor ecd, String target) throws IOException {
		writeNewLine(writer);
        writer.write("    public java.util.List<" + ecd.simpleName()  +"> build() { ");
        writeNewLine(writer);
        writer.write("        return this.$$list$$;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
        
		writeNewLine(writer);
        writer.write("    public T parent() { ");
        writeNewLine(writer);
        writer.write("        and();");
        writeNewLine(writer);
        writer.write("        return parent;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
        
    	writeNewLine(writer);
        writer.write("    public " + classname  +"<T> and() { ");
        writeNewLine(writer);
        writer.write("        this.$$list$$.add( new " + target + "Impl(");
        writeNewLine(writer);
        for (int i = 0; i < ecd.properties().size(); i++) {
            writer.write("            " + ecd.properties().get(i).variable() + "$$");
            if (getReturnType(ecd.properties().get(i).getter()).startsWith("java.util.List")) {
            	writer.write(".build()");
            }
            if (i+1 < ecd.properties().size()) {
            	writer.write(",");
            }
            writeNewLine(writer);
        }
        writer.write("            ));");
        writeNewLine(writer);
        writer.write("        return this;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
        
        writeNewLine(writer);
        writer.write("    public " + classname  +"<T> and(" + ecd.fullyQualidiedClassName() + " item) { ");
        writeNewLine(writer);
        writer.write("        this.$$list$$.add(item);");
        writeNewLine(writer);
        writer.write("        return this;");
        writeNewLine(writer);
        writer.write("    }");
        writeNewLine(writer);
        
	}
    
}