package com.fr.swift.api.request;

import com.fr.swift.api.info.RequestInfo;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface RequestService<T, Sender extends RpcSender> {


    /**
     * execute query
     *
     * @param sender
     * @param sql
     * @return
     */
    T apply(Sender sender, RequestInfo sql);

    T apply(Sender sender, String requestJson);

    /**
     * execute query with retry
     *
     * @param sender
     * @param sql
     * @param retryTime
     * @return
     */
    T applyWithRetry(Sender sender, RequestInfo sql, int retryTime);

    T applyWithRetry(Sender sender, String requestJson, int retryTime);
}
