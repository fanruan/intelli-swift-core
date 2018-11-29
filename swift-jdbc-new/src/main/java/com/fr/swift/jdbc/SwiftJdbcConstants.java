package com.fr.swift.jdbc;

/**
 * @author yee
 * @date 2018/11/19
 */
public class SwiftJdbcConstants {
    public static final String DEFAULT_DATABASE = "DECISION_LOG";
    public static final String EMPTY = "";
    public static final String SEPARATOR = "/";
    public static final String TABLE = "TABLE";

    public static final int JSON_QUERY_HEAD_LENGTH = KeyWords.JSON_QUERY.length() + 1;

    public static class KeyWords {
        public static final String JSON_QUERY = "jsonquery";
    }
}
