package com.fr.swift.service;

import com.fr.swift.config.bean.ServerCurrentStatus;
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
    <Stuff extends IndexingStuff> void index(Stuff stuff);

    ServerCurrentStatus currentStatus();
}