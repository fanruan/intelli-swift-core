package com.fr.swift.api.request;

import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.server.response.ApiResponse;

/**
 * @author yee
 * @date 2018/11/16
 */
public interface RequestService<Sender extends RpcSender> {


    /**
     * execute query
     *
     * @param sender
     * @param sql
     * @return
     */
    ApiResponse apply(Sender sender, RequestInfo sql);

    ApiResponse apply(Sender sender, String requestJson);

    /**
     * execute query with retry
     *
     * @param sender
     * @param sql
     * @param retryTime
     * @return
     */
    ApiResponse applyWithRetry(Sender sender, RequestInfo sql, int retryTime);

    ApiResponse applyWithRetry(Sender sender, String requestJson, int retryTime);
}
