package com.fr.swift.query.group.by2.node.expander;

import com.fr.swift.query.group.by2.GroupByController;
import com.fr.swift.query.group.by2.PopUpCallback;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.row.RowIndexKey;

import java.util.List;
import java.util.Set;

/**
 * 表头展开
 * Created by Lyon on 2018/5/12.
 */
public class NodeNLevelExpanderController implements GroupByController<GroupNode> {

    private int nLevel;
    // TODO: 2018/5/12 手动点击展开没处理
    private Set<RowIndexKey<int[]>> expandIndexKeys;

    public NodeNLevelExpanderController(int nLevel) {
        this.nLevel = nLevel;
    }

    @Override
    public boolean isRow(List<GroupNode> entries, PopUpCallback callback) {
        if (entries.get(nLevel) != null) {
            callback.popUp();
            return true;
        }
        return false;
    }
}
