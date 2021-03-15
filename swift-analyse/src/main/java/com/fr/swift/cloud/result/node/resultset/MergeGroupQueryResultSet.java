package com.fr.swift.cloud.result.node.resultset;

import com.fr.swift.cloud.log.SwiftLoggers;
import com.fr.swift.cloud.query.aggregator.Aggregator;
import com.fr.swift.cloud.query.group.by2.node.GroupPage;
import com.fr.swift.cloud.result.GroupNode;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeUtils;
import com.fr.swift.cloud.result.SwiftResultSet;
import com.fr.swift.cloud.result.qrs.QueryResultSet;
import com.fr.swift.cloud.source.SwiftMetaData;
import com.fr.swift.cloud.util.IoUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Lyon
 * @date 2018/7/26
 */
public class MergeGroupQueryResultSet implements QueryResultSet<GroupPage> {
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
        lastRowOfPrevPages = new ArrayList<>(sources.size());
        for (int i = 0; i < sources.size(); i++) {
            lastRowOfPrevPages.add(null);
        }
    }

    private GroupPage updateAll() {
        final List<GroupPage> pages = new ArrayList<>();
        // 暂时不多线程跑，seg query和result query的合并都是用的这个，单机自己查自己会导致阻塞
        // 如先合并result query，占满了线程池，合并seg query的任务就提交不进去了
        for (int i = 0; i < sources.size(); i++) {
            QueryResultSet<GroupPage> resultSet = sources.get(i);
            try {
                if (resultSet.hasNextPage()) {
                    GroupPage pair = resultSet.getPage();
                    if (pair == null) {
                        SwiftLoggers.getLogger().error("MergeGroupQueryResultSet#updateAll: invalid page data!");
                        continue;
                    }
                    SwiftNode node = pair.getRoot();
                    lastRowOfPrevPages.set(i, SwiftNodeUtils.getLastRow(node));
                    pages.add(new GroupPage(node, pair.getGlobalDicts()));
                }
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
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
        List<GroupPage> newPages = new ArrayList<>();
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
            return new ArrayList<>(0);
        }
        List<Map<Integer, Object>> dictionary = new ArrayList<>(oldDictionary.size());
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
                if (i == dictionary.size()) {
                    dictionary.add(new HashMap<Integer, Object>());
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
