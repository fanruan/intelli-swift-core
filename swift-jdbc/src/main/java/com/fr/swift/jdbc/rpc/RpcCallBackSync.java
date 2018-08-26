package com.fr.swift.jdbc.rpc;

import com.fr.swift.rpc.bean.RpcRequest;
import com.fr.swift.rpc.bean.RpcResponse;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yee
 * @date 2018/8/26
 */
public class RpcCallBackSync implements Future<RpcResponse> {

    private int index;

    /**
     * 请求发送包
     */
    private RpcRequest request;

    /**
     * 请求返回数据包
     */
    private RpcResponse response;

    public RpcCallBackSync(int index, RpcRequest request) {
        this.index = index;
        this.request = request;
    }

    public RpcRequest getRequest() {
        return request;
    }

    public void setRequest(RpcRequest request) {
        this.request = request;
    }

    public RpcResponse getResponse() {
        return response;
    }

    public void setResponse(RpcResponse response) {
        this.response = response;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        return response;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return null;
    }

}