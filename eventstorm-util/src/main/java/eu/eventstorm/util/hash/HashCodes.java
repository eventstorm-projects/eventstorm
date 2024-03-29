package eu.eventstorm.util.hash;

public final class HashCodes {

    private HashCodes() {
    }


    public static int hash(String code) {
        if (code == null) {
            return 0;
        } else {
            return 31 * code.hashCode();
        }
    }

}