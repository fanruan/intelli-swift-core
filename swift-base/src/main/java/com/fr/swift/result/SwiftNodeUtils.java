package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Iterator;
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

    public static Iterator<List<SwiftNode>> node2RowListIterator(SwiftNode root) {
        return new Tree2RowIterator<SwiftNode>(getDimensionSize(root), root.getChildren().iterator(), new Function<SwiftNode, Iterator<SwiftNode>>() {
            @Override
            public Iterator<SwiftNode> apply(SwiftNode p) {
                return p.getChildren().iterator();
            }
        });
    }

    public static Iterator<Row> node2RowIterator(SwiftNode root) {
        if (getDimensionSize(root) == 0) {
            Row row = new ListBasedRow(aggValue2Object(root.getAggregatorValue()));
            List<Row> list = new ArrayList<Row>();
            list.add(row);
            return list.iterator();
        }
        Iterator<List<SwiftNode>> iterator = node2RowListIterator(root);
        return new MapperIterator<List<SwiftNode>, Row>(iterator, new Function<List<SwiftNode>, Row>() {
            @Override
            public Row apply(List<SwiftNode> p) {
                return nodes2Row(p);
            }
        });
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
            data.addAll(aggValue2Object(values));
        }
        return new ListBasedRow(data);
    }

    private static List<Object> aggValue2Object(AggregatorValue[] values) {
        List<Object> objects = new ArrayList<Object>();
        values = values == null ? new AggregatorValue[0] : values;
        for (int i = 0; i < values.length; i++) {
            objects.add(values[i] == null ? null : values[i].calculateValue());
        }
        return objects;
    }
}
