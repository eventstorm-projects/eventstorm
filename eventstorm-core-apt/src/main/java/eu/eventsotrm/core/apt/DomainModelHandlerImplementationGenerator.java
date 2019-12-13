package eu.eventsotrm.core.apt;

import static eu.eventsotrm.sql.apt.Helper.writeGenerated;
import static eu.eventsotrm.sql.apt.Helper.writeNewLine;
import static eu.eventsotrm.sql.apt.Helper.writePackage;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.JavaFileObject;

import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.Event;
import eu.eventstorm.core.EventListener;
import eu.eventstorm.core.EventPayload;
import eu.eventstorm.core.annotation.CqrsEventPayload;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
final class DomainModelHandlerImplementationGenerator {

	private final Logger logger;

	DomainModelHandlerImplementationGenerator() {
		logger = LoggerFactory.getInstance().getLogger(DomainModelHandlerImplementationGenerator.class);
	}

	public void generate(ProcessingEnvironment processingEnvironment, SourceCode sourceCode) {

		Map<DeclaredType, List<EventDescriptor>> map = new HashMap<>();

		sourceCode.forEachEvent(event -> {
			CqrsEventPayload eventData = event.element().getAnnotation(CqrsEventPayload.class);

			DeclaredType domainModel = getDomain(eventData);
			List<EventDescriptor> descs = map.get(domainModel);
			if (descs == null) {
				descs = new ArrayList<>();
				map.put(domainModel, descs);
			}
			descs.add(event);
		});

		map.forEach((domain, events) -> {
			try {
				generate(processingEnvironment, domain, events);
			} catch (Exception cause) {
				logger.error("Exception for [" + domain + "] -> [" + cause.getMessage() + "]", cause);
			}
		});

	}

	private void generate(ProcessingEnvironment env, DeclaredType domainModel, List<EventDescriptor> events) throws IOException {

		JavaFileObject object = env.getFiler().createSourceFile(getName(domainModel.asElement().toString()));
		try (Writer writer = object.openWriter()) {

			writeHeader(writer, env, domainModel, events);
			// writeConstructor(writer, descriptor);
			// writeVariables(writer, descriptor);
			writeMethods(writer, events);
			writer.write("}");
		}

	}

	private static void writeHeader(Writer writer, ProcessingEnvironment env, DeclaredType domainModel, List<EventDescriptor> events) throws IOException {

		writePackage(writer, env.getElementUtils().getPackageOf(domainModel.asElement()).toString());

		writer.write("import ");
		writer.write(Event.class.getName());
		writer.write(";");
		writeNewLine(writer);
		writer.write("import ");
		writer.write(EventPayload.class.getName());
		writer.write(";");
		writeNewLine(writer);
		writer.write("import ");
		writer.write(EventListener.class.getName());
		writer.write(";");
		writeNewLine(writer);
		for (EventDescriptor ed : events) {
			writer.write("import ");
			writer.write(ed.fullyQualidiedClassName());
			writer.write(";");
			writeNewLine(writer);
		}

		writeGenerated(writer, DomainModelHandlerImplementationGenerator.class.getName());

		writer.write("public abstract class ");
		writer.write(getName(domainModel.asElement().getSimpleName().toString()));
		writer.write(" implements ");
		writer.write(EventListener.class.getSimpleName());
		writer.write(" {");
		writeNewLine(writer);
	}

	private static void writeMethods(Writer writer, List<EventDescriptor> events) throws IOException {
		writeNewLine(writer);
		writer.write("    @SuppressWarnings(\"unchecked\")");
		writeNewLine(writer);
		writer.write("    public final void accept(Event<? extends EventPayload> event) {");
		writeNewLine(writer);

		writeNewLine(writer);
		writer.write("        Class<?> clazz = event.getPayload().getClass();");
		writeNewLine(writer);
		writeNewLine(writer);

		for (EventDescriptor event : events) {

			writer.write("        if (");
			writer.write(event.simpleName());
			writer.write(".class.isAssignableFrom(clazz)) {");
			writeNewLine(writer);
			writer.write("            on");
			writer.write(event.simpleName());
			writer.write("((Event<" + event.simpleName() + ">) event);");
			writeNewLine(writer);
			writer.write("            return;");
			writeNewLine(writer);
			writer.write("        }");
			writeNewLine(writer);
		}
		writer.write("    }");
		writeNewLine(writer);
		writeNewLine(writer);

		for (EventDescriptor event : events) {

			writer.write("    protected abstract void on");
			writer.write(event.simpleName());
			writer.write("(Event<" + event.simpleName() + "> event); ");

			writeNewLine(writer);
			writeNewLine(writer);
		}

	}

	private static String getName(String value) {

		if (value.endsWith("DomainModel")) {
			return value + "Handler";
		}

		if (value.endsWith("Domain")) {
			return value + "ModelHandler";
		}

		return value + "DomainModelHandler";
	}

	private static DeclaredType getDomain(CqrsEventPayload eventData) {
		try {
			eventData.domain(); // this should throw
		} catch (MirroredTypeException mte) {
			return (DeclaredType) mte.getTypeMirror();
		}
		return null; // can this ever happen ??
	}

}