package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.group.by.GroupByEntry;
import com.fr.swift.query.group.info.IndexInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.function.Function;

import java.util.List;

/**
 * Created by Lyon on 2018/7/25.
 */
class ItemMapper implements Function<Pair<Integer, GroupByEntry>, GroupNode> {

    private List<Pair<Column, IndexInfo>> columns;

    public ItemMapper(List<Pair<Column, IndexInfo>> columns) {
        this.columns = columns;
    }

    @Override
    public GroupNode apply(Pair<Integer, GroupByEntry> p) {
        int depth = p.getKey();
        if (columns.get(depth).getValue().isGlobalIndexed()) {
            return new GroupNode(depth, p.getValue().getIndex());
        } else {
            DictionaryEncodedColumn dict = columns.get(depth).getKey().getDictionaryEncodedColumn();
            return new GroupNode(depth, dict.getValue(p.getValue().getIndex()));
        }
    }
}
