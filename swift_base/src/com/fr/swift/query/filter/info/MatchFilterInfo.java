package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.DetailBasedMatchFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.segment.Segment;

/**
 * Created by pony on 2018/4/17.
 */
public class MatchFilterInfo extends AbstractFilterInfo {
    private FilterInfo filterInfo;
    private int index;

    public MatchFilterInfo(FilterInfo filterInfo, int index) {
        this.filterInfo = filterInfo;
        this.index = index;
    }

    @Override
    public boolean isMatchFilter() {
        return true;
    }

    @Override
    public DetailFilter createDetailFilter(Segment segment) {
        return filterInfo.createDetailFilter(segment);
    }

    @Override
    public MatchFilter createMatchFilter() {
        return new DetailBasedMatchFilter(index, this.createDetailFilter(null));
    }
}
