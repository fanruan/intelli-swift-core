package com.fr.swift.config.service;

import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.source.SourceKey;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
public interface SwiftSegmentBucketService {

    SwiftSegmentBucket getBucketByTable(SourceKey sourceKey);

    void save(SwiftSegmentBucketElement element);

    void delete(SwiftSegmentBucketElement element);

}
