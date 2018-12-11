package com.fr.swift.jdbc.request.impl;

import com.fr.swift.api.info.AuthRequestInfo;
import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.server.ApiServerService;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.jdbc.exception.Exceptions;
import com.fr.swift.jdbc.request.JdbcRequestService;
import com.fr.swift.jdbc.response.JdbcResponse;
import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.jdbc.rpc.invoke.ClientProxy;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/11/20
 */
public class RequestServiceImpl implements JdbcRequestService {
    @Override
    public JdbcResponse apply(JdbcExecutor sender, String user, String password) {
        return apply(sender, new AuthRequestInfo(user, password));
    }

    @Override
    public JdbcResponse apply(JdbcExecutor sender, RequestInfo sql) {
        try {
            String json = JsonBuilder.writeJsonString(sql);
            return apply(sender, json);
        } catch (Exception e) {
            throw Exceptions.runtime("Build Json Exception", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JdbcResponse apply(JdbcExecutor sender, final String requestJson) {
        ClientProxy proxy = new ClientProxy(sender);
        proxy.start();
        final ApiResponse response = proxy.getProxy(ApiServerService.class).dispatchRequest(requestJson);
        if (response.isError()) {
            return new JdbcResponse() {

                @Override
                public SQLException exception() {
                    return new SQLException(response.description());
                }

                @Override
                public Object result() {
                    return null;
                }
            };
        }
        // TODO JdbcResponse实现
        return null;
    }

    @Override
    public JdbcResponse applyWithRetry(JdbcExecutor sender, String user, String password, int retryTime) {
        return applyWithRetry(sender, new AuthRequestInfo(user, password), retryTime);
    }

    @Override
    public JdbcResponse applyWithRetry(JdbcExecutor sender, RequestInfo sql, int retryTime) {
        try {
            String json = JsonBuilder.writeJsonString(sql);
            return applyWithRetry(sender, json, retryTime);
        } catch (Exception e) {
            throw Exceptions.runtime("Build Json Exception", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public JdbcResponse applyWithRetry(JdbcExecutor sender, String requestJson, int retryTime) {
        JdbcResponse response = null;
        for (int i = 0; i < retryTime; i++) {
            try {
                response = apply(sender, requestJson);
                if (null == response.exception()) {
                    return response;
                }
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (final Exception e) {
                response = new JdbcResponse() {

                    @Override
                    public SQLException exception() {
                        return new SQLException(e);
                    }

                    @Override
                    public Object result() {
                        return null;
                    }
                };
            }
        }
        return response;
    }
}
