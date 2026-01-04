package net.scholnick.lbdb.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Collection;

/**
 * NullSafe provides utility methods for null-safe operations.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public final class NullSafe {
    /** Checks if a string is null or empty (after trimming). */
    public static boolean isEmpty(String s) {
        if (s == null) return true;
        return s.trim().isEmpty();
    }

    /** Checks if a collection is null or empty. */
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    /** Trims a string, returning an empty string if null. */
    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** Compares two comparable objects, handling nulls safely. */
    public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 != null && o2 == null) return 1;
        if (o1 == null) return -1;
        return o1.compareTo(o2);
    }

    /** Checks if two strings are "close" to each other, ignoring case and whitespace. */
    public static boolean isClose(String s1, String s2) {
        if (s1 == null || s2 == null) return false;

        return LevenshteinDistance.getDefaultInstance().apply(
            s1.replaceAll("\\s*","").trim().toLowerCase(),
            s2.replaceAll("\\s*","").trim().toLowerCase()
        ) < 2;
    }

    private NullSafe() {}
}
