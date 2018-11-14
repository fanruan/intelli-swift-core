package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.match.DetailBasedMatchFilter;
import com.fr.swift.query.filter.match.MatchFilter;

/**
 * Created by pony on 2017/12/21.
 */
public abstract class AbstractDetailFilterInfo extends AbstractFilterInfo {
    @Override
    public boolean isMatchFilter() {
        return false;
    }

    @Override
    public MatchFilter createMatchFilter() {
        // 这边默认是对节点本身进行过滤的，而不是根据指标进行过滤
        // 为什么呢？因为依赖于指标的结果过滤在解析的时候直接解析为MatchFilter了
        return new DetailBasedMatchFilter(-1, this.createDetailFilter(null));
    }
}
