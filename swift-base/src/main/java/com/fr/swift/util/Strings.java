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
        String regex = String.format("(%s)+", Matcher.quoteReplacement(oldSep));
        return s.replaceAll(regex, Matcher.quoteReplacement(newSep));
    }

}