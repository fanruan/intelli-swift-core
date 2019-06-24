package com.fr.swift.result.node.resultset;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeQRSImpl;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


/**
 * Created by Lyon on 2018/7/26.
 */
class NodeResultSetMerger implements Iterator<QueryResultSet<GroupPage>> {

    private static ExecutorService service = SwiftExecutors.newFixedThreadPool(10, new PoolThreadFactory(NodeResultSetMerger.class));

    private int fetchSize;
    private boolean[] isGlobalIndexed;
    private List<QueryResultSet<GroupPage>> sources;
    private List<Comparator<SwiftNode>> comparators;
    private Function<List<QueryResultSet<GroupPage>>, QueryResultSet<GroupPage>> operator;
    private QueryResultSet<GroupPage> remainResultSet;
    /**
     * 用于判断是否从源resultSet中更新数据。remainRowCount >= fetchSize不为空，否则为空
     */
    private List<SwiftNode> theRowOfRemainNode;
    private List<List<SwiftNode>> lastRowOfPrevPages;

    NodeResultSetMerger(int fetchSize, boolean[] isGlobalIndexed, List<QueryResultSet<GroupPage>> sources,
                        List<Aggregator> aggregators, List<Comparator<SwiftNode>> comparators) {
        this.fetchSize = fetchSize;
        this.isGlobalIndexed = isGlobalIndexed;
        this.sources = sources;
        this.comparators = comparators;
        this.operator = new MergeOperator(fetchSize, aggregators, comparators);
        init();
    }

    private void init() {
        lastRowOfPrevPages = new ArrayList<List<SwiftNode>>(sources.size());
        for (int i = 0; i < sources.size(); i++) {
            lastRowOfPrevPages.add(null);
        }
    }

    private QueryResultSet<GroupPage> updateAll() {
        final List<QueryResultSet<GroupPage>> resultSets = new ArrayList<QueryResultSet<GroupPage>>();
        List<Future<QueryResultSet<GroupPage>>> futures = new ArrayList<Future<QueryResultSet<GroupPage>>>();
        for (int i = 0; i < sources.size(); i++) {
            final int finalI = i;
            futures.add(service.submit(new Callable<QueryResultSet<GroupPage>>() {
                @Override
                public QueryResultSet<GroupPage> call() {
                    if (sources.get(finalI).hasNextPage()) {
                        GroupPage pair = sources.get(finalI).getPage();
                        if (pair == null) {
                            SwiftLoggers.getLogger().error("NodeResultSetMerger#updateAll: invalid page data!");
                            return null;
                        }
                        SwiftNode node = pair.getRoot();
                        lastRowOfPrevPages.set(finalI, SwiftNodeUtils.getLastRow(node));
                        return new NodeMergeQRSImpl(fetchSize, node, pair.getGlobalDicts());
                    }
                    return null;
                }
            }));
        }
        for (Future<QueryResultSet<GroupPage>> future : futures) {
            try {
                QueryResultSet<GroupPage> rs = future.get();
                if (rs != null) {
                    resultSets.add(rs);
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        if (remainResultSet != null) {
            resultSets.add(remainResultSet);
        }
        QueryResultSet<GroupPage> mergeResultSet = operator.apply(resultSets);
        return getPage(mergeResultSet);
    }

    private QueryResultSet<GroupPage> getNext() {
        if (theRowOfRemainNode == null) {
            return updateAll();
        }
        List<QueryResultSet<GroupPage>> newPages = new ArrayList<QueryResultSet<GroupPage>>();
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).hasNextPage()) {
                if (shouldUpdate(lastRowOfPrevPages.get(i))) {
                    GroupPage pair = sources.get(i).getPage();
                    SwiftNode node = pair.getRoot();
                    List<Map<Integer, Object>> dict = pair.getGlobalDicts();
                    newPages.add(new NodeMergeQRSImpl(fetchSize, node, dict));
                    List<SwiftNode> lastRow = SwiftNodeUtils.getLastRow(node);
                    lastRowOfPrevPages.set(i, lastRow);
                }
            }
        }
        QueryResultSet<GroupPage> mergeResultSet;
        if (!newPages.isEmpty()) {
            newPages.add(remainResultSet);
            mergeResultSet = operator.apply(newPages);
        } else {
            mergeResultSet = remainResultSet;
        }
        QueryResultSet<GroupPage> page = getPage(mergeResultSet);
        if (SwiftNodeUtils.countRows(page.getPage().getRoot()) < fetchSize && hasNext()) {
            // 按照前面的规则更新了，但是不满一页，并且源结果集还有剩余，继续取下一页
            remainResultSet = page;
            return getNext();
        }
        return page;
    }

    private boolean shouldUpdate(List<SwiftNode> lastRowOfPage) {
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
    private QueryResultSet<GroupPage> getPage(QueryResultSet<GroupPage> mergeResultSet) {
        GroupPage pair = mergeResultSet.getPage();
        SwiftNode root = pair.getRoot();
        SwiftNode[] nodes = SwiftNodeUtils.splitNode(root, 2, fetchSize);
        SwiftNode remainNode = null;
        theRowOfRemainNode = null;
        if (nodes[1] != null) {
            remainNode = nodes[1];
            if (SwiftNodeUtils.countRows(remainNode) >= fetchSize) {
                theRowOfRemainNode = SwiftNodeUtils.getRow(remainNode, fetchSize - 1);
            }
        }
        List<Map<Integer, Object>> oldDictionary = pair.getGlobalDicts();
        remainResultSet = null;
        if (remainNode != null) {
            remainResultSet = new NodeMergeQRSImpl(fetchSize, remainNode, getDictionary(remainNode, oldDictionary));
        }
        SwiftNode page = nodes[0];
        if (root.getChildrenSize() == 0) {
            page = root;
        }
        return new NodeMergeQRSImpl(fetchSize, page, getDictionary(page, oldDictionary));
    }

    private List<Map<Integer, Object>> getDictionary(SwiftNode root, List<Map<Integer, Object>> oldDictionary) {
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
        for (QueryResultSet<GroupPage> resultSet : sources) {
            if (resultSet.hasNextPage()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public QueryResultSet<GroupPage> next() {
        return getNext();
    }

    @Override
    public void remove() {

    }
}
