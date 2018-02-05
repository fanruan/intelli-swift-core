package com.fr.swift.query.filter.info;

import com.fr.swift.bitmap.BitMaps;
import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.query.filter.DetailFilterFactory;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.segment.Segment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/2/2.
 */
public class DetailFilterInfo extends AbstractDetailFilterInfo {

    List<SwiftDetailFilterValue> filterValues;

    public DetailFilterInfo(List<SwiftDetailFilterValue> filterValues) {
        this.filterValues = filterValues;
    }

    @Override
    public DetailFilter createDetailFilter(final Segment segment) {
        final List<DetailFilter> filters = new ArrayList<DetailFilter>();
        for (SwiftDetailFilterValue filterValue : filterValues) {
            filters.add(DetailFilterFactory.createFilter(segment, filterValue));
        }
        return new DetailFilter() {
            @Override
            public ImmutableBitMap createFilterIndex() {
                ImmutableBitMap bitMap = BitMaps.newAllShowBitMap(segment.getRowCount());
                for (DetailFilter filter : filters) {
                    bitMap.getAnd(filter.createFilterIndex());
                }
                return bitMap;
            }

            @Override
            public boolean matches(SwiftNode node) {
                return false;
            }
        };
    }
}
