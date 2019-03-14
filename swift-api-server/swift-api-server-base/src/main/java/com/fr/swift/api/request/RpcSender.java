package com.fr.swift.api.request;

import com.fr.swift.basic.SwiftResponse;
import com.fr.swift.basic.SwiftRequest;

/**
 * @author yee
 * @date 2018/8/26
 */
public interface RpcSender {
    /**
     * send rpc request
     *
     * @param request rpc request
     * @return rpc response
     * @throws Exception the method might be throw exception
     * @see SwiftRequest
     * @see SwiftResponse
     */
    SwiftResponse send(SwiftRequest request) throws Exception;
}
