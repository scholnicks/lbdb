package net.scholnick.lbdb.util;

import java.util.Objects;

public final class NullSafe {
	private NullSafe() {}

	public static boolean isEmpty(String s) {
		if (s == null) return true;
		return s.trim().length() == 0;
	}

	public static String asNull(String s) {
		return isEmpty(s) ? null : s;
	}

	public static String concatenate(String s1, String s2, String s3) {
		return notNullSubst(s1) + notNullSubst(s2) + notNullSubst(s3);
	}

	private static String notNullSubst(String s) {
		return notNullSubst(s, true);
	}

	public static String notNullSubst(String s, boolean trim) {
		if (s == null) return "";
		return trim ? s.trim() : s;
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

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) return false;
		return o1.equals(o2);
	}
	
	public static boolean canonicalEquals(String s1, String s2) {
		if (s1 == null || s2 == null) return false;
		return Objects.equals(toCanonical(s1), toCanonical(s2));
	}

	public static String toCanonical(String s) {
		return s == null ? null : s.replaceAll("\\s*","").trim().toLowerCase();
	}
	
//	public static boolean toBoolean(String s) {
//		if (s == null) {
//			return false;
//		}
//
//		return POSITIVE_VALUES.contains(s.toLowerCase(Locale.getDefault()));
//	}

//	private static final Set<String> POSITIVE_VALUES = new HashSet<>(Arrays.asList("1", "true", "yes"));
//
//    public static boolean startsWithIgnoreCase(String str1, String str2) {
//    	if (str1 == null || str2 == null) return false;
//        return str1.toLowerCase().startsWith(str2.toLowerCase());
//    }
}
