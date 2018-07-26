package com.fr.swift.result.node;

import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeMergeResultSetImpl;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.node.iterator.DFTGroupNodeIterator;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/7/26.
 */
class NodeResultSetMerger implements Iterator<NodeMergeResultSet<GroupNode>> {

    private int fetchSize;
    private List<NodeMergeResultSet<GroupNode>> sources;
    private List<Comparator<GroupNode>> comparators;
    private Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>> operator;
    private int remainRowCount = 0;
    private NodeMergeResultSet<GroupNode> remainResultSet;
    // 用于判断是否从源resultSet中更新数据。remainRowCount >= fetchSize不为空，否则为空
    private List<GroupNode> theRowOfRemainNode;
    private List<List<GroupNode>> lastRowOfPrevPages;

    NodeResultSetMerger(int fetchSize, List<NodeMergeResultSet<GroupNode>> sources,
                        List<Comparator<GroupNode>> comparators,
                        Function<List<NodeMergeResultSet<GroupNode>>, NodeMergeResultSet<GroupNode>> operator) {
        this.fetchSize = fetchSize;
        this.sources = sources;
        this.comparators = comparators;
        this.operator = operator;
        init();
    }

    private void init() {
        lastRowOfPrevPages = new ArrayList<List<GroupNode>>(sources.size());
        for (int i = 0; i < sources.size(); i++) {
            lastRowOfPrevPages.add(null);
        }
    }

    private NodeMergeResultSet<GroupNode> updateAll() {
        List<NodeMergeResultSet<GroupNode>> resultSets = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (NodeMergeResultSet<GroupNode> source : sources) {
            if (source.hasNextPage()) {
                resultSets.add(new NodeMergeResultSetImpl<GroupNode>(fetchSize, (GroupNode) source.getNode(), source.getRowGlobalDictionaries()));
            }
        }
        NodeMergeResultSet<GroupNode> mergeResultSet = operator.apply(resultSets);
        return getPage(mergeResultSet);
    }

    private NodeMergeResultSet<GroupNode> getNext() {
        if (theRowOfRemainNode == null) {
            return updateAll();
        }
        List<NodeMergeResultSet<GroupNode>> newPages = new ArrayList<NodeMergeResultSet<GroupNode>>();
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).hasNextPage()) {
                if (shouldUpdate(lastRowOfPrevPages.get(i))) {
                    GroupNode node = (GroupNode) sources.get(i).getNode();
                    List<Map<Integer, Object>> dict = sources.get(i).getRowGlobalDictionaries();
                    newPages.add(new NodeMergeResultSetImpl<GroupNode>(fetchSize, node, dict));
                    List<GroupNode> lastRow = SwiftNodeUtils.getLastRow(node);
                    lastRowOfPrevPages.set(i, lastRow);
                }
            }
        }
        NodeMergeResultSet<GroupNode> mergeResultSet;
        if (!newPages.isEmpty()) {
            newPages.add(remainResultSet);
            mergeResultSet = operator.apply(newPages);
        } else {
            mergeResultSet = remainResultSet;
        }
        // TODO: 2018/7/26 按照前面的规则更新了，但是不满一页，并且源结果集还有剩余，继续取下一页
        return getPage(mergeResultSet);
    }

    private boolean shouldUpdate(List<GroupNode> lastRowOfPage) {
        for (int i = 0; i < comparators.size(); i++) {
            if (comparators.get(i).compare(theRowOfRemainNode.get(i), lastRowOfPage.get(i)) > 0) {
                return true;
            }
        }
        return false;
    }

    private NodeMergeResultSet<GroupNode> getPage(NodeMergeResultSet<GroupNode> mergeResultSet) {
        // TODO: 2018/7/26 从合并结果中取出一页，并把剩余node和字典保存到remainResultSet
        // TODO: 2018/7/26 复制node
        GroupNode root = (GroupNode) mergeResultSet.getNode();
        int dimensionSize = SwiftNodeUtils.getDimensionSize(root);
        Iterator<GroupNode> iterator = new DFTGroupNodeIterator(dimensionSize, root);
        iterator.next();    // 跳过root
        GroupNode[] cachedNodes = new GroupNode[dimensionSize];
        copy(cachedNodes, iterator);
        GroupNode page = cachedNodes[0];
        GroupNode remainNode = null;
        theRowOfRemainNode = null;
        if (iterator.hasNext()) {
            theRowOfRemainNode = SwiftNodeUtils.getLastRow(page);
            copy(cachedNodes, iterator);
            remainNode = cachedNodes[0];
        }
        // TODO: 2018/7/26 取出字典
        if (remainNode != null) {
            remainResultSet = new NodeMergeResultSetImpl<GroupNode>(fetchSize, remainNode, null);
        }
        return new NodeMergeResultSetImpl<GroupNode>(fetchSize, page, null);
    }

    private void copy(GroupNode[] cachedNodes, Iterator<GroupNode> iterator) {
        int rowCount = 0;
        boolean uninitialized = true;
        while (iterator.hasNext() && rowCount < fetchSize) {
            GroupNode node = iterator.next();
            int depth = node.getDepth();
            if (uninitialized) {
                cachedNodes = newCacheNodes(cachedNodes, depth);
                uninitialized = false;
            }
            GroupNode copy = new GroupNode(depth, node.getData());
            copy.setGlobalIndex(node.getDictionaryIndex());
            copy.setAggregatorValue(node.getAggregatorValue());
            cachedNodes[depth].addChild(copy);
            if (depth < cachedNodes.length - 1) {
                cachedNodes[depth + 1] = copy;
            } else {
                rowCount++;
            }
        }
    }

    private GroupNode[] newCacheNodes(GroupNode[] cachedNodes, int index) {
        GroupNode[] newCachedNodes = new GroupNode[cachedNodes.length];
        newCachedNodes[0] = new GroupNode(-1, null);
        for (int i = 0; i < index; i++) {
            GroupNode child = new GroupNode(i, cachedNodes[i + 1].getDictionaryIndex());
            child.setData(cachedNodes[i + 1].getData());
            newCachedNodes[i + 1] = child;
            newCachedNodes[i].addChild(child);
        }
        return newCachedNodes;
    }

    @Override
    public boolean hasNext() {
        if (remainRowCount > 0) {
            return true;
        }
        for (NodeMergeResultSet resultSet : sources) {
            if (resultSet.hasNextPage()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NodeMergeResultSet<GroupNode> next() {
        return getNext();
    }

    @Override
    public void remove() {

    }
}
