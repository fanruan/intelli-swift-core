package com.fr.swift.api.info;

/**
 * @author yee
 * @date 2018-12-11
 */
public enum RequestType {
    // common
    AUTH,
    // api
    JSON_QUERY, INSERT, DELETE, CREATE_TABLE, DROP_TABLE, TRUNCATE_TABLE,
    // jdbc
    SQL, CATALOGS, TABLES, COLUMNS
}
