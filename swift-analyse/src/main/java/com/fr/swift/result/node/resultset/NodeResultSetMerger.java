package com.fr.swift.result.node.resultset;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRS;
import com.fr.swift.result.NodeMergeQRSImpl;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.structure.Pair;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * Created by Lyon on 2018/7/26.
 */
class NodeResultSetMerger implements Iterator<NodeMergeQRS<GroupNode>> {

    private static ExecutorService service = SwiftExecutors.newFixedThreadPool(10, new PoolThreadFactory(NodeResultSetMerger.class));

    private int fetchSize;
    private boolean[] isGlobalIndexed;
    private List<NodeMergeQRS<GroupNode>> sources;
    private List<Comparator<GroupNode>> comparators;
    private Function<List<NodeMergeQRS<GroupNode>>, NodeMergeQRS<GroupNode>> operator;
    private NodeMergeQRS<GroupNode> remainResultSet;
    /**
     * 用于判断是否从源resultSet中更新数据。remainRowCount >= fetchSize不为空，否则为空
     */
    private List<GroupNode> theRowOfRemainNode;
    private List<List<GroupNode>> lastRowOfPrevPages;

    NodeResultSetMerger(int fetchSize, boolean[] isGlobalIndexed, List<NodeMergeQRS<GroupNode>> sources,
                        List<Aggregator> aggregators, List<Comparator<GroupNode>> comparators) {
        this.fetchSize = fetchSize;
        this.isGlobalIndexed = isGlobalIndexed;
        this.sources = sources;
        this.comparators = comparators;
        this.operator = new MergeOperator(fetchSize, aggregators, comparators);
        init();
    }

    private void init() {
        lastRowOfPrevPages = new ArrayList<List<GroupNode>>(sources.size());
        for (int i = 0; i < sources.size(); i++) {
            lastRowOfPrevPages.add(null);
        }
    }

    private NodeMergeQRS<GroupNode> updateAll() {
        final List<NodeMergeQRS<GroupNode>> resultSets = new ArrayList<NodeMergeQRS<GroupNode>>();
        List<Future> futures = new ArrayList<Future>();
        for (int i = 0; i < sources.size(); i++) {
            final int finalI = i;
            futures.add(service.submit(new Runnable() {
                @Override
                public void run() {
                    if (sources.get(finalI).hasNextPage()) {
                        Pair<GroupNode, List<Map<Integer, Object>>> pair = sources.get(finalI).getPage();
                        if (pair == null) {
                            SwiftLoggers.getLogger().error("NodeResultSetMerger#updateAll: invalid page data!");
                            return;
                        }
                        GroupNode node = pair.getKey();
                        resultSets.add(new NodeMergeQRSImpl<GroupNode>(fetchSize, node, pair.getValue()));
                        lastRowOfPrevPages.set(finalI, SwiftNodeUtils.getLastRow(node));
                    }
                }
            }));
        }
        for (Future future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        if (remainResultSet != null) {
            resultSets.add(remainResultSet);
        }
        NodeMergeQRS<GroupNode> mergeResultSet = operator.apply(resultSets);
        return getPage(mergeResultSet);
    }

    private NodeMergeQRS<GroupNode> getNext() {
        if (theRowOfRemainNode == null) {
            return updateAll();
        }
        List<NodeMergeQRS<GroupNode>> newPages = new ArrayList<NodeMergeQRS<GroupNode>>();
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).hasNextPage()) {
                if (shouldUpdate(lastRowOfPrevPages.get(i))) {
                    Pair<GroupNode, List<Map<Integer, Object>>> pair = sources.get(i).getPage();
                    GroupNode node = pair.getKey();
                    List<Map<Integer, Object>> dict = pair.getValue();
                    newPages.add(new NodeMergeQRSImpl<GroupNode>(fetchSize, node, dict));
                    List<GroupNode> lastRow = SwiftNodeUtils.getLastRow(node);
                    lastRowOfPrevPages.set(i, lastRow);
                }
            }
        }
        NodeMergeQRS<GroupNode> mergeResultSet;
        if (!newPages.isEmpty()) {
            newPages.add(remainResultSet);
            mergeResultSet = operator.apply(newPages);
        } else {
            mergeResultSet = remainResultSet;
        }
        NodeMergeQRS<GroupNode> page = getPage(mergeResultSet);
        if (SwiftNodeUtils.countRows(page.getPage().getKey()) < fetchSize && hasNext()) {
            // 按照前面的规则更新了，但是不满一页，并且源结果集还有剩余，继续取下一页
            remainResultSet = page;
            return getNext();
        }
        return page;
    }

    private boolean shouldUpdate(List<GroupNode> lastRowOfPage) {
        for (int i = 0; i < comparators.size(); i++) {
            if (comparators.get(i).compare(theRowOfRemainNode.get(i), lastRowOfPage.get(i)) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将合并出来的结果拆分成两份，拆分node和字典
     *
     * @param mergeResultSet
     * @return
     */
    private NodeMergeQRS<GroupNode> getPage(NodeMergeQRS<GroupNode> mergeResultSet) {
        Pair<GroupNode, List<Map<Integer, Object>>> pair = mergeResultSet.getPage();
        GroupNode root = pair.getKey();
        GroupNode[] nodes = SwiftNodeUtils.splitNode(root, 2, fetchSize);
        GroupNode remainNode = null;
        theRowOfRemainNode = null;
        if (nodes[1] != null) {
            remainNode = nodes[1];
            if (SwiftNodeUtils.countRows(remainNode) >= fetchSize) {
                theRowOfRemainNode = SwiftNodeUtils.getRow(remainNode, fetchSize - 1);
            }
        }
        List<Map<Integer, Object>> oldDictionary = pair.getValue();
        remainResultSet = null;
        if (remainNode != null) {
            remainResultSet = new NodeMergeQRSImpl<GroupNode>(fetchSize, remainNode, getDictionary(remainNode, oldDictionary));
        }
        GroupNode page = nodes[0];
        if (root.getChildrenSize() == 0) {
            page = root;
        }
        return new NodeMergeQRSImpl<GroupNode>(fetchSize, page, getDictionary(page, oldDictionary));
    }

    private List<Map<Integer, Object>> getDictionary(GroupNode root, List<Map<Integer, Object>> oldDictionary) {
        if (root == null || root.getChildrenSize() == 0) {
            return new ArrayList<Map<Integer, Object>>(0);
        }
        List<Map<Integer, Object>> dictionary = new ArrayList<Map<Integer, Object>>(oldDictionary.size());
        for (int i = 0; i < oldDictionary.size(); i++) {
            dictionary.add(null);
        }
        Iterator<List<SwiftNode>> rows = SwiftNodeUtils.node2RowListIterator(root);
        while (rows.hasNext()) {
            List<SwiftNode> row = rows.next();
            for (int i = 0; i < row.size(); i++) {
                GroupNode node = (GroupNode) row.get(i);
                if (!isGlobalIndexed[i]) {
                    continue;
                }
                if (dictionary.get(i) == null) {
                    dictionary.set(i, new HashMap<Integer, Object>());
                }
                dictionary.get(i).put(node.getDictionaryIndex(), oldDictionary.get(i).get(node.getDictionaryIndex()));
            }
        }
        return dictionary;
    }

    @Override
    public boolean hasNext() {
        if (remainResultSet != null) {
            return true;
        }
        for (NodeMergeQRS resultSet : sources) {
            if (resultSet.hasNextPage()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NodeMergeQRS<GroupNode> next() {
        return getNext();
    }

    @Override
    public void remove() {

    }
}
