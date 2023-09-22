package eu.eventstorm.core.apt;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import eu.eventstorm.annotation.CqrsConfiguration;
import eu.eventstorm.core.apt.model.AbstractCommandDescriptor;
import eu.eventstorm.core.apt.model.CommandDescriptor;
import eu.eventstorm.core.apt.model.DatabaseTableQueryDescriptor;
import eu.eventstorm.core.apt.model.DatabaseViewQueryDescriptor;
import eu.eventstorm.core.apt.model.Descriptor;
import eu.eventstorm.core.apt.model.ElsQueryDescriptor;
import eu.eventstorm.core.apt.model.EmbeddedCommandDescriptor;
import eu.eventstorm.core.apt.model.EventEvolutionDescriptor;
import eu.eventstorm.core.apt.model.PojoQueryDescriptor;
import eu.eventstorm.core.apt.model.QueryClientDescriptor;
import eu.eventstorm.core.apt.model.QueryClientServiceDescriptor;
import eu.eventstorm.core.apt.model.QueryDescriptor;
import eu.eventstorm.core.apt.model.RestControllerDescriptor;
import eu.eventstorm.core.apt.model.SagaCommandDescriptor;
import eu.eventstorm.sql.apt.log.Logger;

import javax.annotation.processing.ProcessingEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

public final class SourceCode {

    private final CqrsConfiguration cqrsConfiguration;

    private final ImmutableMap<String, CommandDescriptor> commands;
    private final ImmutableMap<String, SagaCommandDescriptor> sagaCommands;
    private final ImmutableMap<String, EmbeddedCommandDescriptor> embeddedCommands;

    private final ImmutableMap<String, ImmutableList<AbstractCommandDescriptor>> packages;
    private final ImmutableMap<String, ImmutableList<EmbeddedCommandDescriptor>> embeddedCommandsPackages;

    private final ImmutableMap<String, ImmutableList<AbstractCommandDescriptor>> allCommandsPackages;

    private final ImmutableList<EventEvolutionDescriptor> eventEvolutionDescriptors;

    private final Map<String, ImmutableList<RestControllerDescriptor>> restControllers;

    private final ImmutableMap<String, ElsQueryDescriptor> queriesElasticSearch;
    private final ImmutableMap<String, DatabaseViewQueryDescriptor> queriesDatabaseView;
    private final ImmutableMap<String, DatabaseTableQueryDescriptor> queriesDatabaseTable;
    private final ImmutableMap<String, PojoQueryDescriptor> queriesPojo;

    private final ImmutableMap<String, ImmutableList<DatabaseViewQueryDescriptor>> queriesDatabaseViewPackages;
    private final ImmutableMap<String, ImmutableList<DatabaseTableQueryDescriptor>> queriesDatabaseTablePackages;
    private final ImmutableMap<String, ImmutableList<PojoQueryDescriptor>> queriesPojoPackages;
    private final ImmutableMap<String, ImmutableList<QueryDescriptor>> queriesPackages;

    private final ImmutableMap<String, QueryClientDescriptor> clientQueries;
    private final ImmutableMap<String, ImmutableList<QueryClientDescriptor>> clientQueriesPackages;

    private final ImmutableMap<String, QueryClientServiceDescriptor> clientServiceDescriptors;


    SourceCode(ProcessingEnvironment env, CqrsConfiguration cqrsConfiguration,
               List<CommandDescriptor> commands,
               List<SagaCommandDescriptor> sagaCommands,
               List<EmbeddedCommandDescriptor> embeddedCommands,
               List<EventEvolutionDescriptor> eventEvolutionDescriptors,
               List<RestControllerDescriptor> restControllerDescriptors,
               List<ElsQueryDescriptor> queriesElasticSearch,
               List<DatabaseViewQueryDescriptor> queriesDatabase,
               List<DatabaseTableQueryDescriptor> queriesTableDatabase,
               List<PojoQueryDescriptor> queriesPojo,
               List<QueryClientDescriptor> clientQueries,
               List<QueryClientServiceDescriptor> cqrsQueryClientServices) {

        this.cqrsConfiguration = cqrsConfiguration;
        this.commands = commands.stream().collect(toImmutableMap(CommandDescriptor::fullyQualidiedClassName, identity()));
        this.sagaCommands = sagaCommands.stream().collect(toImmutableMap(SagaCommandDescriptor::fullyQualidiedClassName, identity()));
        this.embeddedCommands = embeddedCommands.stream().collect(toImmutableMap(EmbeddedCommandDescriptor::fullyQualidiedClassName, identity()));
        this.packages = mapByPackage(env, ImmutableMap.<String, AbstractCommandDescriptor>builder()
                .putAll(this.commands)
                .putAll(this.sagaCommands)
                .build());

        this.embeddedCommandsPackages = mapByPackage(env, this.embeddedCommands);

        this.allCommandsPackages = mapByPackage(env, ImmutableMap.<String, AbstractCommandDescriptor>builder()
                .putAll(this.commands)
                .putAll(this.embeddedCommands)
                .putAll(this.sagaCommands)
                .build());

        this.eventEvolutionDescriptors = ImmutableList.copyOf(eventEvolutionDescriptors);

        this.restControllers = restControllerDescriptors.stream()
                .collect(groupingBy(t -> t.getFCQN(env), mapping(identity(), toImmutableList())));

        this.queriesElasticSearch = queriesElasticSearch.stream().collect(toImmutableMap(ElsQueryDescriptor::fullyQualidiedClassName, identity()));
        this.queriesDatabaseView = queriesDatabase.stream().collect(toImmutableMap(DatabaseViewQueryDescriptor::fullyQualidiedClassName, identity()));
        this.queriesDatabaseTable = queriesTableDatabase.stream().collect(toImmutableMap(DatabaseTableQueryDescriptor::fullyQualidiedClassName, identity()));
        this.queriesDatabaseViewPackages = mapByPackage(env, this.queriesDatabaseView);
        this.queriesDatabaseTablePackages = mapByPackage(env, this.queriesDatabaseTable);

        this.queriesPojo = queriesPojo.stream().collect(toImmutableMap(PojoQueryDescriptor::fullyQualidiedClassName, identity()));
        this.queriesPojoPackages = mapByPackage(env, this.queriesPojo);

        this.clientQueries = clientQueries.stream().collect(toImmutableMap(QueryClientDescriptor::fullyQualidiedClassName, identity()));
        this.clientQueriesPackages = mapByPackage(env, this.clientQueries);

        ImmutableList.Builder<QueryDescriptor> builder = ImmutableList.<QueryDescriptor>builder();
        builder.addAll(queriesDatabase);
        builder.addAll(queriesTableDatabase);
        builder.addAll(queriesPojo);
        builder.addAll(clientQueries);
        this.queriesPackages = mapByPackage(env, builder.build().stream().collect(toImmutableMap(QueryDescriptor::fullyQualidiedClassName, identity())));

        this.clientServiceDescriptors = cqrsQueryClientServices.stream().collect(toImmutableMap(QueryClientServiceDescriptor::fullyQualifiedClassName, identity()));
    }

    public void forEachCommand(Consumer<CommandDescriptor> consumer) {
        this.commands.values().forEach(consumer);
    }

    public void forEachSagaCommand(Consumer<SagaCommandDescriptor> consumer) {
        this.sagaCommands.values().forEach(consumer);
    }

    public CommandDescriptor getCommandDescriptor(String fqcn) {
        return this.commands.get(fqcn);
    }

    public void forEachEmbeddedCommand(Consumer<EmbeddedCommandDescriptor> consumer) {
        this.embeddedCommands.values().forEach(consumer);
    }

    public EmbeddedCommandDescriptor getEmbeddedCommandDescriptor(String fqcn) {
        return this.embeddedCommands.get(fqcn);
    }

    public void forEachCommandPackage(BiConsumer<String, ImmutableList<AbstractCommandDescriptor>> consumer) {
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
        this.queriesDatabaseView.values().forEach(consumer);
        this.queriesPojo.values().forEach(consumer);
    }

    public void forEachElasticSearchQuery(Consumer<ElsQueryDescriptor> consumer) {
        this.queriesElasticSearch.values().forEach(consumer);
    }

    public void forEachDatabaseViewQuery(Consumer<DatabaseViewQueryDescriptor> consumer) {
        this.queriesDatabaseView.values().forEach(consumer);
    }

    public void forEachDatabaseTableQuery(Consumer<DatabaseTableQueryDescriptor> consumer) {
        this.queriesDatabaseTable.values().forEach(consumer);
    }

    public void forEachQueryClient(Consumer<QueryClientDescriptor> consumer) {
        this.clientQueries.values().forEach(consumer);
    }

    public void forEachQueryClientService(Consumer<QueryClientServiceDescriptor> consumer) {
        this.clientServiceDescriptors.values().forEach(consumer);
    }

    public void forEachDatabaseViewQueryPackage(BiConsumer<String, ImmutableList<DatabaseViewQueryDescriptor>> consumer) {
        this.queriesDatabaseViewPackages.forEach(consumer);
    }

    public void forEachDatabaseTableQueryPackage(BiConsumer<String, ImmutableList<DatabaseTableQueryDescriptor>> consumer) {
        this.queriesDatabaseTablePackages.forEach(consumer);
    }

    public void forEachPojoQueryPackage(BiConsumer<String, ImmutableList<PojoQueryDescriptor>> consumer) {
        this.queriesPojoPackages.forEach(consumer);
    }

    public void forEachQueryPackage(BiConsumer<String, ImmutableList<QueryDescriptor>> consumer) {
        this.queriesPackages.forEach(consumer);
    }

    public void forEachQueryClientPackage(BiConsumer<String, ImmutableList<QueryClientDescriptor>> consumer) {
        this.clientQueriesPackages.forEach(consumer);
    }

    public void forEachRestController(BiConsumer<String, ImmutableList<RestControllerDescriptor>> consumer) {
        this.restControllers.forEach(consumer);
    }

    public CqrsConfiguration getCqrsConfiguration() {
        Logger.getMainLogger().info("getCqrsConfiguration -------> " + cqrsConfiguration);
        if (cqrsConfiguration == null) {
            throw new IllegalStateException("Missing @CqrsConfiguration");
        }
        return cqrsConfiguration;
    }

    void dump() {
        Logger logger = Logger.getMainLogger();
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
        logger.info("Number of Query Database View found : " + queriesDatabaseView.size());
        queriesDatabaseView.values().forEach(desc -> {
            logger.info("\t-> " + desc);
        });
        logger.info("---------------------------------------------------------------------------------------------------------");
        logger.info("Number of Query Database Table found : " + queriesDatabaseTable.size());
        queriesDatabaseTable.values().forEach(desc -> {
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