package com.fr.swift.result.node.xnode;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.KeyValue;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.node.GroupNodeFactory;
import com.fr.swift.result.row.RowIndexKey;
import com.fr.swift.result.row.XGroupByResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/11.
 */
public class XLeftNodeFactory {

    public static XLeftNode createXLeftNode(XGroupByResultSet resultSet, int targetLength) {
        List<KeyValue<RowIndexKey<int[]>, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>> xRowList =
                resultSet.getXResultList();
        @SuppressWarnings("unchecked")
        List<Map<Integer, Object>> rowDictionaries = resultSet.getRowGlobalDictionaries();
        int rowDimensionSize = resultSet.rowDimensionSize();
        GroupNodeFactory.Creator<XLeftNode> creator = new GroupNodeFactory.Creator<XLeftNode>() {
            @Override
            public XLeftNode create(int deep, Object data) {
                return new XLeftNode(deep, data);
            }
        };
        GroupNodeFactory.ValueSetter<XLeftNode, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>> valueSetter = new GroupNodeFactory.ValueSetter<XLeftNode, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>>>() {
            @Override
            public void setValue(XLeftNode node, List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> keyValues, int targetLength) {
                node.setValueArrayList(toValueArrayList(keyValues, targetLength));
            }
        };
        return GroupNodeFactory.createFromSortedList(targetLength, rowDimensionSize, xRowList, rowDictionaries,
                creator, valueSetter);
    }

    private static List<AggregatorValue[]> toValueArrayList(List<KeyValue<RowIndexKey<int[]>, AggregatorValue[]>> row,
                                                            int targetLength) {
        List<AggregatorValue[]> valueArrayList = new ArrayList<AggregatorValue[]>();
        for (int i = 0; i < row.size(); i++) {
            valueArrayList.add(GroupNodeFactory.createAggregatorValueArray(row.get(i).getValue(), targetLength));
        }
        return valueArrayList;
    }
}
