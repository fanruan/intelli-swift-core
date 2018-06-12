package com.fr.swift.service;

import com.fr.swift.info.ServerCurrentStatus;
import com.fr.swift.stuff.IndexingStuff;

/**
 * @author anchore
 * @date 2018/6/4
 */
public interface IndexingService extends SwiftService {
    /**
     * 全量导入
     *
     * @param stuff stuff
     */
    void index(IndexingStuff stuff);

    ServerCurrentStatus currentStatus();
}