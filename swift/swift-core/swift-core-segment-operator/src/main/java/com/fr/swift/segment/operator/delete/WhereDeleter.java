package com.fr.swift.segment.operator.delete;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.db.Where;
import com.fr.swift.segment.operator.Deleter;

/**
 * @author anchore
 * @date 2018/6/19
 * <p>
 * 按明细值删
 */
public interface WhereDeleter extends Deleter {
    ImmutableBitMap delete(Where where) throws Exception;
}