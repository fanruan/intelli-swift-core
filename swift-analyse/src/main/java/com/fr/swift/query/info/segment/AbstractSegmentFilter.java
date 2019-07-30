package com.fr.swift.query.info.segment;

import com.fr.swift.SwiftContext;
import com.fr.swift.config.entity.SwiftSegmentBucket;
import com.fr.swift.config.entity.SwiftTableAllotRule;
import com.fr.swift.query.info.SegmentFilter;
import com.fr.swift.segment.SwiftSegmentManager;

/**
 * Create by lifan on 2019-07-24 17:00
 */
public abstract class AbstractSegmentFilter implements SegmentFilter {

    protected final static int ALL_SEGMENT = -1;

    protected static final SwiftSegmentManager SEG_SVC = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);

    protected SwiftTableAllotRule tableAllotRule;
    protected SwiftSegmentBucket segmentBucket;

    public AbstractSegmentFilter(SwiftTableAllotRule tableAllotRule, SwiftSegmentBucket segmentBucket) {
        this.tableAllotRule = tableAllotRule;
        this.segmentBucket = segmentBucket;
    }

}
