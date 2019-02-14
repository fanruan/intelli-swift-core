package com.fr.swift.util;

import java.util.Arrays;
import java.util.regex.Matcher;

/**
 * 字符串的操作
 *
 * @author anchore
 * @date 17/11/27
 */
public final class Strings {
    public static final String EMPTY = "";

    public static String trimSeparator(String s, String sep) {
        return trimSeparator(s, sep, sep);
    }

    /**
     * 去除多余分隔符，替换为新分隔符
     * <p>
     * comment from jdk:
     * Note that backslashes ({@code \}) and dollar signs ({@code $}) in the
     * replacement string may cause the results to be different than if it were
     * being treated as a literal replacement string; see
     * {@link java.util.regex.Matcher#replaceAll Matcher.replaceAll}.
     * Use {@link java.util.regex.Matcher#quoteReplacement} to suppress the special
     * meaning of these characters, if desired.
     *
     * @param s      string to be replaced
     * @param oldSep old separator
     * @param newSep new separator
     * @return new replaced string
     */
    public static String trimSeparator(String s, String oldSep, String newSep) {
        if (s.contains(oldSep)) {
            // oldSep全换成单个newSep
            String quoteOldSep = Matcher.quoteReplacement(oldSep);
            s = s.replaceAll("(" + quoteOldSep + ")+", Matcher.quoteReplacement(newSep));
        }
        if (s.contains(newSep + newSep)) {
            // 去除重复newSep
            return trimSeparator(s, newSep);
        }
        if (s.endsWith(newSep)) {
            // 去除最后的newSep
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String unifySlash(String s) {
        return trimSeparator(s, "\\", "/");
    }

    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasText(CharSequence str) {
        if (!hasLength(str)) {
            return false;
        } else {
            int strLen = str.length();

            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean isEmpty(String string) {
        return null == string || EMPTY.equals(string.trim());
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

    public static String rightPad(String str, int length) {
        int offset = length - str.length();
        if (offset <= 0) {
            return str;
        } else {
            char[] chars = new char[offset];
            Arrays.fill(chars, ' ');
            return str + String.valueOf(chars);
        }
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        int len = str.length();
        return isEmpty(str) || len == 0 || isBlank(str, len);
    }

    private static boolean isBlank(String str, int len) {
        for (int i = 0; i < len; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static String[] split(String src, String delimiter) {
        int maxparts = src.length() / delimiter.length() + 2;
        int[] positions = new int[maxparts];
        int dellen = delimiter.length();
        int j = 0;
        int count = 0;

        int i;
        for (positions[0] = -dellen; (i = src.indexOf(delimiter, j)) != -1; j = i + dellen) {
            ++count;
            positions[count] = i;
        }

        ++count;
        positions[count] = src.length();
        String[] result = new String[count];

        for (i = 0; i < count; ++i) {
            result[i] = src.substring(positions[i] + dellen, positions[i + 1]);
        }
        return result;
    }
}