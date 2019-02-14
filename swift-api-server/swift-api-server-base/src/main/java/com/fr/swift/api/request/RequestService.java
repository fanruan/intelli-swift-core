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
     * @param sender an operator to send rpc request
     * @param sql request info
     * @return response of the request
     */
    ApiResponse apply(Sender sender, RequestInfo sql);

    /**
     * execute query
     *
     * @param sender      an operator to send rpc request
     * @param requestJson request json
     * @return response of the request
     */
    ApiResponse apply(Sender sender, String requestJson);

    /**
     * execute query
     *
     * @param sender an operator to send rpc request
     * @param sql request info
     * @param retryTime retry time of this request.
     * @return response of the request
     */
    ApiResponse applyWithRetry(Sender sender, RequestInfo sql, int retryTime);

    /**
     * execute query
     *
     * @param sender an operator to send rpc request
     * @param requestJson request json
     * @param retryTime retry time of this request.
     * @return response of the request
     */
    ApiResponse applyWithRetry(Sender sender, String requestJson, int retryTime);
}
