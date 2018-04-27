package com.fr.swift.query.group.by2.node;

import com.fr.swift.query.group.by2.GroupByController;
import com.fr.swift.query.group.by2.PopUpCallback;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.row.RowIndexKey;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Lyon on 2018/4/27.
 */
public class NodeAllExpanderController implements GroupByController<GroupNode> {

    private Set<RowIndexKey<int[]>> indexKeys;

    public NodeAllExpanderController(Set<RowIndexKey<int[]>> indexKeys) {
        this.indexKeys = indexKeys;
    }

    @Override
    public boolean isRow(List<GroupNode> entries, PopUpCallback callback) {
        if (entries.get(entries.size() - 1) != null) {
            // 全部展开情况下普通的一行
            return true;
        }
        // 下面处理全部展开的情况下，个别节点不展开的情况
        int[] indexes = getRowIndex(entries);
        if (indexKeys.contains(new RowIndexKey<int[]>(indexes))) {
            // 遇到一个收起的节点
            callback.pop();
            return true;
        }
        return false;
    }

    static int[] getRowIndex(List<GroupNode> entries) {
        int[] indexes = new int[entries.size()];
        Arrays.fill(indexes, -1);
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i) != null) {
                indexes[i] = entries.get(i).getDictionaryIndex();
            }
        }
        return indexes;
    }
}
