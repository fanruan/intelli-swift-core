package com.fr.swift.api.server;

import com.fr.swift.api.server.response.ApiResponse;

/**
 * @author yee
 * @date 2018/11/20
 */
public interface ApiServerService {
    ApiResponse dispatchRequest(String request);
}
