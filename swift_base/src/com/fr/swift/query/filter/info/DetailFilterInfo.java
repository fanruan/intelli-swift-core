package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.GeneralAndFilter;
import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * Created by Lyon on 2018/2/2.
 */
public class DetailFilterInfo extends AbstractDetailFilterInfo {

    List<FilterInfo> filterValues;

    public DetailFilterInfo(List<FilterInfo> filterValues) {
        this.filterValues = filterValues;
    }

    @Override
    public DetailFilter createDetailFilter(final Segment segment) {
        return new GeneralAndFilter(filterValues, segment);
    }
}
