package eu.eventstorm.util.hash;

import com.google.common.base.Objects;

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

    public static int hash(Object ... codes) {
        return Objects.hashCode(codes);
    }

}