package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Where;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.operator.Deleter;

import java.util.Map;

/**
 * @author anchore
 * @date 2018/6/19
 * <p>
 * 按明细值删
 */
public interface WhereDeleter extends Deleter {
    Map<SegmentKey, ImmutableBitMap> delete(Where where) throws Exception;
}