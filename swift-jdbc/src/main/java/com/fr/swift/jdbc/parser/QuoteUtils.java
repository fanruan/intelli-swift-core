package com.fr.swift.jdbc.parser;

import com.fr.stable.StringUtils;

/**
 * Created by lyon on 2018/8/23.
 */
class QuoteUtils {

    private static final String QUOTE = "`";

    static String trimQuote(String name) {
        if (StringUtils.isEmpty(name) || name.length() < 2) {
            return name;
        }
        if (name.substring(0, 1).equals(QUOTE) && name.substring(name.length() - 1, name.length()).equals(QUOTE)) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }
}
