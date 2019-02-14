package com.fr.swift.api.rpc;

import com.fr.swift.api.server.response.AuthResponse;

/**
 * @author yee
 * @date 2018/8/23
 */
public interface DetectService extends ApiService {
    /**
     * 获取Analyse和RealTime地址
     * @param defaultAddress
     * @return
     */
    AuthResponse detectiveAnalyseAndRealTime(String defaultAddress, String username, String password);
}
