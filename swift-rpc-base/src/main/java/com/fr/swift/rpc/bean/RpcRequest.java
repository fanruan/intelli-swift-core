package com.fr.swift.rpc.bean;

import com.fr.swift.annotation.RpcServiceType;

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
}
