package com.fr.swift.jdbc.info;

/**
 * 预解析后的sql信息
 * @see com.fr.swift.jdbc.json.JsonRequestBuilder#buildRequest(RequestInfo)
 * @author yee
 * @date 2018/11/16
 */
public interface RequestInfo {
    String getAuthCode();

    Request getRequest();

    enum Request {
        SQL, AUTH, TABLES, COLUMNS
    }
}
