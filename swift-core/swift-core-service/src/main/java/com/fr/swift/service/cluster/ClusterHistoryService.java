package com.fr.swift.service.cluster;

import com.fr.swift.db.Where;
import com.fr.swift.service.HistoryService;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * This class created on 2018/9/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface ClusterHistoryService extends HistoryService {
    boolean delete(SourceKey sourceKey, Where where, List<String> segKeys) throws Exception;
}
