package com.fr.swift.jdbc.request.impl;

import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.json.JsonRequestBuilder;
import com.fr.swift.api.server.ApiServerService;
import com.fr.swift.jdbc.info.AuthRequestInfo;
import com.fr.swift.jdbc.request.RequestService;
import com.fr.swift.jdbc.response.JdbcResponse;
import com.fr.swift.jdbc.rpc.JdbcExecutor;
import com.fr.swift.jdbc.rpc.invoke.ClientProxy;
import com.fr.swift.rpc.bean.RpcResponse;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * @author yee
 * @date 2018/11/20
 */
public class RequestServiceImpl implements RequestService {
    @Override
    public <T extends JdbcResponse> T apply(JdbcExecutor sender, String user, String password) {
        String json = JsonRequestBuilder.BUILDER.buildRequest(new AuthRequestInfo(user, password));
        return apply(sender, json);
    }

    @Override
    public <T extends JdbcResponse> T apply(JdbcExecutor sender, RequestInfo sql) {
        String json = JsonRequestBuilder.BUILDER.buildRequest(sql);
        return apply(sender, json);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JdbcResponse> T apply(JdbcExecutor sender, final String requestJson) {
        ClientProxy proxy = new ClientProxy(sender);
        proxy.start();
        final RpcResponse response = proxy.getProxy(ApiServerService.class).dispatchRequest(requestJson);
        if (response.isError()) {
            return (T) new JdbcResponse() {

                @Override
                public SQLException exception() {
                    return new SQLException(response.getException());
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
    public <T extends JdbcResponse> T applyWithRetry(JdbcExecutor sender, String user, String password, int retryTime) {
        String json = JsonRequestBuilder.BUILDER.buildRequest(new AuthRequestInfo(user, password));
        return applyWithRetry(sender, json, retryTime);
    }

    @Override
    public <T extends JdbcResponse> T applyWithRetry(JdbcExecutor sender, RequestInfo sql, int retryTime) {
        String json = JsonRequestBuilder.BUILDER.buildRequest(sql);
        return applyWithRetry(sender, json, retryTime);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends JdbcResponse> T applyWithRetry(JdbcExecutor sender, String requestJson, int retryTime) {
        T response = null;
        for (int i = 0; i < retryTime; i++) {
            try {
                response = apply(sender, requestJson);
                if (null == response.exception()) {
                    return response;
                }
                TimeUnit.MILLISECONDS.sleep(10);
            } catch (final Exception e) {
                response = (T) new JdbcResponse() {

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
