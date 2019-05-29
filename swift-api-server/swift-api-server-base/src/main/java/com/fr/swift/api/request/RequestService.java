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
     * @param sender an operator to send rpc requestType
     * @param sql requestType info
     * @return response of the requestType
     */
    ApiResponse apply(Sender sender, RequestInfo sql);

    /**
     * execute query
     *
     * @param sender      an operator to send rpc requestType
     * @param requestJson requestType json
     * @return response of the requestType
     */
    ApiResponse apply(Sender sender, String requestJson);

    /**
     * execute query
     *
     * @param sender an operator to send rpc requestType
     * @param sql requestType info
     * @param retryTime retry time of this requestType.
     * @return response of the requestType
     */
    ApiResponse applyWithRetry(Sender sender, RequestInfo sql, int retryTime);

    /**
     * execute query
     *
     * @param sender an operator to send rpc requestType
     * @param requestJson requestType json
     * @param retryTime retry time of this requestType.
     * @return response of the requestType
     */
    ApiResponse applyWithRetry(Sender sender, String requestJson, int retryTime);
}
