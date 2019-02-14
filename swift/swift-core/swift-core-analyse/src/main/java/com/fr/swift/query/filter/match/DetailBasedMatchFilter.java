package com.fr.swift.query.filter.match;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.SwiftNode;

/**
 * Created by pony on 2018/4/17.
 */
public class DetailBasedMatchFilter implements MatchFilter {
    private int targetIndex;
    private MatchConverter converter;
    private DetailFilter filter;

    /**
     * 明细过滤器当成结果过滤来用
     * 可能根据指标的值SwiftNode#AggregatorValue[targetIndex]来过滤，也可能根据Node本身来过滤，比如过滤前N个Node
     *
     * @param targetIndex 如果依赖指标进行过滤，则对应该指标在指标数组中的index，否则targetIndex为-1，相关过滤器依赖这个-1来区分过滤方式
     * @param converter
     * @param filter      明细过滤器
     */
    public DetailBasedMatchFilter(int targetIndex, MatchConverter converter, DetailFilter filter) {
        this.targetIndex = targetIndex;
        this.converter = converter;
        this.filter = filter;
    }

    public DetailBasedMatchFilter(int targetIndex, DetailFilter filter) {
        this.targetIndex = targetIndex;
        this.converter = new ToStringConverter();
        this.filter = filter;
    }

    @Override
    public boolean matches(SwiftNode node) {
        return filter.matches(node, this.targetIndex, converter);
    }
}
