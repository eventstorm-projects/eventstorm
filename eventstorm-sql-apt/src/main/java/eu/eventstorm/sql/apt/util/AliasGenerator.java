package eu.eventstorm.sql.apt.util;

public final class AliasGenerator {

    private static String alias = null;

    private AliasGenerator() {
    }

    public static String generate(String prefix) {

        if (alias == null) {
            alias = "a";
            return prefix + alias;
        } else if ("z".equals(alias)) {
            alias = "aa";
            return prefix + alias;
        } else if ("zz".equals(alias)) {
            alias = "aaa";
            return prefix + alias;
        } else if ("zzz".equals(alias)) {
            alias = "aaa";
            return prefix + alias;
        }

        StringBuilder builder = new StringBuilder();

        if (alias.length() > 1) {
            builder.append(alias.substring(0, alias.length() - 2));
        }
        builder.append((char) (alias.charAt(alias.length() - 1) + 1));

        alias = builder.toString();
        return prefix + alias;
    }

}
