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
import javax.tools.Diagnostic;

import eu.eventsotrm.core.apt.analyser.CqrsCommandAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsEmbeddedCommandAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsQueryDatabaseAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsQueryElasticSearchAnalyser;
import eu.eventsotrm.core.apt.analyser.CqrsRestControllerAnalyser;
import eu.eventsotrm.core.apt.analyser.EventEvolutionAnalyser;
import eu.eventsotrm.core.apt.command.CommandBuilderGenerator;
import eu.eventsotrm.core.apt.command.CommandExceptionGenerator;
import eu.eventsotrm.core.apt.command.CommandFactoryGenerator;
import eu.eventsotrm.core.apt.command.CommandImplementationGenerator;
import eu.eventsotrm.core.apt.command.CommandJacksonModuleGenerator;
import eu.eventsotrm.core.apt.command.CommandJacksonStdDeserializerGenerator;
import eu.eventsotrm.core.apt.command.CommandJacksonStdSerializerGenerator;
import eu.eventsotrm.core.apt.command.CommandOpenApiGenerator;
import eu.eventsotrm.core.apt.command.CommandRestControllerImplementationGenerator;
import eu.eventsotrm.core.apt.command.CommandValidatorGenerator;
import eu.eventsotrm.core.apt.event.EventProtoGenerator;
import eu.eventsotrm.core.apt.event.EventStreamGenerator;
import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.DatabaseQueryDescriptor;
import eu.eventsotrm.core.apt.model.ElsQueryDescriptor;
import eu.eventsotrm.core.apt.model.EmbeddedCommandDescriptor;
import eu.eventsotrm.core.apt.model.EventEvolutionDescriptor;
import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.core.apt.query.QueryBuilderGenerator;
import eu.eventsotrm.core.apt.query.QueryDescriptorGenerator;
import eu.eventsotrm.core.apt.query.QueryDescriptorsGenerator;
import eu.eventsotrm.core.apt.query.QueryImplementationGenerator;
import eu.eventsotrm.core.apt.query.QueryJacksonModuleGenerator;
import eu.eventsotrm.core.apt.query.QueryJacksonStdSerializerGenerator;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseDescriptorGenerator;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseMapperFactoryGenerator;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseMapperGenerator;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseModuleGenerator;
import eu.eventsotrm.core.apt.query.els.ElasticIndexDefinitionGenerator;
import eu.eventsotrm.core.apt.spring.SpringConfigurationGenerator;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsCommand;
import eu.eventstorm.annotation.CqrsCommandRestController;
import eu.eventstorm.annotation.CqrsConfiguration;
import eu.eventstorm.annotation.CqrsEmbeddedCommand;
import eu.eventstorm.annotation.CqrsQueryDatabaseView;
import eu.eventstorm.annotation.CqrsQueryElsIndex;
import eu.eventstorm.annotation.EventEvolution;

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
		
		if (roundEnv.getClass().getName().startsWith("org.eclipse.jdt.internal.compiler.apt.")) {
			logger.info("Disable internal eclipse apt ... [" + roundEnv + "]");
			LoggerFactory.getInstance().close();
			return true;
		}
		
		Helper.setTypes(this.processingEnv.getTypeUtils());
		
		try {
			logger.info("EventProcessor start");

			doProcess(logger, roundEnv);

			logger.info("EventProcessor end");
			this.firstTime = true;
		} catch (Throwable cause) {
			logger.error("EventProcessor end with Excaption", cause);
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

		List<CommandDescriptor> commandDescriptors= roundEnvironment.getElementsAnnotatedWith(CqrsCommand.class).stream().map(new CqrsCommandAnalyser())
		        .collect(Collectors.toList());
		
		List<EmbeddedCommandDescriptor> embeddedCommandDescriptors = roundEnvironment.getElementsAnnotatedWith(CqrsEmbeddedCommand.class).stream().map(new CqrsEmbeddedCommandAnalyser())
		        .collect(Collectors.toList());
		
		List<RestControllerDescriptor> restControllerDescriptors = roundEnvironment.getElementsAnnotatedWith(CqrsCommandRestController.class).stream().map(new CqrsRestControllerAnalyser())
		        .collect(Collectors.toList());
		
		List<EventEvolutionDescriptor> eventEvolutionDescriptors = roundEnvironment.getElementsAnnotatedWith(EventEvolution.class).stream().map(new EventEvolutionAnalyser())
		        .collect(Collectors.toList());

//		List<EventDescriptor> eventDescriptors = roundEnvironment.getElementsAnnotatedWith(CqrsEventPayload.class).stream().map(new CqrsEventAnalyser())
//		        .collect(Collectors.toList());
		
		List<ElsQueryDescriptor> queries = roundEnvironment.getElementsAnnotatedWith(CqrsQueryElsIndex.class).stream().map(new CqrsQueryElasticSearchAnalyser())
                .collect(Collectors.toList());
		
		List<DatabaseQueryDescriptor> queriesDatabase  = roundEnvironment.getElementsAnnotatedWith(CqrsQueryDatabaseView.class).stream().map(new CqrsQueryDatabaseAnalyser())
                .collect(Collectors.toList());
		
//		queries.addAll(roundEnvironment.getElementsAnnotatedWith(CqrsQueryDatabaseView.class).stream().map(new CqrsQueryAnalyser())
//		        .collect(Collectors.toList()));
		
//		for (QueryDescriptor qd : queries) {
//		    for (TypeMirror tm : ((TypeElement)qd.element()).getInterfaces()) {
//		        String fcqn = tm.toString();
//		        logger.info("add from [" + fcqn + "]");
//		        for (QueryDescriptor temp : queries) {
//		            if (temp.fullyQualidiedClassName().equals(fcqn)) {
//		                logger.info("found -> ");
//		                qd.properties().addAll(temp.properties());
//		                break;
//		            }
//		        }
//		    }
//		    
//        }

		SourceCode sourceCode = new SourceCode(this.processingEnv, 
				cqrsConfiguration, 
				commandDescriptors, 
				embeddedCommandDescriptors,
				eventEvolutionDescriptors,
			//	eventDescriptors,
				restControllerDescriptors,
				queries, 
				queriesDatabase);

		sourceCode.dump();

		new CommandImplementationGenerator().generateCommand(this.processingEnv, sourceCode);
		new CommandImplementationGenerator().generateEmbeddedCommand(this.processingEnv, sourceCode);
		
		CommandBuilderGenerator commandBuilderGenerator = new CommandBuilderGenerator();
		commandBuilderGenerator.generateCommand(this.processingEnv, sourceCode);
		commandBuilderGenerator.generateEmbeddedCommand(this.processingEnv, sourceCode);
		
		new CommandFactoryGenerator().generate(this.processingEnv, sourceCode);
		
		new CommandJacksonStdDeserializerGenerator().generate(processingEnv, sourceCode);
		new CommandJacksonStdDeserializerGenerator().generateEmbedded(processingEnv, sourceCode);
		
		new CommandJacksonStdSerializerGenerator().generate(processingEnv, sourceCode);
		new CommandJacksonStdSerializerGenerator().generateEmbedded(processingEnv, sourceCode);

		new CommandJacksonModuleGenerator().generate(processingEnv, sourceCode);
		new CommandExceptionGenerator().generate(processingEnv, sourceCode);
		
		new CommandValidatorGenerator().generateEmbedded(processingEnv, sourceCode);
		new CommandValidatorGenerator().generate(processingEnv, sourceCode);
		

		// TODO => move to webflux
		//new CommandRestControllerAdviceImplementationGenerator().generate(processingEnv, sourceCode);
		
		new CommandOpenApiGenerator().generate(processingEnv, sourceCode);
		
		// EVENTS .....
		new EventProtoGenerator().generate(processingEnv, sourceCode);
		new EventStreamGenerator().generate(processingEnv, sourceCode);
		

//		new EventPayloadImplementationGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadBuilderGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadFactoryGenerator().generate(this.processingEnv, sourceCode);
//		
//		new EventPayloadJacksonStdDeserializerGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadJacksonStdSerializerGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadJacksonModuleGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadDeserializerGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadSerializerGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadDeserializersGenerator().generate(this.processingEnv, sourceCode);
//		new EventPayloadSerializersGenerator().generate(this.processingEnv, sourceCode);
//
//		new DomainModelHandlerImplementationGenerator().generate(this.processingEnv, sourceCode);
		new CommandRestControllerImplementationGenerator().generate(processingEnv, sourceCode);
        
		//	Query / ELS	
		new QueryImplementationGenerator().generate(this.processingEnv, sourceCode);
		new QueryBuilderGenerator().generate(processingEnv, sourceCode);
//		
		new ElasticIndexDefinitionGenerator().generate(processingEnv, sourceCode);
		
		// Query / Database
		new QueryDatabaseDescriptorGenerator().generate(processingEnv, sourceCode);
		new QueryDatabaseMapperFactoryGenerator().generate(processingEnv, sourceCode);
		new QueryDatabaseMapperGenerator().generate(processingEnv, sourceCode);
		new QueryDatabaseModuleGenerator().generate(processingEnv, sourceCode);
		
		new QueryJacksonStdSerializerGenerator().generate(processingEnv, sourceCode);
		new QueryJacksonModuleGenerator().generate(processingEnv, sourceCode);
		new QueryDescriptorGenerator().generate(processingEnv, sourceCode);
		new QueryDescriptorsGenerator().generate(processingEnv, sourceCode);
		
//		sourceCode.forEachQuery(queryDescriptor -> {
//		    CqrsQueryDatabaseView query =  queryDescriptor.element().getAnnotation(CqrsQueryDatabaseView.class);
//		    if (query != null) {
//		    	new QueryDatabaseDescriptorGenerator().generate(processingEnv, queryDescriptor);
//		    	new QueryDatabaseMapperGenerator().generate(processingEnv, queryDescriptor);
//		    }
//		});
//		new QueryDatabaseMapperFactoryGenerator().generate(processingEnv, sourceCode);
//		new QueryDatabaseModuleGenerator().generate(processingEnv, sourceCode);

		new SpringConfigurationGenerator().generateCommand(processingEnv, sourceCode);
	}

}