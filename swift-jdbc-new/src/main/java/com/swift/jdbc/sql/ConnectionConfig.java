package com.swift.jdbc.sql;

import com.swift.jdbc.request.RequestSender;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface ConnectionConfig {
    /**
     * return username
     *
     * @return
     */
    String swiftUser();

    /**
     * return password
     *
     * @return
     */
    String swiftPassword();

    /**
     * return selected database
     *
     * @return
     */
    String swiftDatabase();

    /**
     * return request sender
     *
     * @return
     */
    RequestSender requestSender();
}
