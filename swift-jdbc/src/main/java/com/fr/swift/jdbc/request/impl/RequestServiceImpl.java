package com.fr.swift.jdbc.request.impl;

import com.fr.swift.api.info.AuthRequestInfo;
import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.server.ApiServerService;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.request.JdbcRequestService;
import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.jdbc.rpc.invoke.ClientProxy;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/11/20
 */
public class RequestServiceImpl implements JdbcRequestService {
    @Override
    public ApiResponse apply(JdbcExecutor sender, String user, String password) {
        return apply(sender, new AuthRequestInfo(user, password));
    }

    @Override
    public ApiResponse apply(JdbcExecutor sender, RequestInfo sql) {
        try {
            String json = JsonBuilder.writeJsonString(sql);
            return apply(sender, json);
        } catch (Exception e) {
            throw Exceptions.runtime("Build Json Exception", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse apply(JdbcExecutor sender, final String requestJson) {
        ClientProxy proxy = new ClientProxy(sender);
        return proxy.getProxy(ApiServerService.class).dispatchRequest(requestJson);
    }

    @Override
    public ApiResponse applyWithRetry(JdbcExecutor sender, String user, String password, int retryTime) {
        return applyWithRetry(sender, new AuthRequestInfo(user, password), retryTime);
    }

    @Override
    public ApiResponse applyWithRetry(JdbcExecutor sender, RequestInfo sql, int retryTime) {
        try {
            String json = JsonBuilder.writeJsonString(sql);
            return applyWithRetry(sender, json, retryTime);
        } catch (Exception e) {
            throw Exceptions.runtime("Build Json Exception", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ApiResponse applyWithRetry(JdbcExecutor sender, String requestJson, int retryTime) {
        ApiResponse response = null;
        for (int i = 0; i < retryTime; i++) {
            try {
                response = apply(sender, requestJson);
                if (!response.isError()) {
                    return response;
                }
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (final Exception e) {
                return new ApiResponse() {
                    @Override
                    public int statusCode() {
                        return UNKNOWN_ERROR;
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
