package eu.eventsotrm.sql.apt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.processing.ProcessingEnvironment;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import eu.eventsotrm.sql.apt.log.Logger;
import eu.eventsotrm.sql.apt.log.LoggerFactory;
import eu.eventsotrm.sql.apt.model.Desc;
import eu.eventsotrm.sql.apt.model.PojoDescriptor;
import eu.eventsotrm.sql.apt.model.ViewDescriptor;

public final class SourceCode {

    private final Logger logger = LoggerFactory.getInstance().getLogger(SourceCode.class);

    private final ImmutableList<PojoDescriptor> all;

    private final ImmutableMap<String, PojoDescriptor> descriptors;

    private final ImmutableMap<String, PojoDescriptor> joinTableDescriptors;

    private final ImmutableMap<String, ImmutableList<PojoDescriptor>> packages;
    
    private final ImmutableMap<String, ViewDescriptor> viewDescriptors;

    private final ImmutableMap<String, ImmutableList<ViewDescriptor>> viewDescriptorsPackages;
    
    SourceCode(ProcessingEnvironment env, List<PojoDescriptor> tables, List<PojoDescriptor> joinTables, List<ViewDescriptor> viewDescriptors) {
        this.descriptors = tables.stream().collect(ImmutableMap.toImmutableMap(PojoDescriptor::fullyQualidiedClassName, desc -> desc));
        this.joinTableDescriptors = joinTables.stream().collect(ImmutableMap.toImmutableMap(PojoDescriptor::fullyQualidiedClassName, desc -> desc));
        this.all = ImmutableList.<PojoDescriptor>builder().addAll(tables).addAll(joinTables).build();
        this.packages = mapByPackage(all,env);
        this.viewDescriptors = viewDescriptors.stream().collect(ImmutableMap.toImmutableMap(ViewDescriptor::fullyQualidiedClassName, desc -> desc));
        this.viewDescriptorsPackages = mapByPackage(viewDescriptors, env);
    }


    void dump() {
        logger.info("Result Analysis -----------------------------------------------------------------------------------------");
        logger.info("---------------------------------------------------------------------------------------------------------");
        logger.info("Number of Pojo(s) found : " + descriptors.size());
        descriptors.values().forEach(pojoDesc -> {
        	logger.info("\t->" + pojoDesc);
        });
        logger.info("---------------------------------------------------------------------------------------------------------");
        logger.info("Number of Joint Table Pojo(s) found : " + joinTableDescriptors.size());
        joinTableDescriptors.values().forEach(pojoDesc -> {
        	logger.info("\t->" + pojoDesc);
        });
        logger.info("---------------------------------------------------------------------------------------------------------");
        logger.info("Number of View(s) found : " + viewDescriptors.size());
        viewDescriptors.values().forEach(viewDesc -> {
        	logger.info("\t->" + viewDesc);
        });
        logger.info("---------------------------------------------------------------------------------------------------------");
    }

    public PojoDescriptor getPojoDescriptor(String name) {
    	return this.descriptors.get(name);
    }

	public void forEach(Consumer<PojoDescriptor> consumer) {
        this.all.forEach(consumer);
	}

	public void forEachView(Consumer<ViewDescriptor> consumer) {
        this.viewDescriptors.values().forEach(consumer);
	}
	
    public void forEachByPackage(BiConsumer<String, ImmutableList<PojoDescriptor>> consumer) {
        this.packages.forEach(consumer);
	}
    
    public void forEachViewByPackage(BiConsumer<String, ImmutableList<ViewDescriptor>> consumer) {
        this.viewDescriptorsPackages.forEach(consumer);
	}

	public ImmutableList<PojoDescriptor> all() {
		return this.all;
	}

    private <T extends Desc> ImmutableMap<String, ImmutableList<T>> mapByPackage(Collection<T> all, ProcessingEnvironment env) {

        Map<String, List<T>> map = new HashMap<>();

        all.forEach(desc -> {
            String pack = env.getElementUtils().getPackageOf(desc.element()).toString();
            if (pack.startsWith("package")) {
                // with eclipse compiler
                pack = pack.substring(7).trim();
            }
            List<T> list = map.get(pack);
            if (list == null) {
                list = new ArrayList<>();
                map.put(pack, list);
            }
            list.add(desc);
        });

        ImmutableMap.Builder<String, ImmutableList<T>> builder = ImmutableMap.builder();
        map.forEach((key, value) -> {
            builder.put(key , ImmutableList.copyOf(value));
        });
        return builder.build();
    }

}