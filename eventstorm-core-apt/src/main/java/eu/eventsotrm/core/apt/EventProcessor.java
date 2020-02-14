package eu.eventsotrm.core.apt;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import eu.eventsotrm.core.apt.analyser.CqrsCommandAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsEventAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsQueryAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsRestControllerAnalyser;
import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.core.apt.model.QueryDescriptor;
import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsCommand;
import eu.eventstorm.annotation.CqrsCommandRestController;
import eu.eventstorm.annotation.CqrsConfiguration;
import eu.eventstorm.annotation.CqrsEventPayload;
import eu.eventstorm.annotation.CqrsQuery;
import eu.eventstorm.annotation.CqrsQueryDatabaseView;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({ "eu.eventstorm.annotation.CqrsCommand", "eu.eventstorm.annotation.CqrsEvent" })
public class EventProcessor extends AbstractProcessor {

	private boolean firstTime = false;
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		if (firstTime) {
			this.processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "EventProcessor done");
			return false;
		}

		LoggerFactory.getInstance().init(processingEnv, "eu.eventstorm.report","event-output.txt");
		Logger logger = LoggerFactory.getInstance().getLogger(EventProcessor.class);
		
		Helper.setTypes(this.processingEnv.getTypeUtils());
		
		try {
			logger.info("EventProcessor start");

			doProcess(logger, roundEnv);

			logger.info("EventProcessor end");
			this.firstTime = true;
		} catch (Exception cause) {
			logger.error("EventProcessor start", cause);
		} finally {
			LoggerFactory.getInstance().close();
		}

		return true;

	}

    private void doProcess(Logger logger, RoundEnvironment roundEnvironment) {
		
		Set<? extends Element> configs = roundEnvironment.getElementsAnnotatedWith(CqrsConfiguration.class);
		CqrsConfiguration cqrsConfiguration= null;
		if (configs.size() > 1) {
			logger.error("more than 1 @CqrsConfiguration (Max 1)");
			for (Element el : configs) {
				logger.error("\t" + el.toString());
				return;
			}
		} else if (configs.size() == 1) {
			cqrsConfiguration = configs.iterator().next().getAnnotation(CqrsConfiguration.class);
		}

		List<CommandDescriptor> descriptors = roundEnvironment.getElementsAnnotatedWith(CqrsCommand.class).stream().map(new CqrsCommandAnalyser())
		        .collect(Collectors.toList());
		
		List<RestControllerDescriptor> restControllerDescriptors = roundEnvironment.getElementsAnnotatedWith(CqrsCommandRestController.class).stream().map(new CqrsRestControllerAnalyser())
		        .collect(Collectors.toList());

		List<EventDescriptor> eventDescriptors = roundEnvironment.getElementsAnnotatedWith(CqrsEventPayload.class).stream().map(new CqrsEventAnalyser())
		        .collect(Collectors.toList());
		
		List<QueryDescriptor> queries = roundEnvironment.getElementsAnnotatedWith(CqrsQuery.class).stream().map(new CqrsQueryAnalyser())
                .collect(Collectors.toList());
		
		queries.addAll(roundEnvironment.getElementsAnnotatedWith(CqrsQueryDatabaseView.class).stream().map(new CqrsQueryAnalyser())
		        .collect(Collectors.toList()));
		
		for (QueryDescriptor qd : queries) {
		    for (TypeMirror tm : ((TypeElement)qd.element()).getInterfaces()) {
		        String fcqn = tm.toString();
		        logger.info("add from [" + fcqn + "]");
		        for (QueryDescriptor temp : queries) {
		            if (temp.fullyQualidiedClassName().equals(fcqn)) {
		                logger.info("found -> ");
		                qd.properties().addAll(temp.properties());
		                break;
		            }
		        }
		    }
		    
        }

		SourceCode sourceCode = new SourceCode(this.processingEnv, cqrsConfiguration, descriptors, eventDescriptors, restControllerDescriptors, queries);

		sourceCode.dump();

		new CommandImplementationGenerator().generate(this.processingEnv, sourceCode);
		new CommandFactoryGenerator().generate(this.processingEnv, sourceCode);
		new CommandBuilderGenerator().generate(this.processingEnv, sourceCode);
		new CommandJacksonStdDeserializerGenerator().generate(processingEnv, sourceCode);
		new CommandJacksonModuleGenerator().generate(processingEnv, sourceCode);
		new CommandExceptionGenerator().generate(processingEnv, sourceCode);
		new CommandRestControllerAdviceImplementationGenerator().generate(processingEnv, sourceCode);
		new CommandValidatorGenerator().generate(processingEnv, sourceCode);
		
		new EventPayloadImplementationGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadBuilderGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadFactoryGenerator().generate(this.processingEnv, sourceCode);
		
		new EventPayloadJacksonStdDeserializerGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadJacksonStdSerializerGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadJacksonModuleGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadDeserializerGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadSerializerGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadDeserializersGenerator().generate(this.processingEnv, sourceCode);
		new EventPayloadSerializersGenerator().generate(this.processingEnv, sourceCode);

		new DomainModelHandlerImplementationGenerator().generate(this.processingEnv, sourceCode);
		new RestControllerImplementationGenerator().generate(processingEnv, sourceCode);
		
		new QueryImplementationGenerator().generate(this.processingEnv, sourceCode);
		new QueryBuilderGenerator().generate(processingEnv, sourceCode);
		
		sourceCode.forEachQuery(queryDescriptor -> {
		    CqrsQueryDatabaseView query =  queryDescriptor.element().getAnnotation(CqrsQueryDatabaseView.class);
		    if (query != null) {
		    	new QueryDatabaseDescriptorGenerator().generate(processingEnv, queryDescriptor);
		    	new QueryDatabaseMapperGenerator().generate(processingEnv, queryDescriptor);
		    }
		});
		new QueryDatabaseMapperFactoryGenerator().generate(processingEnv, sourceCode);
		new QueryDatabaseModuleGenerator().generate(processingEnv, sourceCode);

	}

}