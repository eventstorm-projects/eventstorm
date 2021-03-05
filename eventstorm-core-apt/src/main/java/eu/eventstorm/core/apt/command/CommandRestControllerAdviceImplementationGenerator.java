package eu.eventstorm.core.apt.command;

import static eu.eventstorm.sql.apt.Helper.writeGenerated;
import static eu.eventstorm.sql.apt.Helper.writeNewLine;
import static eu.eventstorm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import eu.eventstorm.core.apt.SourceCode;
import eu.eventstorm.core.apt.model.CommandDescriptor;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.log.LoggerFactory;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandRestControllerAdviceImplementationGenerator {

	private final Logger logger;

	public CommandRestControllerAdviceImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandRestControllerAdviceImplementationGenerator.class);
	}

	public void generate(ProcessingEnvironment env, SourceCode sourceCode) {
	    
	    String packagename = findBestCommonPackage(sourceCode);

	    if (packagename == null) {
	    	//no command -> skip
			logger.info("no command -> skip CommandRestControllerAdvice");
			return;
		}
	    
		try {
		    
		    //CommandRestControllerAdvice
		    
		    // check due to "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
	        if (env.getElementUtils().getTypeElement(packagename+ ".CommandRestControllerAdvice") != null) {
	            logger.info("Java SourceCode already exist [" + packagename + ".CommandRestControllerAdvice]");
	            return;
	        }



	        
	        JavaFileObject object = env.getFiler().createSourceFile(packagename + ".CommandRestControllerAdvice");
	        try (Writer writer = object.openWriter()) {

	            writeHeader(writer, env, packagename, sourceCode);
	            writeMethods(writer, sourceCode);
	            writer.write("}");
	        }
			
		} catch (Exception cause) {
			logger.error("Exception for [" + packagename + ".CommandRestControllerAdvice] -> [" + cause.getMessage() + "]", cause);
		}

	}

    private void writeHeader(Writer writer, ProcessingEnvironment env, String packagename, SourceCode sourceCode) throws IOException {

        writePackage(writer, packagename);
        writeNewLine(writer);

        
        writer.write("import org.springframework.http.server.reactive.ServerHttpRequest;");
        writeNewLine(writer);
        writer.write("import eu.eventstorm.problem.Problem;");
        writeNewLine(writer);
        
        writer.write("import org.springframework.http.HttpHeaders;");
        writeNewLine(writer);
        writer.write("import org.springframework.http.MediaType;");
        writeNewLine(writer);
        writer.write("import org.springframework.http.ResponseEntity;");
        writeNewLine(writer);

        writer.write("import org.springframework.web.bind.annotation.ExceptionHandler;");
        writeNewLine(writer);
        writer.write("import org.springframework.web.bind.annotation.RestControllerAdvice;");
        writeNewLine(writer);
		
        writeGenerated(writer, CommandRestControllerAdviceImplementationGenerator.class.getName());
		writer.write("@RestControllerAdvice");
		writeNewLine(writer);
		writer.write("public final class CommandRestControllerAdvice {");
		writeNewLine(writer);
		writeNewLine(writer);
	}



	private void writeMethods(Writer writer, SourceCode sourceCode) throws IOException {

	    sourceCode.forEachCommand(cd -> {
	        try {
                writeMethodOn(writer, cd);
            } catch (IOException cause) {
                logger.error("Exception for [CommandRestControllerAdvice] -> [" + cause.getMessage() + "] -> command [" + cd + "]", cause);
            }
	    });
	    
	}

	private static void writeMethodOn(Writer writer, CommandDescriptor cd) throws IOException {
	    writer.write("    @ExceptionHandler(" + cd.element().toString()+ "Exception.class)");
	    writeNewLine(writer);
		writer.write("    public ResponseEntity<Problem> on(");
		writer.write(cd.element().toString());
		writer.write("Exception  ex, ServerHttpRequest request) { ");
		writeNewLine(writer);
		writer.write("        return ResponseEntity.badRequest()");
		writeNewLine(writer);
		writer.write("                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)");
        writeNewLine(writer);
        writer.write("                .body(Problem.builder()");
        writeNewLine(writer);
        writer.write("                        .withTitle(\""+ cd.simpleName() +"Exception\")");
        writeNewLine(writer);
        writer.write("                        .withDetail(ex.getMessage())");
        writeNewLine(writer);
        writer.write("                        .withReactiveRequest(request)");
        writeNewLine(writer);
        writer.write("                        .withStatus(400)");
        writeNewLine(writer);
        writer.write("                        .build());");
        writeNewLine(writer);
		
		writer.write("    }");
		writeNewLine(writer);
		
	}

	private static String findBestCommonPackage(SourceCode sourceCode) {
        List<String> temp = new ArrayList<>();
        sourceCode.forEachCommandPackage((pack, list) -> {
            temp.add(pack);
        });
        
        if (temp.size() == 0) {
            return null;
        }
        
        if (temp.size() == 1) {
            return temp.get(0);
        }
        
        String common = findCommon( temp.get(0),  temp.get(1)); 
        
        if (temp.size() == 2) {
            return common;
        }
        
        for (int i =2 ; i < temp.size() ; i++) {
            common = findCommon(common, temp.get(i));
        }
        
        return common;
    }
	
	private static String findCommon(String one, String two) {
	    int index = 0;
	    for (int i = 0, n = Math.min(one.length(), two.length()) ; i < n ; i++) {
	        if (one.charAt(i) != two.charAt(index)) {
	            return one.substring(0, index);
	        } 
	        
	        if (one.charAt(i) == '.') {
	            index = i;
	        }
	        
	    }
	    return "";
	}
	
}