package com.fr.swift.jdbc.info;

/**
 * 预解析后的sql信息
 * @see com.fr.swift.jdbc.json.JsonRequestBuilder#buildSqlRequest(SqlInfo)
 * @author yee
 * @date 2018/11/16
 */
public interface SqlInfo {
    String getSql();

    String getAuthCode();

    String getDatabase();

    Request getRequest();

    enum Request {
        SELECT, INSERT, DELETE, DROP, TRUNCATE, CREATE
    }
}
