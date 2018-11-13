package com.fr.swift.query.filter;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.segment.Segment;

/**
 * Created by pony on 2017/12/14.
 */
public class FilterBuilder {
    public static DetailFilter buildDetailFilter(Segment segment, FilterInfo info) {
        return info.createDetailFilter(segment);
    }

    public static MatchFilter buildMatchFilter(FilterInfo info) {
        return info.createMatchFilter();
    }
}
