package eu.eventsotrm.sql.apt.util;

public final class AliasGenerator {

	private static String alias = null;
	
	private AliasGenerator() {
	}
	
	public static String generate() {
		
		if (alias == null) {
			alias = "a";
			return alias;
		}

		else if ("z".equals(alias)) {
			alias = "aa";
			return alias;
		}

		else if ("zz".equals(alias)) {
			alias = "aaa";
			return alias;
		}

		else if ("zzz".equals(alias)) {
			alias = "aaa";
			return alias;
		}

		StringBuilder builder = new StringBuilder();

		if (alias.length() > 1) {
			builder.append(alias.substring(0, alias.length() - 2));
		}
		builder.append((char) (alias.charAt(alias.length() - 1) + 1));

		alias = builder.toString();
		return alias;
	}
	
}
