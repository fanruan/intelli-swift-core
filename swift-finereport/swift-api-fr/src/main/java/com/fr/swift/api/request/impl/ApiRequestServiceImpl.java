package com.fr.swift.api.request.impl;

import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.request.RequestService;
import com.fr.swift.api.rpc.Api;
import com.fr.swift.api.rpc.invoke.CallClient;
import com.fr.swift.api.server.ApiServerService;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.base.json.JsonBuilder;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018-12-07
 */
public class ApiRequestServiceImpl implements RequestService<CallClient> {

    @Override
    public ApiResponse apply(CallClient sender, RequestInfo sql) {
        try {
            return apply(sender, JsonBuilder.writeJsonString(sql));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponse apply(CallClient sender, String requestJson) {
        return new Api(sender).getProxy(ApiServerService.class).dispatchRequest(requestJson);
    }

    @Override
    public ApiResponse applyWithRetry(CallClient sender, RequestInfo sql, int retryTime) {
        try {
            return applyWithRetry(sender, JsonBuilder.writeJsonString(sql), retryTime);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ApiResponse applyWithRetry(CallClient sender, String requestJson, int retryTime) {
        ApiResponse response = null;
        for (int i = 0; i < retryTime; i++) {
            try {
                response = apply(sender, requestJson);
                if (!response.isError()) {
                    return response;
                }
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (final Exception e) {
                response = new ApiResponse() {
                    @Override
                    public int statusCode() {
                        return 0;
                    }

                    @Override
                    public String description() {
                        return e.getMessage();
                    }

                    @Override
                    public boolean isError() {
                        return true;
                    }

                    @Override
                    public Serializable result() {
                        return null;
                    }

                    @Override
                    public void setResult(Serializable result) {

                    }

                    @Override
                    public void setThrowable(Throwable throwable) {

                    }

                    @Override
                    public void setStatusCode(int statusCode) {

                    }
                };
            }
        }
        return response;
    }
}
