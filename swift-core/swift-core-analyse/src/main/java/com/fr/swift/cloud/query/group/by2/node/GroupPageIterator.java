package com.fr.swift.cloud.query.group.by2.node;

import com.fr.swift.cloud.query.group.by2.DFTIterator;
import com.fr.swift.cloud.query.group.by2.ItCreator;
import com.fr.swift.cloud.query.group.info.GroupByInfo;
import com.fr.swift.cloud.query.group.info.IndexInfo;
import com.fr.swift.cloud.query.group.info.MetricInfo;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeUtils;
import com.fr.swift.cloud.segment.column.Column;
import com.fr.swift.cloud.segment.column.DictionaryEncodedColumn;
import com.fr.swift.cloud.structure.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Lyon on 2018/7/25.
 */
class GroupPageIterator implements Iterator<GroupPage> {

    private int dimensionSize;
    private GroupByInfo groupByInfo;
    private Iterator<GroupNode> iterator;

    GroupPageIterator(int pageSize, GroupByInfo groupByInfo, MetricInfo metricInfo) {
        this.dimensionSize = groupByInfo.getDimensions().size();
        this.groupByInfo = groupByInfo;
        this.iterator = new GroupNodeIterator(dimensionSize, pageSize,
                new DFTIterator(dimensionSize, new ItCreator(groupByInfo)),
                new ItemMapper(groupByInfo.getDimensions()), new RowMapper(metricInfo));
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public GroupPage next() {
        GroupNode root = iterator.next();
        return new GroupPage(root, getGlobalDictionaries(root));
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    private List<Map<Integer, Object>> getGlobalDictionaries(GroupNode root) {
        List<Map<Integer, Object>> dictionaries = new ArrayList<Map<Integer, Object>>(dimensionSize);
        for (int i = 0; i < dimensionSize; i++) {
            dictionaries.add(null);
        }
        List<Pair<Column, IndexInfo>> columns = groupByInfo.getDimensions();
        Iterator<List<SwiftNode>> rowIt = SwiftNodeUtils.node2RowListIterator(root);
        while (rowIt.hasNext()) {
            List<SwiftNode> row = rowIt.next();
            for (SwiftNode n : row) {
                GroupNode node = (GroupNode) n;
                int dimensionIndex = node.getDepth();
                if (columns.get(dimensionIndex).getValue().isGlobalIndexed()) {
                    if (dictionaries.get(dimensionIndex) == null) {
                        dictionaries.set(dimensionIndex, new HashMap<Integer, Object>());
                    }
                    DictionaryEncodedColumn dict = columns.get(dimensionIndex).getKey().getDictionaryEncodedColumn();
                    int globalIndex = dict.getGlobalIndexByIndex(node.getDictionaryIndex());
                    if (dictionaries.get(dimensionIndex).containsKey(globalIndex)) {
                        continue;
                    }
                    Object value = dict.getValue(node.getDictionaryIndex());
                    dictionaries.get(dimensionIndex).put(globalIndex, value);
                    node.setGlobalIndex(globalIndex);
                }
            }
        }
        return dictionaries;
    }
}
