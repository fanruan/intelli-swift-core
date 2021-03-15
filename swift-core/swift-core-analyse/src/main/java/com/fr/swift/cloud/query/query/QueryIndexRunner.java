package com.fr.swift.cloud.query.query;

import com.fr.swift.cloud.bitmap.ImmutableBitMap;
import com.fr.swift.cloud.db.Table;
import com.fr.swift.cloud.db.Where;
import com.fr.swift.cloud.segment.SegmentKey;

import java.util.Collection;
import java.util.Map;

/**
 * This class created on 2018/7/4
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface QueryIndexRunner {

    Map<SegmentKey, IndexQuery<ImmutableBitMap>> getBitMap(Table table, Where where) throws Exception;

    IndexQuery<ImmutableBitMap> getBitMap(Table table, Where where, SegmentKey segmentKey) throws Exception;

    Collection<SegmentKey> getWhereSegments(Table table, Where where) throws Exception;
}
