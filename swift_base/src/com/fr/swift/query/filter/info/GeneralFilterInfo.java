package com.fr.swift.query.filter.info;

import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.segment.Segment;

import java.util.List;

/**
 * Created by pony on 2017/12/21.
 */
public class GeneralFilterInfo implements FilterInfo{
    public static final int OR = 0;
    public static final int AND = 1;
    private List<FilterInfo> children;

    private int type;

    public GeneralFilterInfo(List<FilterInfo> children, int type) {
        this.children = children;
        this.type = type;
    }

    @Override
    public boolean isMatchFilter() {
        if (children == null){
            return false;
        }
        for (FilterInfo filterInfo : children){
            if (filterInfo != null && filterInfo.isMatchFilter()){
                return true;
            }
        }
        return false;
    }

    @Override
    public DetailFilter createDetailFilter(Segment segment) {
        return null;
    }
}
