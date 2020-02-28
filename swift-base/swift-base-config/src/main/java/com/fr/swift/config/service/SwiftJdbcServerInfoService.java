package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftJdbcServerInfo;

import java.util.Map;

/**
 * @author yee
 * @version 1.1
 * Created by yee on 2019-09-03
 */
public interface SwiftJdbcServerInfoService extends ConfigService<SwiftJdbcServerInfo> {
    Map<String, SwiftJdbcServerInfo> getAllServerInfo();
}
