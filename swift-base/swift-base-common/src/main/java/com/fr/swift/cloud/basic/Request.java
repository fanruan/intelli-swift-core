package com.fr.swift.cloud.basic;

/**
 * @author yee
 * @date 2018/8/27
 */
public interface Request {
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
