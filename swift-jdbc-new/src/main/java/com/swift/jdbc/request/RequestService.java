package com.swift.jdbc.request;

import com.swift.jdbc.info.SqlInfo;
import com.swift.jdbc.response.JdbcResponse;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface RequestService {
    /**
     * execute auth
     *
     * @param sender
     * @param user
     * @param password
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T apply(RequestSender sender, String user, String password);

    /**
     * execute query
     *
     * @param sender
     * @param sql
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T apply(RequestSender sender, SqlInfo sql);

    /**
     * execute auth with retry
     *
     * @param sender
     * @param user
     * @param password
     * @param retryTime
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T applyWithRetry(RequestSender sender, String user, String password, int retryTime);

    /**
     * execute query with retry
     *
     * @param sender
     * @param sql
     * @param retryTime
     * @param <T>
     * @return
     */
    <T extends JdbcResponse> T applyWithRetry(RequestSender sender, SqlInfo sql, int retryTime);
}
