package eu.eventsotrm.core.apt;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.processing.ProcessingEnvironment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.core.apt.model.CommandDescriptor;
import eu.eventsotrm.core.apt.model.EventDescriptor;
import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;

public final class SourceCode {

	private final Logger logger = LoggerFactory.getInstance().getLogger(SourceCode.class);

	private final ImmutableMap<String, CommandDescriptor> commands;

	private final ImmutableMap<String, ImmutableList<CommandDescriptor>> packages;

	private final ImmutableMap<String,EventDescriptor> events;
	
	SourceCode(ProcessingEnvironment env, List<CommandDescriptor> commands, List<EventDescriptor> events) {
		this.commands = commands.stream().collect(toImmutableMap(CommandDescriptor::fullyQualidiedClassName, desc -> desc));
		this.events = events.stream().collect(toImmutableMap(EventDescriptor::fullyQualidiedClassName, de -> de));
		this.packages = mapByPackage(env);
	}

	public void forEachCommand(Consumer<CommandDescriptor> consumer) {
		this.commands.values().forEach(consumer);
	}
	
	public void forEachEvent(Consumer<EventDescriptor> consumer) {
		this.events.values().forEach(consumer);
	}
	
	public void forEachCommandPackage(BiConsumer<String, ImmutableList<CommandDescriptor>> consumer) {
		this.packages.forEach(consumer);
	}

	void dump() {
		logger.info("Result Analysis -----------------------------------------------------------------------------------------");
		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("Number of command(s) found : " + commands.size());
		commands.values().forEach(desc -> {
			logger.info("\t-> " + desc);
		});
		logger.info("---------------------------------------------------------------------------------------------------------");
		logger.info("Number of events(s) found : " + events.size());
		events.values().forEach(desc -> {
			logger.info("\t-> " + desc);
		});
		logger.info("---------------------------------------------------------------------------------------------------------");
	}


	private ImmutableMap<String, ImmutableList<CommandDescriptor>> mapByPackage(ProcessingEnvironment env) {

		Map<String, List<CommandDescriptor>> map = new HashMap<>();

		this.commands.values().forEach(desc -> {
			String pack = env.getElementUtils().getPackageOf(desc.element()).toString();
			List<CommandDescriptor> list = map.get(pack);
			if (list == null) {
				list = new ArrayList<>();
				map.put(pack, list);
			}
			list.add(desc);
		});

		ImmutableMap.Builder<String, ImmutableList<CommandDescriptor>> builder = ImmutableMap.builder();
		map.forEach((key, value) -> {
			builder.put(key, ImmutableList.copyOf(value));
		});
		return builder.build();
	}
}