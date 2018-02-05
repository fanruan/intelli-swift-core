package com.fr.swift.query.filter.detail;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.query.filter.match.MatchFilter;

/**
 * Created by pony on 2017/10/9.
 * 利用索引对明细进行过滤,同时也要支持对汇总结果的过滤
 */
public interface DetailFilter extends MatchFilter {
    ImmutableBitMap createFilterIndex();
}
