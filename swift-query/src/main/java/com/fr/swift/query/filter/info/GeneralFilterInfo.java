package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.AllShowDetailFilter;
import com.fr.swift.query.filter.detail.impl.AndFilter;
import com.fr.swift.query.filter.detail.impl.OrFilter;
import com.fr.swift.query.filter.match.GeneralAndMatchFilter;
import com.fr.swift.query.filter.match.GeneralOrMatchFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.core.CoreField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/12/21.
 */
public class GeneralFilterInfo extends AbstractFilterInfo {
    public static final int OR = 0;
    public static final int AND = 1;
    @CoreField
    private List<FilterInfo> children;
    @CoreField
    private int type;

    public GeneralFilterInfo(List<FilterInfo> children, int type) {
        this.children = children;
        this.type = type;
    }

    public List<FilterInfo> getChildren() {
        return children;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean isMatchFilter() {
        if (children == null) {
            return false;
        }
        for (FilterInfo filterInfo : children) {
            if (filterInfo != null && filterInfo.isMatchFilter()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DetailFilter createDetailFilter(Segment segment) {
        return type == OR ? new OrFilter(toDetailFilter(children, segment)) : new AndFilter(toDetailFilter(children, segment));
    }

    private List<DetailFilter> toDetailFilter(List<FilterInfo> filterInfoList, Segment segment) {
        List<DetailFilter> detailFilters = new ArrayList<DetailFilter>();
        if (filterInfoList.size() == 0) {
            detailFilters.add(new AllShowDetailFilter(segment));
            return detailFilters;
        }
        for (FilterInfo filterInfo : filterInfoList) {
            detailFilters.add(filterInfo.createDetailFilter(segment));
        }
        return detailFilters;
    }

    @Override
    public MatchFilter createMatchFilter() {
        List<MatchFilter> matchFilters = new ArrayList<MatchFilter>();
        if (children != null) {
            for (FilterInfo info : children) {
                matchFilters.add(info.createMatchFilter());
            }
        }
        return type == AND ? new GeneralAndMatchFilter(matchFilters) : new GeneralOrMatchFilter(matchFilters);
    }
}
