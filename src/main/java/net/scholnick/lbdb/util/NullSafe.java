package net.scholnick.lbdb.util;

import org.apache.commons.text.similarity.LevenshteinDistance;

public final class NullSafe {
    private NullSafe() {}

    public static boolean isEmpty(String s) {
        if (s == null) return true;
        return s.trim().length() == 0;
    }

    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 != null && o2 == null) return 1;
        if (o1 == null) return -1;
        return o1.compareTo(o2);
    }

    public static boolean isClose(String s1, String s2) {
        if (s1 == null || s2 == null) return false;

        return LevenshteinDistance.getDefaultInstance().apply(
            s1.replaceAll("\\s*","").trim().toLowerCase(),
            s2.replaceAll("\\s*","").trim().toLowerCase()
        ) < 2;
    }
}
