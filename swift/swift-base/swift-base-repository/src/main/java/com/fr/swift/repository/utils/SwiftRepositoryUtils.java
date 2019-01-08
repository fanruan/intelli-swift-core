package com.fr.swift.repository.utils;

import com.fr.swift.util.Strings;

/**
 * @author yee
 * @date 2018-12-04
 */
public class SwiftRepositoryUtils {
    public static String getParent(String path) {
        if (Strings.isEmpty(path)) {
            return "/";
        }
        String separator = getPathSeparator(path);
        if (separator == null) {
            return null;
        } else {
            path = normalize(path);
            int prefix = getPrefixLength(path);
            int idx = path.lastIndexOf(separator);
            if (idx == path.length() - 1 && !path.startsWith(separator)) {
                path = path.substring(0, path.length() - 1);
                idx = path.lastIndexOf(separator);
            }

            if (idx < prefix) {
                return prefix > 0 && path.length() > prefix ? path.substring(0, prefix) : null;
            } else {
                return path.substring(0, idx);
            }
        }
    }

    private static String getPathSeparator(String path) {
        String seperator = "/";
        int idx = path.lastIndexOf(seperator);
        if (idx != -1) {
            return seperator;
        } else {
            seperator = "\\";
            return path.lastIndexOf(seperator) != -1 ? seperator : null;
        }
    }

    private static String normalize(String path) {
        String separator = getPathSeparator(path);
        if (Strings.isNotEmpty(separator) && path.length() > 1 && path.endsWith(separator)) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    private static int getPrefixLength(String path) {
        if (path.length() == 0) {
            return 0;
        } else {
            String separator = getPathSeparator(path);
            if (separator == null) {
                return 0;
            } else if (separator.equals("/")) {
                return path.charAt(0) == '/' ? 1 : 0;
            } else {
                byte c = 92;
                int len = path.length();
                char char0 = path.charAt(0);
                char char1 = path.charAt(1);
                if (char0 == c) {
                    return char1 == c ? 2 : 1;
                } else if (isLetter(char0) && char1 == ':') {
                    return len > 2 && path.charAt(2) == c ? 3 : 2;
                } else {
                    return 0;
                }
            }
        }
    }

    private static boolean isLetter(char testChar) {
        return testChar >= 'a' && testChar <= 'z' || testChar > 'A' && testChar <= 'Z';
    }

    public static String getName(String path) {
        if (Strings.isEmpty(path)) {
            return "/";
        }
        String separator = getPathSeparator(path);
        if (Strings.isEmpty(separator)) {
            return path;
        } else {
            path = normalize(path);
            int start = getPrefixLength(path);
            int end = path.lastIndexOf(separator);
            return end < start ? path.substring(start) : path.substring(end + 1);
        }
    }
}
