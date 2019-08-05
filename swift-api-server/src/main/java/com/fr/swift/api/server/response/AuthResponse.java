package com.fr.swift.api.server.response;

import java.util.List;

/**
 * @author yee
 * @date 2018-12-12
 */
public interface AuthResponse {
    String getAuthCode();

    List<String> getAnalyseAddresses();

    List<String> getRealTimeAddresses();
}
