package com.gempukku.startrek.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

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
        return merge(values, delimiter, 0, values.length);
    }

    public static String merge(String[] values, String delimiter, int startIndex, int count) {
        return merge(Arrays.asList(values), delimiter, startIndex, count);
    }

    public static String merge(Iterable<String> values, String delimiter) {
        return merge(values, delimiter, 0, Integer.MAX_VALUE);
    }

    public static String merge(Iterable<String> values, String delimiter, int startIndex, int count) {
        return merge(values, s -> s, delimiter, startIndex, count);
    }

    public static <T> String merge(Iterable<T> values, Function<T, String> valueFunction,
                                   String delimiter, int startIndex, int count) {
        if (count == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        Iterator<T> iterator = values.iterator();
        // Skip the startIndex
        for (int i = 0; i < startIndex; i++) {
            if (iterator.hasNext())
                iterator.next();
        }
        int addedCount = 0;
        while (addedCount < count && iterator.hasNext()) {
            sb.append(delimiter).append(valueFunction.apply(iterator.next()));
            addedCount++;
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
