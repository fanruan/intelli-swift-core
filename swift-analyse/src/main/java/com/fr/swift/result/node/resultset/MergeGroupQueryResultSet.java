package com.fr.swift.result.node.resultset;

import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.Aggregator;
import com.fr.swift.query.group.by2.node.GroupPage;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.SwiftNode;
import com.fr.swift.result.SwiftNodeUtils;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.concurrent.PoolThreadFactory;
import com.fr.swift.util.concurrent.SwiftExecutors;

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
 * @author Lyon
 * @date 2018/7/26
 */
public class MergeGroupQueryResultSet implements QueryResultSet<GroupPage> {
    private static final ExecutorService EXEC = SwiftExecutors.newFixedThreadPool(new PoolThreadFactory(MergeGroupQueryResultSet.class));

    private int fetchSize;
    private boolean[] globalIndexed;
    private List<QueryResultSet<GroupPage>> sources;
    private GroupPageMerger pageMerger;
    private GroupPage remainResultSet;
    /**
     * 用于判断是否从源resultSet中更新数据。remainRowCount >= fetchSize不为空，否则为空
     */
    private List<SwiftNode> theRowOfRemainNode;
    private List<List<SwiftNode>> lastRowOfPrevPages;

    public MergeGroupQueryResultSet(int fetchSize, boolean[] globalIndexed, List<QueryResultSet<GroupPage>> sources,
                                    List<Aggregator> aggregators, List<Comparator<SwiftNode>> comparators) {
        this.fetchSize = fetchSize;
        this.globalIndexed = globalIndexed;
        this.sources = sources;
        this.pageMerger = new GroupPageMerger(aggregators, comparators);
        init();
    }

    private void init() {
        lastRowOfPrevPages = new ArrayList<List<SwiftNode>>(sources.size());
        for (int i = 0; i < sources.size(); i++) {
            lastRowOfPrevPages.add(null);
        }
    }

    private GroupPage updateAll() {
        final List<GroupPage> pages = new ArrayList<GroupPage>();
        List<Future<GroupPage>> pageFutures = new ArrayList<Future<GroupPage>>();
        for (int i = 0; i < sources.size(); i++) {
            final int finalI = i;
            pageFutures.add(EXEC.submit(new Callable<GroupPage>() {
                @Override
                public GroupPage call() {
                    if (sources.get(finalI).hasNextPage()) {
                        GroupPage pair = sources.get(finalI).getPage();
                        if (pair == null) {
                            SwiftLoggers.getLogger().error("MergeGroupQueryResultSet#updateAll: invalid page data!");
                            return null;
                        }
                        SwiftNode node = pair.getRoot();
                        lastRowOfPrevPages.set(finalI, SwiftNodeUtils.getLastRow(node));
                        return new GroupPage(node, pair.getGlobalDicts());
                    }
                    return null;
                }
            }));
        }
        for (Future<GroupPage> future : pageFutures) {
            try {
                GroupPage rs = future.get();
                if (rs != null) {
                    pages.add(rs);
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e.getMessage(), e);
            }
        }
        if (remainResultSet != null) {
            pages.add(remainResultSet);
        }
        GroupPage mergeResultSet = pageMerger.apply(pages);
        return getPage(mergeResultSet);
    }

    @Override
    public GroupPage getPage() {
        if (theRowOfRemainNode == null) {
            return updateAll();
        }
        List<GroupPage> newPages = new ArrayList<GroupPage>();
        for (int i = 0; i < sources.size(); i++) {
            if (sources.get(i).hasNextPage()) {
                if (shouldUpdate(lastRowOfPrevPages.get(i))) {
                    GroupPage pair = sources.get(i).getPage();
                    SwiftNode node = pair.getRoot();
                    List<Map<Integer, Object>> dict = pair.getGlobalDicts();
                    newPages.add(new GroupPage(node, dict));
                    List<SwiftNode> lastRow = SwiftNodeUtils.getLastRow(node);
                    lastRowOfPrevPages.set(i, lastRow);
                }
            }
        }
        GroupPage mergeResultSet;
        if (!newPages.isEmpty()) {
            newPages.add(remainResultSet);
            mergeResultSet = pageMerger.apply(newPages);
        } else {
            mergeResultSet = remainResultSet;
        }
        GroupPage page = getPage(mergeResultSet);
        if (SwiftNodeUtils.countRows(page.getRoot()) < fetchSize && hasNextPage()) {
            // 按照前面的规则更新了，但是不满一页，并且源结果集还有剩余，继续取下一页
            remainResultSet = page;
            return getPage();
        }
        return page;
    }

    private boolean shouldUpdate(List<SwiftNode> lastRowOfPage) {
        for (int i = 0; i < pageMerger.comparators.size(); i++) {
            if (pageMerger.comparators.get(i).compare(theRowOfRemainNode.get(i), lastRowOfPage.get(i)) > 0) {
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
    private GroupPage getPage(GroupPage mergeResultSet) {
        SwiftNode root = mergeResultSet.getRoot();
        SwiftNode[] nodes = SwiftNodeUtils.splitNode(root, 2, fetchSize);
        SwiftNode remainNode = null;
        theRowOfRemainNode = null;
        if (nodes[1] != null) {
            remainNode = nodes[1];
            if (SwiftNodeUtils.countRows(remainNode) >= fetchSize) {
                theRowOfRemainNode = SwiftNodeUtils.getRow(remainNode, fetchSize - 1);
            }
        }
        List<Map<Integer, Object>> oldDictionary = mergeResultSet.getGlobalDicts();
        remainResultSet = null;
        if (remainNode != null) {
            remainResultSet = new GroupPage(remainNode, getDictionary(remainNode, oldDictionary));
        }
        SwiftNode page = nodes[0];
        if (root.getChildrenSize() == 0) {
            page = root;
        }
        return new GroupPage(page, getDictionary(page, oldDictionary));
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
                if (!globalIndexed[i]) {
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
    public boolean hasNextPage() {
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
    public int getFetchSize() {
        return fetchSize;
    }

    public boolean[] getGlobalIndexed() {
        return globalIndexed;
    }

    public List<Aggregator> getAggragators() {
        return pageMerger.aggregators;
    }

    public List<Comparator<SwiftNode>> getComparators() {
        return pageMerger.comparators;
    }

    @Override
    public SwiftResultSet convert(SwiftMetaData metaData) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        IoUtil.close(sources);
    }
}
