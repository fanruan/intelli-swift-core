package com.fr.swift.util;

import java.util.regex.Matcher;

/**
 * 字符串的操作
 *
 * @author anchore
 * @date 17/11/27
 */
public final class Strings {
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
            String quoteOldSep = Matcher.quoteReplacement(oldSep);
            String onlyHasNewSep = s.replaceAll("(" + quoteOldSep + ")+", Matcher.quoteReplacement(newSep));
            if (onlyHasNewSep.contains(newSep + newSep)) {
                return trimSeparator(onlyHasNewSep, newSep);
            }
            return onlyHasNewSep;
        }
        if (s.contains(newSep + newSep)) {
            return trimSeparator(s, newSep);
        }
        return s;
    }

    public static String unifySlash(String s) {
        return trimSeparator(s, "\\", "/");
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
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
}