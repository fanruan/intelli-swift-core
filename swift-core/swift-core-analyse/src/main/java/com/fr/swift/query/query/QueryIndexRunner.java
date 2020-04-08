package com.fr.swift.query.query;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Table;
import com.fr.swift.db.Where;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SegmentKey;

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
}
