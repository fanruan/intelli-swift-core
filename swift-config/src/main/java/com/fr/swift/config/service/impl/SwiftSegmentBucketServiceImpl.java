package com.fr.swift.config.service.impl;

import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.config.command.impl.SwiftHibernateConfigCommandBus;
import com.fr.swift.config.condition.impl.SwiftConfigConditionImpl;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftSegmentBucketElement;
import com.fr.swift.config.entity.SwiftSegmentEntity;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.query.impl.SwiftHibernateConfigQueryBus;
import com.fr.swift.config.service.SwiftSegmentBucketService;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.source.SourceKey;
import com.fr.swift.util.function.Function;

import java.util.Collection;

/**
 * @author lucifer
 * @date 2019-06-25
 * @description
 * @since advanced swift 1.0
 */
@SwiftBean
public class SwiftSegmentBucketServiceImpl implements SwiftSegmentBucketService {
    private SwiftHibernateConfigCommandBus<SwiftSegmentBucketElement> commandBus = new SwiftHibernateConfigCommandBus<SwiftSegmentBucketElement>(SwiftSegmentBucketElement.class);
    private SwiftHibernateConfigQueryBus<SwiftSegmentBucketElement> queryBus = new SwiftHibernateConfigQueryBus<>(SwiftSegmentBucketElement.class);
    private SwiftHibernateConfigQueryBus<SwiftSegmentEntity> segBus = new SwiftHibernateConfigQueryBus<>(SwiftSegmentEntity.class);

    @Override
    public SwiftSegmentBucket getBucketByTable(final SourceKey sourceKey) {
        return queryBus.get(SwiftConfigConditionImpl.newInstance().addWhere(ConfigWhereImpl.eq("unionKey.sourceKey", sourceKey.getId())), new Function<Collection<SwiftSegmentBucketElement>, SwiftSegmentBucket>() {
            @Override
            public SwiftSegmentBucket apply(Collection<SwiftSegmentBucketElement> p) {
                SwiftSegmentBucket swiftSegmentBucket = new SwiftSegmentBucket(sourceKey);
                for (SwiftSegmentBucketElement bucketElement : p) {
                    SegmentKey segmentKey = segBus.select(bucketElement.getRealSegmentKey());
                    swiftSegmentBucket.put(bucketElement.getBucketIndex(), segmentKey);
                }
                return swiftSegmentBucket;

            }
        });
    }

    @Override
    public boolean saveElement(final SwiftSegmentBucketElement element) {
        commandBus.merge(element);
        return true;
    }
}
