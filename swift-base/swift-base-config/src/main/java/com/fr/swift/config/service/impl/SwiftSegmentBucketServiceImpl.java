package com.fr.swift.config.service.impl;

import com.fr.swift.SwiftContext;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.dao.SwiftDao;
import com.fr.swift.config.dao.SwiftDaoImpl;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean(name = "swiftSegmentBucketService")
public class SwiftSegmentBucketServiceImpl implements SwiftSegmentBucketService {

    private SwiftDao<SwiftSegmentBucketElement> dao = new SwiftDaoImpl<>(SwiftSegmentBucketElement.class);
    private SwiftSegmentService swiftSegmentService = SwiftContext.get().getBean(SwiftSegmentService.class);

    @Override
    public SwiftSegmentBucket getBucketByTable(SourceKey sourceKey) {
        final List<SwiftSegmentBucketElement> bucketElements = (List<SwiftSegmentBucketElement>) dao.select(criteria -> criteria.add(Restrictions.eq("unionKey.sourceKey", sourceKey.getId())));
        SwiftSegmentBucket swiftSegmentBucket = new SwiftSegmentBucket(sourceKey);

        if (bucketElements == null) {
            return swiftSegmentBucket;
        }

        for (SwiftSegmentBucketElement bucketElement : bucketElements) {
            List<SegmentKey> segmentKey = swiftSegmentService.getOwnSegments(new SourceKey(bucketElement.getRealSegmentKey()));
            swiftSegmentBucket.put(bucketElement.getBucketIndex(), segmentKey.get(0));
        }
        return swiftSegmentBucket;
    }

    @Override
    public void save(SwiftSegmentBucketElement element) {
        dao.insert(element);
    }

    @Override
    public void delete(SwiftSegmentBucketElement element) {
        dao.delete(criteria -> criteria.add(Restrictions.eq("bucketIndex", element.getBucketIndex()))
                .add(Restrictions.eq("realSegmentKey", element.getRealSegmentKey()))
                .add(Restrictions.eq("sourceKey", element.getSourceKey())));
    }
}