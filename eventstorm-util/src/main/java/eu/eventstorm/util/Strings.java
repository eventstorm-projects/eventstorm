package eu.eventstorm.util;

/**
 * @author <a href="mailto:jacques.militello@gmail.com">Jacques Militello</a>
 */
public final class Strings {

    /**
     * Empty String.
     */
    public static final String EMPTY = "";


    // private constructor.
    private Strings() {
    }

    /**
     * Checks if a CharSequence is empty ("") or null.
     * <p>
     * <pre>
     * Strings.isEmpty(null)      = true
     * Strings.isEmpty("")        = true
     * Strings.isEmpty(" ")       = false
     * Strings.isEmpty("bob")     = false
     * Strings.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null.
     * @return {@code true} if the CharSequence is empty or null.
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

}
