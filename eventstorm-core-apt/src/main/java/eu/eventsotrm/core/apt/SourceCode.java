package eu.eventsotrm.core.apt;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.processing.ProcessingEnvironment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.model.AbstractCommandDescriptor;
import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.DatabaseQueryDescriptor;
import eu.eventsotrm.core.apt.model.Descriptor;
import eu.eventsotrm.core.apt.model.ElsQueryDescriptor;
import eu.eventsotrm.core.apt.model.EmbeddedCommandDescriptor;
import eu.eventsotrm.core.apt.model.EventEvolutionDescriptor;
import eu.eventsotrm.core.apt.model.QueryDescriptor;
import eu.eventsotrm.core.apt.model.RestControllerDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventstorm.annotation.CqrsConfiguration;

public final class SourceCode {

	private final Logger logger = LoggerFactory.getInstance().getLogger(SourceCode.class);

	private final CqrsConfiguration cqrsConfiguration;
	
	private final ImmutableMap<String, CommandDescriptor> commands;
	private final ImmutableMap<String, EmbeddedCommandDescriptor> embeddedCommands;

	private final ImmutableMap<String, ImmutableList<CommandDescriptor>> packages;
	private final ImmutableMap<String, ImmutableList<EmbeddedCommandDescriptor>> embeddedCommandsPackages;

	private final ImmutableMap<String, ImmutableList<AbstractCommandDescriptor>> allCommandsPackages;
	
	private final ImmutableList<EventEvolutionDescriptor> eventEvolutionDescriptors;
	
	private final Map<String, ImmutableList<RestControllerDescriptor>> restControllers;
	
	private final ImmutableMap<String, ElsQueryDescriptor> queriesElasticSearch;
	private final ImmutableMap<String, DatabaseQueryDescriptor> queriesDatabase;
	private final ImmutableMap<String, ImmutableList<DatabaseQueryDescriptor>> queriesDatabasePackages;
	
	SourceCode(ProcessingEnvironment env, CqrsConfiguration cqrsConfiguration,
			List<CommandDescriptor> commands,
			List<EmbeddedCommandDescriptor> embeddedCommands,
			List<EventEvolutionDescriptor> eventEvolutionDescriptors,
	//		List<EventDescriptor> events,
	        List<RestControllerDescriptor> restControllerDescriptors, 
	        List<ElsQueryDescriptor> queriesElasticSearch,
	        List<DatabaseQueryDescriptor> queriesDatabase) {
		this.cqrsConfiguration = cqrsConfiguration;
		this.commands = commands.stream().collect(toImmutableMap(CommandDescriptor::fullyQualidiedClassName, identity()));
		this.embeddedCommands = embeddedCommands.stream().collect(toImmutableMap(EmbeddedCommandDescriptor::fullyQualidiedClassName, identity()));
		this.packages = mapByPackage(env, this.commands);
		this.embeddedCommandsPackages = mapByPackage(env, this.embeddedCommands);
		
		this.allCommandsPackages = mapByPackage(env, ImmutableMap.<String,AbstractCommandDescriptor>builder().putAll(this.commands).putAll(this.embeddedCommands).build());
		
		
		this.eventEvolutionDescriptors = ImmutableList.copyOf(eventEvolutionDescriptors);
		
	//	this.events = events.stream().collect(toImmutableMap(EventDescriptor::fullyQualidiedClassName, identity()));
//		this.eventpackages = mapByPackage(env, this.events);
		this.restControllers = restControllerDescriptors.stream()
		        .collect(groupingBy( t -> t.getFCQN(env), mapping(identity(), toImmutableList())));
		
		this.queriesElasticSearch = queriesElasticSearch.stream().collect(toImmutableMap(ElsQueryDescriptor::fullyQualidiedClassName, identity()));
		this.queriesDatabase = queriesDatabase.stream().collect(toImmutableMap(DatabaseQueryDescriptor::fullyQualidiedClassName, identity()));
		this.queriesDatabasePackages = mapByPackage(env, this.queriesDatabase);
		//this.queryPackages = mapByPackage(env, this.queries);
	}

	public void forEachCommand(Consumer<CommandDescriptor> consumer) {
		this.commands.values().forEach(consumer);
	}
	
	public void forEachEmbeddedCommand(Consumer<EmbeddedCommandDescriptor> consumer) {
		this.embeddedCommands.values().forEach(consumer);
	}
	
	public EmbeddedCommandDescriptor getEmbeddedCommandDescriptor(String fqcn) {
		return this.embeddedCommands.get(fqcn);
	}
	
	public void forEachCommandPackage(BiConsumer<String, ImmutableList<CommandDescriptor>> consumer) {
		this.packages.forEach(consumer);
	}
	
	public void forEachEmbeddedCommandPackage(BiConsumer<String, ImmutableList<EmbeddedCommandDescriptor>> consumer) {
		this.embeddedCommandsPackages.forEach(consumer);
	}
	
	public void forEachAllCommandPackage(BiConsumer<String, ImmutableList<AbstractCommandDescriptor>> consumer) {
		this.allCommandsPackages.forEach(consumer);
	}
	
	public void forEventEvolution(Consumer<EventEvolutionDescriptor> consumer) {
    	this.eventEvolutionDescriptors.forEach(consumer);
    }
	
	public void forEachQuery(Consumer<QueryDescriptor> consumer) {
        this.queriesElasticSearch.values().forEach(consumer);
        this.queriesDatabase.values().forEach(consumer);
    }
	
    public void forEachElasticSearchQuery(Consumer<ElsQueryDescriptor> consumer) {
        this.queriesElasticSearch.values().forEach(consumer);
    }
    
    public void forEachDatabaseQuery(Consumer<DatabaseQueryDescriptor> consumer) {
        this.queriesDatabase.values().forEach(consumer);
    }
    
    public void forEachDatabaseQueryPackage(BiConsumer<String, ImmutableList<DatabaseQueryDescriptor>> consumer) {
        this.queriesDatabasePackages.forEach(consumer);
    }

//	public void forEachEvent(Consumer<EventDescriptor> consumer) {
//		this.events.values().forEach(consumer);
//	}
//
//	public void forEachEventPackage(BiConsumer<String, ImmutableList<EventDescriptor>> consumer) {
//		this.eventpackages.forEach(consumer);
//	}
//	
//	public void forEachQueryPackage(BiConsumer<String, ImmutableList<ElsQueryDescriptor>> consumer) {
//		this.queryPackages.forEach(consumer);
//	}

	public void forEachRestController(BiConsumer<String, ImmutableList<RestControllerDescriptor>> consumer) {
		this.restControllers.forEach(consumer);
	}
	
	public CqrsConfiguration getCqrsConfiguration() {
		logger.info("getCqrsConfiguration -------> " + cqrsConfiguration);
		if (cqrsConfiguration == null) {
			throw new IllegalStateException("Missing @CqrsConfiguration");
		}
		return cqrsConfiguration;
	}
	
	void dump() {
		logger.info("Result Analysis -----------------------------------------------------------------------------------------");
		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("Number of command(s) found : " + commands.size());
		commands.values().forEach(desc -> {
			logger.info("\t-> " + desc);
		});
		logger.info("Number of embedded command(s) found : " + embeddedCommands.size());
		embeddedCommands.values().forEach(desc -> {
			logger.info("\t-> " + desc);
		});
		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("Number of event(s) evolution found : " + eventEvolutionDescriptors.size());
		eventEvolutionDescriptors.forEach(desc -> {
			logger.info("\t-> " + desc);
		});
		logger.info("---------------------------------------------------------------------------------------------------------");
		
//		logger.info("Number of event(s) found : " + events.size());
//		events.values().forEach(desc -> {
//			logger.info("\t-> " + desc);
//		});
//		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("---------------------------------------------------------------------------------------------------------");
        logger.info("Number of Query ElasticSeach found : " + queriesElasticSearch.size());
        queriesElasticSearch.values().forEach(desc -> {
            logger.info("\t-> " + desc);
        });
        logger.info("---------------------------------------------------------------------------------------------------------");
        logger.info("Number of Query Database found : " + queriesDatabase.size());
        queriesDatabase.values().forEach(desc -> {
            logger.info("\t-> " + desc);
        });
        logger.info("---------------------------------------------------------------------------------------------------------");
	}
	
	private <T extends Descriptor> ImmutableMap<String, ImmutableList<T>> mapByPackage(ProcessingEnvironment env, ImmutableMap<String, T> map) {

		Map<String, List<T>> temp = new HashMap<>();

		map.values().forEach(desc -> {
            String pack = env.getElementUtils().getPackageOf(desc.element()).toString();
            if (pack.startsWith("package")) {
                // with eclipse compiler
                pack = pack.substring(7).trim();
            }
			List<T> list = temp.get(pack);
			if (list == null) {
				list = new ArrayList<>();
				temp.put(pack, list);
			}
			list.add(desc);
		});

		ImmutableMap.Builder<String, ImmutableList<T>> builder = ImmutableMap.builder();
		temp.forEach((key, value) -> {
			builder.put(key, ImmutableList.copyOf(value));
		});
		return builder.build();
	}

}