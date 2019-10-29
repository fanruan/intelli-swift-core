package com.fr.swift.query.group.by;

import com.fr.swift.query.group.info.GroupByInfo;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.segment.column.Column;
import com.fr.swift.structure.Pair;

import java.util.List;

/**
 * Created by Lyon on 2018/3/28.
 */
public class MultiGroupByValues extends MultiGroupBy<Object[]> {

    private List<Pair<Column, IndexInfo>> dimensions;

    public MultiGroupByValues(GroupByInfo groupByInfo) {
        super(groupByInfo);
        this.dimensions = groupByInfo.getDimensions();
    }

    @Override
    protected Object[] createKey(int[] indexes) {
        Object[] values = new Object[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            values[i] = dimensions.get(i).getKey().getDictionaryEncodedColumn().getValue(indexes[i]);
        }
        return values;
    }
}
