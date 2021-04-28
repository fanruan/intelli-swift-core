package com.fr.swift.cloud.query.builder;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.query.info.detail.DetailQueryInfo;
import com.fr.swift.cloud.query.query.IndexQuery;
import com.fr.swift.cloud.segment.SegmentKey;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface LocalDetailIndexQueryBuilder {

    IndexQuery<ImmutableBitMap> buildLocalQuery(DetailQueryInfo info, SegmentKey segmentKey);
}
