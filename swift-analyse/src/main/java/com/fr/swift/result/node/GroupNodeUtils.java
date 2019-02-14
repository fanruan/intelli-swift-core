package com.fr.swift.result.node;

import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/22.
 */
public class GroupNodeUtils {

    public static void updateNodeData(GroupNode root, final List<Map<Integer, Object>> dictionaries) {
        // 从计算结果中提取要展示的结果集
        Iterator<GroupNode> iterator = new MapperIterator<GroupNode, GroupNode>(SwiftNodeUtils.dftNodeIterator(root), new Function<GroupNode, GroupNode>() {
            @Override
            public GroupNode apply(GroupNode p) {
                // 设置节点的data
                if (p.getDepth() != -1 && dictionaries.get(p.getDepth()) != null) {
                    p.setData(dictionaries.get(p.getDepth()).get(p.getDictionaryIndex()));
                }
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
    }
}
