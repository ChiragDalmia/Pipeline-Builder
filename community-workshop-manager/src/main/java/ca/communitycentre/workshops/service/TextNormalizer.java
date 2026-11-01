package ca.communitycentre.workshops.service;

import java.util.Locale;

/** Applies the same clean-up rules to text before it is compared or stored. */
public final class TextNormalizer {

    private TextNormalizer() {
    }

    /** Trims the value and collapses runs of whitespace to a single space. */
    public static String clean(String value) {
        return value == null ? null : value.trim().replaceAll("\\s+", " ");
    }

    /** Email addresses are stored in lower case so uniqueness ignores letter case. */
    public static String email(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    /** Workshop codes are stored in upper case, for example "ck-101" becomes "CK-101". */
    public static String code(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }
}
