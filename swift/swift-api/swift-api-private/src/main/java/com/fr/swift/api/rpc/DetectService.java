package com.fr.swift.api.rpc;

import com.fr.swift.service.ServiceType;

import java.util.List;
import java.util.Map;

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
    Map<ServiceType, List<String>> detectiveAnalyseAndRealTime(String defaultAddress);
}
