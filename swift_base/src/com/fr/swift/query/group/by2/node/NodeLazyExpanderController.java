package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.group.by2.GroupByController;
import com.fr.swift.query.group.by2.PopUpCallback;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.row.RowIndexKey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeLazyExpanderController implements GroupByController<GroupNode> {

    private Set<RowIndexKey<int[]>> expandIndexKeys;

    public NodeLazyExpanderController(Set<RowIndexKey<int[]>> indexKeys) {
        expandIndexKey(indexKeys);
    }

    private void expandIndexKey(Set<RowIndexKey<int[]>> indexKeys) {
        expandIndexKeys = new HashSet<RowIndexKey<int[]>>();
        for (RowIndexKey<int[]> indexKey : indexKeys) {
            int[] indexes = indexKey.getKey();
            int[] expandIndexes = new int[indexes.length];
            Arrays.fill(expandIndexes, -1);
            for (int i = 0; i < indexes.length; i++) {
                assert indexes[0] != -1;
                expandIndexes[i] = indexes[i];
                // [1, 2, 3] => [1, -1, -1], [1, 2, -1]都要展开
                expandIndexKeys.add(new RowIndexKey<int[]>(Arrays.copyOf(expandIndexes, expandIndexes.length)));
            }
        }
    }

    @Override
    public boolean isRow(List<GroupNode> entries, PopUpCallback callback) {
        int[] indexes = NodeAllExpanderController.getRowIndex(entries);
        if (expandIndexKeys.contains(new RowIndexKey<int[]>(indexes))) {
            // 当前行要继续展开，不是完整的一行
            return false;
        }
        callback.popUp();
        return true;
    }
}
