package com.fr.swift.query.filter;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.segment.Segment;

import java.util.Arrays;

/**
 * Created by pony on 2017/12/14.
 */
public class FilterBuilder {
    public static DetailFilter buildDetailFilter(Segment segment, FilterInfo info) {
        FilterInfo allShow = new SwiftDetailFilterInfo<Object>(null, null, SwiftDetailFilterType.ALL_SHOW);
        FilterInfo filterInfo = new GeneralFilterInfo(Arrays.asList(info, allShow), GeneralFilterInfo.AND);
        return filterInfo.createDetailFilter(segment);
    }

    public static MatchFilter buildMatchFilter(FilterInfo info) {
        return info.createMatchFilter();
    }
}
