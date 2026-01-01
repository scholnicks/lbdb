package net.scholnick.lbdb.isbn;

import java.util.*;

final class ISBNUtilities {
    /** Return candidate query ISBNs: normalized input + convertible counterpart (if available). */
    static Set<String> queryCandidates(String raw) {
        String s = normalize(raw);
        if (s == null) return Set.of();

        Set<String> out = new HashSet<>();
        if (isValidIsbn10(s)) {
            out.add(s);
            out.add(toIsbn13From10(s));
            return out;
        }
        if (isValidIsbn13(s)) {
            out.add(s);
            String as10 = toIsbn10From13(s);
            if (as10 != null) out.add(as10);
            return out;
        }
        return Set.of();
    }

    static String normalize(String raw) {
        if (raw == null) return null;
        String s = raw.replaceAll("[^0-9Xx]", "").toUpperCase();
        return s.isBlank() ? null : s;
    }

    static boolean isValidIsbn10(String s) {
        if (s == null || s.length() != 10) return false;
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            char c = s.charAt(i);
            int v;
            if (i == 9 && c == 'X') v = 10;
            else if (c >= '0' && c <= '9') v = c - '0';
            else return false;
            sum += (10 - i) * v;
        }
        return sum % 11 == 0;
    }

    static boolean isValidIsbn13(String s) {
        if (s == null || s.length() != 13) return false;
        for (int i = 0; i < 13; i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        int expected = computeIsbn13CheckDigit(s.substring(0, 12));
        int actual = s.charAt(12) - '0';
        return expected == actual;
    }

    static String toIsbn13From10(String isbn10) {
        Objects.requireNonNull(isbn10, "isbn10");
        if (!isValidIsbn10(isbn10)) throw new IllegalArgumentException("Invalid ISBN-10: " + isbn10);
        String core9 = isbn10.substring(0, 9);
        String prefix12 = "978" + core9;
        int cd = computeIsbn13CheckDigit(prefix12);
        return prefix12 + cd;
    }

    static String toIsbn10From13(String isbn13) {
        Objects.requireNonNull(isbn13, "isbn13");
        if (!isValidIsbn13(isbn13)) throw new IllegalArgumentException("Invalid ISBN-13: " + isbn13);
        if (!isbn13.startsWith("978")) return null;

        String core9 = isbn13.substring(3, 12);
        char cd = computeIsbn10CheckDigit(core9);
        return core9 + cd;
    }

    private static int computeIsbn13CheckDigit(String first12Digits) {
        if (first12Digits == null || first12Digits.length() != 12) {
            throw new IllegalArgumentException("Expected 12 digits");
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int d = first12Digits.charAt(i) - '0';
            sum += (i % 2 == 0) ? d : 3 * d;
        }
        int mod = sum % 10;
        return (10 - mod) % 10;
    }

    private static char computeIsbn10CheckDigit(String first9Digits) {
        if (first9Digits == null || first9Digits.length() != 9) {
            throw new IllegalArgumentException("Expected 9 digits");
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            char c = first9Digits.charAt(i);
            if (c < '0' || c > '9') throw new IllegalArgumentException("Non-digit in ISBN-10 core");
            int d = c - '0';
            sum += (10 - i) * d;
        }
        int remainder = sum % 11;
        int check = (11 - remainder) % 11;
        return (check == 10) ? 'X' : (char) ('0' + check);
    }

    private ISBNUtilities() {}
}
