package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.detail.impl.GeneralAndFilter;
import com.fr.swift.query.filter.detail.impl.GeneralOrFilter;
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
        return type == OR ? new GeneralOrFilter(children, segment) : new GeneralAndFilter(children, segment);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneralFilterInfo that = (GeneralFilterInfo) o;

        if (type != that.type) return false;
        return children != null ? children.equals(that.children) : that.children == null;
    }

    @Override
    public int hashCode() {
        int result = children != null ? children.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }
}
