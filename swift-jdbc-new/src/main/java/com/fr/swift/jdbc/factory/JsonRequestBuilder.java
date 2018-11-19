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
     * {
     *     requestType: "DETAIL_QUERY",
     *     table: "table_name",
     *     database: "database",
     *     columns: ["column1", "column2"],
     *     where: "column1 = 100",
     *     order: [{
     *         column: "column1",
     *         asc: true
     *     }]
     * }
     */
    String buildSqlRequest(SqlInfo sql);

    /**
     * build auth request json
     *
     * @param user
     * @param password
     * @return like
     * {
     *     requestType: "AUTH",
     *     swiftUser: "username",
     *     swiftPassword: "password"
     * }
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
