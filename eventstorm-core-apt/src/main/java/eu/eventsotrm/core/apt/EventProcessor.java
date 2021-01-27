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

import eu.eventsotrm.core.apt.analyser.*;
import eu.eventsotrm.core.apt.command.CommandBuilderGenerator;
import eu.eventsotrm.core.apt.command.CommandExceptionGenerator;
import eu.eventsotrm.core.apt.command.CommandFactoryGenerator;
import eu.eventsotrm.core.apt.command.CommandImplementationGenerator;
import eu.eventsotrm.core.apt.command.CommandJacksonModuleGenerator;
import eu.eventsotrm.core.apt.command.CommandJacksonStdDeserializerGenerator;
import eu.eventsotrm.core.apt.command.CommandJacksonStdSerializerGenerator;
import eu.eventsotrm.core.apt.command.CommandOpenApiGenerator;
import eu.eventsotrm.core.apt.command.CommandRestControllerAdviceImplementationGenerator;
import eu.eventsotrm.core.apt.command.CommandRestControllerImplementationGenerator;
import eu.eventsotrm.core.apt.command.CommandValidatorGenerator;
import eu.eventsotrm.core.apt.event.EventProtoGenerator;
import eu.eventsotrm.core.apt.model.*;
import eu.eventsotrm.core.apt.query.QueryBuilderGenerator;
import eu.eventsotrm.core.apt.query.SqlPageRequestDescriptorGenerator;
import eu.eventsotrm.core.apt.query.PageQueryDescriptorsGenerator;
import eu.eventsotrm.core.apt.query.QueryImplementationGenerator;
import eu.eventsotrm.core.apt.query.QueryJacksonModuleGenerator;
import eu.eventsotrm.core.apt.query.QueryJacksonStdSerializerGenerator;
import eu.eventsotrm.core.apt.query.client.QueryClientGeneratorFacade;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseDescriptorGenerator;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseMapperFactoryGenerator;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseMapperGenerator;
import eu.eventsotrm.core.apt.query.db.QueryDatabaseModuleGenerator;
import eu.eventsotrm.core.apt.query.els.ElasticIndexDefinitionGenerator;
import eu.eventsotrm.core.apt.spring.SpringConfigurationGenerator;
import eu.eventsotrm.sql.apt.Helper;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.*;
import eu.eventstorm.sql.annotation.Table;

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
		
		List<DatabaseViewQueryDescriptor> queriesDatabase  = roundEnvironment.getElementsAnnotatedWith(CqrsQueryDatabaseView.class).stream().map(new CqrsQueryDatabaseViewAnalyser())
                .collect(Collectors.toList());

		List<DatabaseTableQueryDescriptor> queriesDatabaseTable  = roundEnvironment.getElementsAnnotatedWith(Table.class)
				.stream()
				.filter(el -> el.getAnnotation(CqrsQuery.class) != null)
				.map(new CqrsQueryDatabaseTableAnalyser())
				.collect(Collectors.toList());
		
		List<PojoQueryDescriptor> queriesPojo  = roundEnvironment.getElementsAnnotatedWith(CqrsQueryPojo.class).stream().map(new CqrsQueryPojoAnalyser())
                .collect(Collectors.toList());
		
		
		List<QueryClientDescriptor> clientQueries  = roundEnvironment.getElementsAnnotatedWith(CqrsQueryClient.class).stream().map(new CqrsQueryClientAnalyser())
                .collect(Collectors.toList());

		


		SourceCode sourceCode = new SourceCode(this.processingEnv, 
				cqrsConfiguration, 
				commandDescriptors, 
				embeddedCommandDescriptors,
				eventEvolutionDescriptors,
			//	eventDescriptors,
				restControllerDescriptors,
				queries, 
				queriesDatabase,
				queriesDatabaseTable,
				queriesPojo, 
				clientQueries);

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
		

		new CommandRestControllerAdviceImplementationGenerator().generate(processingEnv, sourceCode);
		
		new CommandOpenApiGenerator().generate(processingEnv, sourceCode);
		
		// EVENTS .....
		new EventProtoGenerator().generate(processingEnv, sourceCode);
		//new EventStreamGenerator().generate(processingEnv, sourceCode);
		

		new CommandRestControllerImplementationGenerator().generate(processingEnv, sourceCode);
        
		//	Query 
		new QueryImplementationGenerator().generate(this.processingEnv, sourceCode);
		new QueryBuilderGenerator().generate(processingEnv, sourceCode);
//		
		//  Quere / ELS	
		new ElasticIndexDefinitionGenerator().generate(processingEnv, sourceCode);
		
		// Query / Database View and Table
		new QueryDatabaseDescriptorGenerator().generate(processingEnv, sourceCode);
		new QueryDatabaseMapperFactoryGenerator().generate(processingEnv, sourceCode);
		new QueryDatabaseMapperGenerator().generate(processingEnv, sourceCode);
		new QueryDatabaseModuleGenerator().generate(processingEnv, sourceCode);
		
		// Query / Database View and Table / Pojo
		new QueryJacksonStdSerializerGenerator().generate(processingEnv, sourceCode);
		new QueryJacksonModuleGenerator().generate(processingEnv, sourceCode);
		new SqlPageRequestDescriptorGenerator().generate(processingEnv, sourceCode);
		new PageQueryDescriptorsGenerator().generate(processingEnv, sourceCode);
		
		// Query Client
		QueryClientGeneratorFacade.generate(processingEnv, sourceCode);

		new SpringConfigurationGenerator().generateCommand(processingEnv, sourceCode);
	}

}