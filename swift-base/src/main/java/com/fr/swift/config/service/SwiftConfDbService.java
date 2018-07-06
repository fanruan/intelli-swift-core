package com.fr.swift.config.service;

import com.fr.swift.config.bean.SwiftConfDbBean;

/**
 * @author yee
 * @date 2018/7/6
 */
public interface SwiftConfDbService {
    SwiftConfDbBean getConfig();

    boolean saveDbConfig(SwiftConfDbBean config);
}
