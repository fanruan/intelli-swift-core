package com.fr.swift.api.rpc.session.impl;

import com.fr.swift.api.info.AuthRequestInfo;
import com.fr.swift.api.info.RequestInfo;
import com.fr.swift.api.request.impl.ApiRequestServiceImpl;
import com.fr.swift.api.rpc.Api;
import com.fr.swift.api.rpc.ApiServiceAddressHolder;
import com.fr.swift.api.rpc.invoke.CallClient;
import com.fr.swift.api.rpc.pool.CallClientPool;
import com.fr.swift.api.rpc.session.SwiftApiSessionFactory;
import com.fr.swift.api.server.response.ApiResponse;
import com.fr.swift.api.server.response.AuthResponse;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.Strings;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author yee
 * @date 2018/8/27
 */
public class SwiftApiSessionFactoryImpl implements SwiftApiSessionFactory<SwiftApiSessionImpl> {

    protected ApiServiceAddressHolder holder;
    private ApiRequestServiceImpl request = new ApiRequestServiceImpl();
    private int maxFrameSize;

    private String authCode;

    private String address;

    public SwiftApiSessionFactoryImpl(String address, int maxFrameSize) {
        this.address = address;
        this.maxFrameSize = maxFrameSize;
    }

    public SwiftApiSessionFactoryImpl(String address) {
        this(address, Api.DEFAULT_MAX_FRAME_SIZE);
    }


    @Override
    public SwiftApiSessionImpl openSession() {
        if (Strings.isEmpty(authCode)) {
            Crasher.crash("Auth code cannot be null. Please ensure the factory had been initialized before you open a session.");
        }

        return new SwiftApiSessionImpl(holder.nextAnalyse(), holder.nextRealTime(), this);
    }

    @Override
    public void init(String username, String password) throws Exception {
        RequestInfo info = new AuthRequestInfo(username, password);
        ApiResponse response = callRpc(address, info);
        if (response.isError()) {
            throw new Exception(String.format("Api ERROR. Code %d, %s", response.statusCode(), response.description()));
        }
        AuthResponse auth = (AuthResponse) response.result();
        authCode = auth.getAuthCode();
        this.holder = new ApiServiceAddressHolder();
        holder.setAnalyseAddresses(new LinkedBlockingQueue<String>(auth.getAnalyseAddresses()));
        holder.setRealtimeAddresses(new LinkedBlockingQueue<String>(auth.getRealTimeAddresses()));
    }

    ApiResponse callRpc(String address, RequestInfo requestInfo) throws Exception {
        CallClient client = CallClientPool.getInstance(maxFrameSize).borrowObject(address);
        try {
            if (!client.isActive()) {
                CallClientPool.getInstance(maxFrameSize).returnObject(address, client);
                CallClientPool.getInstance(maxFrameSize).invalidateObject(address, client);
                client = CallClientPool.getInstance(maxFrameSize).borrowObject(address);
            }
            return request.applyWithRetry(client, requestInfo, 3);
        } finally {
            CallClientPool.getInstance(maxFrameSize).returnObject(address, client);
        }
    }

    @Override
    public void close() {
        CallClientPool.getInstance(maxFrameSize).close();
    }

    public String getAuthCode() {
        return authCode;
    }
}
