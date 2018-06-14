package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lyon on 2018/6/13.
 */
public class SwiftNodeUtils {

    public static int getDimensionSize(SwiftNode root) {
        int size = 0;
        SwiftNode tmp = root;
        while (tmp.getChildrenSize() > 0) {
            size++;
            tmp = tmp.getChild(0);
        }
        return size;
    }

    public static Row nodes2Row(List<SwiftNode> row) {
        List data = new ArrayList();
        if (null != row) {
            for (SwiftNode col : row) {
                if (null != col) {
                    data.add(col.getData());
                } else {
                    data.add(null);
                }
            }
        }
        if (null != row) {
            SwiftNode leafNode = row.get(row.size() - 1);
            AggregatorValue[] values = leafNode.getAggregatorValue();
            values = values == null ? new AggregatorValue[0] : values;
            for (int i = 0; i < values.length; i++) {
                data.add(values[i] == null ? null : values[i].calculateValue());
            }
        }
        return new ListBasedRow(data);
    }
}
