package eu.eventsotrm.core.apt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import eu.eventsotrm.core.apt.analyser.CqrsCommandAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsEventAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsRestControllerAnalyser;
import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.core.annotation.CqrsCommand;
import eu.eventstorm.core.annotation.CqrsEventPayload;
import eu.eventstorm.core.annotation.CqrsCommandRestController;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "eu.eventstorm.core.annotation.CqrsCommand", "eu.eventstorm.core.annotation.CqrsEvent" })
public class EventProcessor extends AbstractProcessor {

	private ProcessingEnvironment processingEnvironment;

	private boolean firstTime = false;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.processingEnvironment = processingEnv;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		if (firstTime) {
			this.processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "SqlProcessor done");
			return false;
		}

		LoggerFactory.getInstance().init(processingEnv, "eu.eventstorm.report","event-output.txt");
		Logger logger = LoggerFactory.getInstance().getLogger(EventProcessor.class);
		try {
			logger.info("EventProcessor start");

			doProcess(roundEnv);

			logger.info("EventProcessor end");
			this.firstTime = true;
		} catch (Exception cause) {
			logger.error("EventProcessor start", cause);
		} finally {
			LoggerFactory.getInstance().close();
		}

		return true;

	}

	private void doProcess(RoundEnvironment roundEnvironment) {

		List<CommandDescriptor> descriptors = roundEnvironment.getElementsAnnotatedWith(CqrsCommand.class).stream().map(new CqrsCommandAnalyser())
		        .collect(Collectors.toList());
		
		List<RestControllerDescriptor> restControllerDescriptors = roundEnvironment.getElementsAnnotatedWith(CqrsCommandRestController.class).stream().map(new CqrsRestControllerAnalyser())
		        .collect(Collectors.toList());

		List<EventDescriptor> eventDescriptors = roundEnvironment.getElementsAnnotatedWith(CqrsEventPayload.class).stream().map(new CqrsEventAnalyser())
		        .collect(Collectors.toList());

		
		SourceCode sourceCode = new SourceCode(this.processingEnv, descriptors, eventDescriptors, restControllerDescriptors);

		sourceCode.dump();

		new CommandImplementationGenerator().generate(this.processingEnv, sourceCode);
		new CommandFactoryGenerator().generate(this.processingEnv, sourceCode);
		new CommandJacksonStdDeserializerGenerator().generate(processingEnv, sourceCode);
		new CommandJacksonModuleGenerator().generate(processingEnv, sourceCode);
		
		new EventImplementationGenerator().generate(this.processingEnv, sourceCode);
		new EventFactoryGenerator().generate(this.processingEnv, sourceCode);

		new DomainModelHandlerImplementationGenerator().generate(this.processingEnv, sourceCode);
		new RestControllerImplementationGenerator().generate(processingEnvironment, sourceCode);

	}

}