package eu.eventsotrm.core.apt.command;

import static eu.eventsotrm.sql.apt.Helper.writeNewLine;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.SourceCode;
import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.core.apt.model.PropertyDescriptor;
import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsConfiguration;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class CommandOpenApiGenerator {

	private final Logger logger;

	public CommandOpenApiGenerator() {
		logger = LoggerFactory.getInstance().getLogger(CommandOpenApiGenerator.class);
	}

	public void generate(ProcessingEnvironment env, SourceCode sourceCode) {

		// check due to
		// "org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BatchFilerImpl.createSourceFile(BatchFilerImpl.java:149)"
		// if (env.getElementUtils().getTypeElement(cd.fullyQualidiedClassName() +
		// "Builder") != null) {
		// logger.info("Java SourceCode already exist [" +cd.fullyQualidiedClassName() +
		// "Builder" + "]");
		// return;
		// }

		try {
			FileObject object = env.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "api.json");
			Writer writer = object.openWriter();

			writer.write("{");
			writeNewLine(writer);
			writer.write("  \"openapi\": \"3.0.3\",");
			writeNewLine(writer);
			
			addInfo(writer, sourceCode.getCqrsConfiguration());
			addPaths(env, sourceCode, writer);
			
			writer.write("  \"components\": {");
			writeNewLine(writer);
			addSchema(env, sourceCode, writer);
			writeNewLine(writer);
			writer.write("  }");
			
			writeNewLine(writer);
			writer.write("}");
			writer.close();

		} catch (Exception ex) {

		}

	}

	private void addInfo(Writer writer, CqrsConfiguration cqrsConfiguration) throws IOException {
		writer.write("  \"info\": {");
		writeNewLine(writer);
		
		if (cqrsConfiguration == null)  {
			writer.write("  \"title\": \"NOT-DEFINED\",");
			writeNewLine(writer);

			writer.write("  },");
			writeNewLine(writer);	
		}
		

		writer.write("    \"title\": \""+ cqrsConfiguration.openAPIDefinition().info().title()+ "\",");
		writeNewLine(writer);
		writer.write("    \"version\": \""+ cqrsConfiguration.openAPIDefinition().info().version()+ "\"");
		writeNewLine(writer);

		writer.write("  },");
		writeNewLine(writer);	

		
	}

	private void addPaths(ProcessingEnvironment env, SourceCode sourceCode, Writer writer) throws IOException {
		writer.write("  \"paths\": {");
		writeNewLine(writer);
		sourceCode.forEachRestController((name, desc) -> {
			try {
				addPath(env, name, desc, sourceCode, writer);
			} catch (Exception cause) {
				logger.error("Exception for [" + name + "] -> [" + cause.getMessage() + "]", cause);
			}
		});
		writer.write("  },");
		writeNewLine(writer);
	}


	private void addPath(ProcessingEnvironment env, String name, ImmutableList<RestControllerDescriptor> desc, SourceCode sourceCode, Writer writer) throws IOException {
		desc.forEach(rcd -> {
			try {
				writer.write("    \"/"+ rcd.getRestController().uri() +"\": {");
				writeNewLine(writer);
				
				writer.write("      \""+ rcd.getRestController().method().toString().toLowerCase() +"\": {");
				writeNewLine(writer);
				
				writer.write("        \"requestBody\": {");
				writeNewLine(writer);
				writer.write("          \"content\": {");
				writeNewLine(writer);
				writer.write("            \"application/json\": {");
				writeNewLine(writer);
				writer.write("              \"schema\": {");
				writeNewLine(writer);
				writer.write("                \"$ref\": \"#/components/schemas/"+ rcd.element().getSimpleName().toString() + "\",");
				writer.write("              }");
				writeNewLine(writer);
				writer.write("            }");
				writeNewLine(writer);
				writer.write("          }");
				writeNewLine(writer);
				
				writer.write("        },");
				writeNewLine(writer);
				
				writer.write("        \"responses\": {");
				writeNewLine(writer);
				writer.write("          \"200\": {");
				writeNewLine(writer);
				writer.write("            \"description\": \"TODO ...\",");
				writeNewLine(writer);
				writer.write("            \"content\": {");
				writeNewLine(writer); 
				writer.write("              \"application/cloudevents+json\": {");
				writeNewLine(writer);
				writer.write("                \"schema\": {");
				writeNewLine(writer);
				writer.write("                  \"$ref\": \"#/components/schemas/CloudEvent\"");
				writer.write("                }");
				writeNewLine(writer);
				writer.write("              }");
				writeNewLine(writer);
				writeNewLine(writer);
				writer.write("            }");
				writeNewLine(writer);
				writer.write("          }");
				writeNewLine(writer);
				writer.write("        }");
				writeNewLine(writer);
			
				
				writer.write("      }");
				writeNewLine(writer);
				
				writer.write("    }");
				writeNewLine(writer);
			} catch (Exception cause) {
				logger.error("Exception for [" + name + "] -> [" + cause.getMessage() + "]", cause);
			}
		});
	}

	private void addSchema(ProcessingEnvironment env, SourceCode sourceCode, Writer writer) throws IOException {
		writer.write("    \"schemas\": {");
		sourceCode.forEachCommand(t -> {
			try {
				addSchema(env, writer, t);
			} catch (Exception cause) {
				logger.error("Exception for [" + t + "] -> [" + cause.getMessage() + "]", cause);
			}
		});
//		sourceCode.forEachEvent(e -> {
//			try {
//				addSchema(env, writer, e);
//			} catch (Exception cause) {
//				logger.error("Exception for [" + e + "] -> [" + cause.getMessage() + "]", cause);
//			}
//		});
		addSchemaCloudEvents(env, writer);
		
		writer.write("    }");
	}

	private void addSchemaCloudEvents(ProcessingEnvironment env, Writer writer) throws IOException {
		writeNewLine(writer);
		writer.write("      \"CloudEvent\": {");
		writeNewLine(writer);
		writer.write("        \"type\": \"object\",");
		writeNewLine(writer);
		writer.write("        \"properties\": {");
		writeNewLine(writer);
		addSchemaProperty(writer, 5, "id", "string", false);
		addSchemaProperty(writer, 5, "source", "string", false);
		addSchemaProperty(writer, 5, "specVersion", "string", false);
		addSchemaProperty(writer, 5, "type", "string", false);
		addSchemaProperty(writer, 5, "dataContentType", "string", false);
		addSchemaProperty(writer, 5, "dataSchema", "string", false);
		addSchemaProperty(writer, 5, "subject", "string", false);
		addSchemaProperty(writer, 5, "time", "string", false, ImmutableMap.of("format","rfc3339"));
		addSchemaProperty(writer, 5, "data", "object", true);
		writer.write("        }");
		writeNewLine(writer);
		writer.write("      }");
		writeNewLine(writer);
		
	}
	
	private void addSchemaProperty(Writer writer, int tab, String name, String type, boolean isLast) throws IOException {
		addSchemaProperty(writer, tab, name, type, isLast, ImmutableMap.of());
	}
	private void addSchemaProperty(Writer writer, int tab, String name, String type, boolean isLast, ImmutableMap<String, String> params) throws IOException {
		for (int i = 0 ; i < tab ; i++) {
			writer.write("  ");
		}
		writer.write("\""+ name +"\": {");
		writeNewLine(writer);
		writer.write("            \"type\": \""+ type +"\"");
		if (!params.isEmpty()) {
//			writeNewLine(writer);
//			for (int i = 0 ; i < tab ; i++) {
//				writer.write("  ");
//			}
//			writer.write("}");
//		} else {
//			
//		}
		
		writer.write(",");
		writeNewLine(writer);
		int j = 0;
		for (Map.Entry<String, String> entry : params.entrySet()) {
			for (int i = 0 ; i < tab + 1 ; i++) {
				writer.write("  ");
			}
			writer.write("\""+ entry.getKey()+"\": \""+ entry.getValue()+"\"");
			j++;
			if (j < params.size()) {
				writer.write(",");
				writeNewLine(writer);
			}
			
		}}
		writeNewLine(writer);
		for (int i = 0 ; i < tab ; i++) {
			writer.write("  ");
		}
		writer.write("}");
		if (!isLast) {
			writer.write(",");
		}
		writeNewLine(writer);
	}
	
	private void addSchema(ProcessingEnvironment env, Writer writer, CommandDescriptor cd) throws IOException {
		writeNewLine(writer);
		writer.write("      \""+ cd.simpleName() +"\": {");
		writeNewLine(writer);
		writer.write("        \"type\": \"object\",");
		writeNewLine(writer);
		writer.write("        \"properties\": {");
		writeNewLine(writer);
		
		
		for (int i = 0; i < cd.properties().size() ; i++) {
			PropertyDescriptor cpd = cd.properties().get(i);
			writer.write("          \""+ cpd.name() +"\": {");
			writeNewLine(writer);
			writer.write("            \"type\": ");
			
			if ("java.lang.String".equals(cpd.getter().getReturnType().toString())) {
				writer.write("\"string\"");
			} else if ("int".equals(cpd.getter().getReturnType().toString()) || "java.lang.Integer".equals(cpd.getter().getReturnType().toString())) {
				writer.write("\"integer\",");
				writeNewLine(writer);
				writer.write("            \"format\": \"int32\"");
			}
			
			writeNewLine(writer);
			writer.write("          }");
			if (i + 1 < cd.properties().size()) {
				writer.write(",");
			}
			writeNewLine(writer);
		}
		
		writer.write("        }");
		writeNewLine(writer);
		writer.write("      },");
		writeNewLine(writer);
	} 

	private void addSchema(ProcessingEnvironment env, Writer writer, EventDescriptor ed) throws IOException {
		writeNewLine(writer);
		writer.write("      \""+ ed.simpleName() +"\": {");
		writeNewLine(writer);
		writer.write("        \"type\": \"object\",");
		writeNewLine(writer);
		writer.write("        \"properties\": {");
		writeNewLine(writer);
		
		
		for (int i = 0; i < ed.properties().size() ; i++) {
			PropertyDescriptor epd = ed.properties().get(i);
			writer.write("          \""+ epd.name() +"\": {");
			writeNewLine(writer);
			writer.write("            \"type\": ");
			
			if ("java.lang.String".equals(epd.getter().getReturnType().toString())) {
				writer.write("\"string\"");
			} else if ("int".equals(epd.getter().getReturnType().toString()) || "java.lang.Integer".equals(epd.getter().getReturnType().toString())) {
				writer.write("\"integer\",");
				writeNewLine(writer);
				writer.write("            \"format\": \"int32\"");
			}
			
			writeNewLine(writer);
			writer.write("          }");
			if (i + 1 < ed.properties().size()) {
				writer.write(",");
			}
			writeNewLine(writer);
		}
		
		writer.write("        }");
		writeNewLine(writer);
		writer.write("      },");
		writeNewLine(writer);
	}
}