package com.fr.swift.rpc.bean;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface RpcRequest {
    String getRequestId();


    String getInterfaceName();


    String getServiceVersion();


    String getMethodName();


    Class<?>[] getParameterTypes();


    Object[] getParameters();

    RpcServiceType requestType();

    enum RpcServiceType {
        INTERNAL, EXTERNAL
    }
}
