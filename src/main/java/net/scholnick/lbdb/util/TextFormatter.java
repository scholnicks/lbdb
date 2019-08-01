package net.scholnick.lbdb.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * TextFormatter returns textual representation of an Object. All methods are null-safe and thread-safe, <b>null</b> will never be returned by any method.
 * 
 * @author Steven Scholnick
 */
public final class TextFormatter {
	/**
	 * returns the textual representation of an object. <code>null</code> is returned as an empty string. 
	 * <code>null</code> return an empty string.
	 */
	private static String toText(Object o) {
		if (o == null)
			return "";

		if ("null".equals(o))
			return "";

		if (o instanceof Calendar)
			return toText((Calendar) o);
		if (o instanceof Date)
			return toText((Date) o);
		if (o instanceof Collection)
			return toText((Collection<?>) o);

		return o.toString();
	}

	/** converts a Collection to a Collections of Strings */
	private static <T> Collection<String> toStrings(Collection<T> c) {
		if (c == null)
			return new HashSet<>();

		Set<String> results = new HashSet<>(c.size());

		for (T each : c) {
			if (each != null)
				results.add(toText(each));
		}

		return results;
	}

//	/** converts an Array to a comma separated String */
//	public static String toText(Object[] array) {
//		if (array == null)
//			return "";
//
//		return toText(Arrays.asList(array));
//	}

	/** converts a Collection to a comma separated String */
	private static <T> String toText(Collection<T> c) {
		if (c == null)
			return "";

		List<String> data = new ArrayList<>(toStrings(c));

		Collections.sort(data);

		StringBuilder buf = new StringBuilder(128);

		for (int i = 0, n = data.size(); i < n; i++) {
			buf.append(data.get(i));

			if (i != (n - 1))
				buf.append(", ");
		}

		return buf.toString();
	}

	/** converts a Calendar to a String */
	public static String toText(Calendar c) {
		if (c == null)
			return "";

		return DATE_FORMAT.format(c.getTime());
	}

//	/** converts a boolean to a "Yes/No" String */
//	private static String toText(boolean b) {
//		return b ? "Yes" : "No";
//	}

//	/** converts a Boolean to a "Yes/No" String */
//	public static String toText(Boolean b) {
//		if (b == null)
//			return "";
//
//		return toText(b.booleanValue());
//	}

//	public static String getTagData(String tagName, String xmlData) {
//		try {
//			String opening = "<" + tagName + ">";
//			String closing = "</" + tagName + ">";
//
//			int openingIndex = xmlData.indexOf(opening);
//			int closingIndex = xmlData.indexOf(closing);
//
//			if (openingIndex < 0 || closingIndex < 0 || closingIndex >= xmlData.length() || (openingIndex + opening.length()) >= xmlData.length())
//				return "";
//
//			return NullSafe.trim(xmlData.substring(openingIndex + opening.length(), closingIndex));
//		}
//		catch (IndexOutOfBoundsException e) {
//			return "";
//		}
//	}

	private TextFormatter() {} // do not allow instantiation

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
}
