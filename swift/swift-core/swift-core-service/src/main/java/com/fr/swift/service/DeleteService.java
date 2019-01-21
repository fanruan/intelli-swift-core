package com.fr.swift.service;

import com.fr.swift.db.Where;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/11/8
 */
interface DeleteService {

    boolean delete(SourceKey sourceKey, Where where, List<SegmentKey> segKeys) throws Exception;

    void truncate(SourceKey sourceKey);
}