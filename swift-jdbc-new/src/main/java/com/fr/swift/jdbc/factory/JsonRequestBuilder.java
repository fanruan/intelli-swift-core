package com.fr.swift.jdbc.factory;

import com.fr.swift.jdbc.info.SqlInfo;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface JsonRequestBuilder {
    /**
     * build sql request json
     *
     * @param sql
     * @return
     */
    String buildSqlRequest(SqlInfo sql);

    /**
     * build auth request json
     *
     * @param user
     * @param password
     * @return
     */
    String buildAuthRequest(String user, String password);

    JsonRequestBuilder INSTANCE = new JsonRequestBuilder() {
        @Override
        public String buildSqlRequest(SqlInfo sql) {
            return null;
        }

        @Override
        public String buildAuthRequest(String user, String password) {
            return null;
        }
    };
}
