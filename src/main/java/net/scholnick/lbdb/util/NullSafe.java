package net.scholnick.lbdb.util;

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

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null) return false;
		return o1.equals(o2);
	}
}
