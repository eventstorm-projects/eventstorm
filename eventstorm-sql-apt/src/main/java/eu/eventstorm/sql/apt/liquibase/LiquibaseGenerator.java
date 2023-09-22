package eu.eventstorm.sql.apt.liquibase;

import eu.eventstorm.sql.annotation.BusinessKey;
import eu.eventstorm.sql.annotation.Column;
import eu.eventstorm.sql.annotation.Db;
import eu.eventstorm.sql.annotation.ForeignKey;
import eu.eventstorm.sql.annotation.Sequence;
import eu.eventstorm.sql.apt.SourceCode;
import eu.eventstorm.sql.apt.log.Logger;
import eu.eventstorm.sql.apt.model.GlobalConfigurationDescriptor;
import eu.eventstorm.sql.apt.model.PojoDescriptor;
import eu.eventstorm.sql.apt.util.Tuple;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class LiquibaseGenerator {

    private Logger logger;


    private final SourceCode sourceCode;

    public LiquibaseGenerator(SourceCode sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void generate(ProcessingEnvironment processingEnv, List<GlobalConfigurationDescriptor> configs) {
        try (Logger l = Logger.getLogger(processingEnv, "eu.eventstorm.sql.generator", "LiquibaseGenerator")) {
            this.logger = l;

            for (GlobalConfigurationDescriptor gcd : configs) {
                try {
                    generate(processingEnv, gcd);
                    generate4Junit(processingEnv, gcd);
                } catch (Exception cause) {
                    logger.error("", cause);
                }
            }
        }
    }

    private void generate(ProcessingEnvironment env, GlobalConfigurationDescriptor gcd) throws IOException {

        logger.info("Generate GlobalConfiguration for gcd");

        FileObject object = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "db.changelog", "db.changelog-master.yaml");
        try (Writer writer = object.openWriter()) {


            writer.append("databaseChangeLog:\n");

            List<Tuple<String, String>> toInclude = Arrays.asList(gcd.getGlobalConfiguration().changeLogsToInclude()).stream().map(v -> {
                String f = v.substring(v.lastIndexOf('/') + 1);
                if (f.contains("-")) {
                    return new Tuple<>(f.substring(0, f.indexOf('-')), v);
                }
                if (f.contains("_")) {
                    return new Tuple<>(f.substring(0, f.indexOf('_')), v);
                }
                return new Tuple<>(f.substring(0, v.indexOf(".sql")), v);
            }).collect(Collectors.toList());
            toInclude.sort((o1, o2) -> compareVersions(o1.getX(), o2.getX()));

            Iterator<Tuple<String, String>> iterator = toInclude.iterator();
            while (iterator.hasNext()) {
                Tuple<String, String> v = iterator.next();
                if (compareVersions(v.getX(), "1.0.0") <= 0) {
                    writer.append("  - include:\n");
                    writer.append("      file: " + v.getY() + "\n");
                    iterator.remove();
                } else {
                    break;
                }
            }

            LinkedHashMap<String, List<Item>> temp = collectAllVersions(gcd.getDescriptors());
            temp.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        try {
                            write(entry, gcd, env, writer);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });

            writer.flush();
        }
    }

    private void write(Map.Entry<String, List<Item>> entry, GlobalConfigurationDescriptor gcd, ProcessingEnvironment env, Writer changelogMaster) throws IOException {


        Map<String, List<Item>> temp = new HashMap<>();
        entry.getValue().stream().collect(Collectors.groupingBy(Item::getClass)).forEach((key, value) -> {
            logger.info("write for [" + key + "] -> [" + value + "]");
            temp.put(key.getSimpleName(), value);
        });

        int i = 1;


        if (temp.containsKey(ItemTable.class.getSimpleName())) {
            write(entry.getKey() + "-0" + i++ + "-TABLES.sql", temp.get(ItemTable.class.getSimpleName()), gcd, env, changelogMaster);
        }

        if (temp.containsKey(ItemJoinTable.class.getSimpleName())) {
            write(entry.getKey() + "-0" + i++ + "-JOIN_TABLES.sql", temp.get(ItemJoinTable.class.getSimpleName()), gcd, env, changelogMaster);
        }

        if (temp.containsKey(ItemIndex.class.getSimpleName())) {
            write(entry.getKey() + "-0" + i++ + "-BKS.sql", temp.get(ItemBusinessKey.class.getSimpleName()), gcd, env, changelogMaster);
        }

        if (temp.containsKey(ItemColumn.class.getSimpleName())) {
            write(entry.getKey() + "-0" + i++ + "-NEW_COLUMNS.sql", temp.get(ItemColumn.class.getSimpleName()), gcd, env, changelogMaster);
        }

        if (temp.containsKey(ItemIndex.class.getSimpleName())) {
            write(entry.getKey() + "-0" + i++ + "-INDEXES.sql", temp.get(ItemIndex.class.getSimpleName()), gcd, env, changelogMaster);
        }

        if (temp.containsKey(ItemForeignKey.class.getSimpleName())) {
            write(entry.getKey() + "-0" + i++ + "-FOREIGN_KEYS.sql", temp.get(ItemForeignKey.class.getSimpleName()), gcd, env, changelogMaster);
        }

        if (temp.containsKey(ItemSequence.class.getSimpleName())) {
            write(entry.getKey() + "-0" + i++ + "-SEQUENCES.sql", temp.get(ItemSequence.class.getSimpleName()), gcd, env, changelogMaster);
        }

    }

    private void write(String filename, List<Item> items, GlobalConfigurationDescriptor gcd, ProcessingEnvironment env, Writer changelogMaster) throws IOException {

        changelogMaster.append("  - include:\n");
        changelogMaster.append("      file: " + filename + "\n");
        changelogMaster.append("      relativeToChangelogFile: true\n");

        FileObject object = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "db.changelog", filename);
        try (Writer writer = object.openWriter()) {
            writer.append("--liquibase formatted sql\n");
            for (Db db : gcd.getGlobalConfiguration().databases()) {
                writer.append("--changeset autogenerate dbms:" + db.name().toLowerCase() + "\n");
                for (Item item : items) {
                    item.write(writer, DatabaseDialects.get(db));
                }
            }
        }
    }

    private LinkedHashMap<String, List<Item>> collectAllVersions(List<PojoDescriptor> descriptors) {

        LinkedHashMap<String, List<Item>> map = new LinkedHashMap<>();

        descriptors.forEach(pojo -> {

            String tableVersion;
            if (pojo.getTable() != null) {
                tableVersion = pojo.getTable().versionable().version();
                if (!map.containsKey(tableVersion)) {
                    map.put(tableVersion, new LinkedList<>());
                }
                map.get(tableVersion).add(new ItemTable(tableVersion, pojo));
            } else {
                tableVersion = pojo.getJoinTable().versionable().version();
                if (!map.containsKey(tableVersion)) {
                    map.put(tableVersion, new LinkedList<>());
                }
                map.get(tableVersion).add(new ItemJoinTable(tableVersion, pojo));
            }

            // check index;
            Arrays.asList(pojo.getTable().indexes()).forEach(index -> {
                logger.info("add Index [" + index.name() + "] to version [" + index.version() + "]");
                if (!map.containsKey(index.version())) {
                    map.put(index.version(), new LinkedList<>());
                }
                map.get(index.version()).add(new ItemIndex(index.version(), pojo, index));
            });

            List<Column> bks = new LinkedList<>();
            pojo.properties().forEach(property -> {
                Column column = property.getter().getAnnotation(Column.class);
                if (column != null) {
                    if (tableVersion.equals(column.version())) {
                        // same -> skip
                    } else {
                        if (!map.containsKey(column.version())) {
                            map.put(column.version(), new LinkedList<>());
                        }
                        map.get(column.version()).add(new ItemTable(tableVersion, pojo));
                    }

                    BusinessKey bk = property.getter().getAnnotation(BusinessKey.class);
                    if (bk != null) {
                        bks.add(column);
                    }
                }

                ForeignKey fk = property.getter().getAnnotation(ForeignKey.class);
                if (fk != null) {
                    if (!map.containsKey(fk.version())) {
                        map.put(fk.version(), new LinkedList<>());
                    }
                    map.get(fk.version()).add(new ItemForeignKey(logger, tableVersion, pojo, property, fk, sourceCode));
                }

            });
            if (!bks.isEmpty()) {
                map.get(tableVersion).add(new ItemBusinessKey(tableVersion, pojo, bks));
            }

            pojo.ids().forEach(p -> {
                if (p.getter().getAnnotation(Sequence.class) != null) {
                    map.get(tableVersion).add(new ItemSequence(logger, tableVersion, p.getter().getAnnotation(Sequence.class)));
                }
            });
        });

        return map;
    }

    private void generate4Junit(ProcessingEnvironment env, GlobalConfigurationDescriptor gcd) throws IOException {
        logger.info("Generate GlobalConfiguration for gcd");

        FileObject object = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "junit", "reset.sql");
        try (Writer writer = object.openWriter()) {

            gcd.getDescriptors().forEach(pojo -> {
                try {
                    writer.append("TRUNCATE TABLE " + pojo.getTable().value() + ";\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static int compareVersions(String version1, String version2) {
        int comparisonResult = 0;

        String[] version1Splits = version1.split("\\.");
        String[] version2Splits = version2.split("\\.");
        int maxLengthOfVersionSplits = Math.max(version1Splits.length, version2Splits.length);

        for (int i = 0; i < maxLengthOfVersionSplits; i++) {
            Integer v1 = i < version1Splits.length ? Integer.parseInt(version1Splits[i]) : 0;
            Integer v2 = i < version2Splits.length ? Integer.parseInt(version2Splits[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0) {
                comparisonResult = compare;
                break;
            }
        }
        return comparisonResult;
    }
}
