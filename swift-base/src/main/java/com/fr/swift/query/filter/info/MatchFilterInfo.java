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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchFilterInfo that = (MatchFilterInfo) o;

        if (index != that.index) return false;
        return filterInfo != null ? filterInfo.equals(that.filterInfo) : that.filterInfo == null;
    }

    @Override
    public int hashCode() {
        int result = filterInfo != null ? filterInfo.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }
}
