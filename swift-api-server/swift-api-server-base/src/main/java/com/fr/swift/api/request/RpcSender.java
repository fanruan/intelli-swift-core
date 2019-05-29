package com.fr.swift.api.request;

import com.fr.swift.basic.SwiftRequest;
import com.fr.swift.basic.SwiftResponse;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface RpcSender {
    /**
     * send rpc requestType
     *
     * @param request rpc requestType
     * @return rpc response
     * @throws Exception the method might be throw exception
     * @see SwiftRequest
     * @see SwiftResponse
     */
    SwiftResponse send(SwiftRequest request) throws Exception;
}
