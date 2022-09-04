package com.gempukku.startrek.common;

import java.util.Arrays;

public class StringUtils {
    public static String[] split(String str, String delimiter) {
        return split(str, delimiter, 0);
    }

    public static String[] split(String str, String delimiter, int limit) {
        if (str == null || str.length() == 0)
            return new String[0];
        return str.split(delimiter, limit);
    }

    public static String merge(String[] values, String delimiter) {
        return merge(Arrays.asList(values), delimiter);
    }

    public static String merge(Iterable<String> iterable, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String value : iterable) {
            sb.append(delimiter).append(value);
        }
        if (sb.length() > 0)
            sb.replace(0, delimiter.length(), "");

        return sb.toString();
    }

    public static boolean equals(String first, String second) {
        if (first == null && second == null)
            return true;
        if (first == null || second == null)
            return false;
        return first.equals(second);
    }
}
