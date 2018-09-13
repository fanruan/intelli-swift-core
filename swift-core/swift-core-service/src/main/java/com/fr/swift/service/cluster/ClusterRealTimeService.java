package com.fr.swift.service.cluster;

import com.fr.swift.db.Where;
import com.fr.swift.service.RealtimeService;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/8/7
 */
public interface ClusterRealTimeService extends RealtimeService {
    boolean delete(SourceKey sourceKey, Where where, List<String> segKeys) throws Exception;
}
