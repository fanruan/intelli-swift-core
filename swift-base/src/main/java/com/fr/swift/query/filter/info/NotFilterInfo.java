package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.NotFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.core.CoreField;

/**
 * Created by Lyon on 2018/7/3.
 */
public class NotFilterInfo extends AbstractFilterInfo {

    @CoreField
    private FilterInfo filterInfo;

    public NotFilterInfo(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
    }

    public SwiftDetailFilterType getType() {
        return SwiftDetailFilterType.NOT;
    }

    @Override
    public boolean isMatchFilter() {
        return filterInfo != null && filterInfo.isMatchFilter();
    }

    @Override
    public DetailFilter createDetailFilter(Segment segment) {
        return new NotFilter(segment.getRowCount(), filterInfo.createDetailFilter(segment));
    }

    @Override
    public MatchFilter createMatchFilter() {
        return null;
    }
}
