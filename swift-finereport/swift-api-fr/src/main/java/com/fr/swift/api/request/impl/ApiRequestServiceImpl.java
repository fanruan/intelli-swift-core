package com.fr.swift.api.request.impl;

import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.json.impl.JsonRequestBuilderImpl;
import com.fr.swift.api.request.RequestService;
import com.fr.swift.api.rpc.Api;
import com.fr.swift.api.rpc.invoke.CallClient;
import com.fr.swift.api.server.ApiServerService;
import com.fr.swift.api.server.response.ApiResponse;

/**
 * @author yee
 * @date 2018-12-07
 */
public class ApiRequestServiceImpl implements RequestService<ApiResponse, CallClient> {

    @Override
    public ApiResponse apply(CallClient sender, RequestInfo sql) {
        return apply(sender, JsonRequestBuilderImpl.getInstance().buildRequest(sql));
    }

    @Override
    public ApiResponse apply(CallClient sender, String requestJson) {
        return new Api(sender).getProxy(ApiServerService.class).dispatchRequest(requestJson);
    }

    @Override
    public ApiResponse applyWithRetry(CallClient sender, RequestInfo sql, int retryTime) {
        return applyWithRetry(sender, JsonRequestBuilderImpl.getInstance().buildRequest(sql), retryTime);
    }

    @Override
    public ApiResponse applyWithRetry(CallClient sender, String requestJson, int retryTime) {
        return null;
    }
}
