package com.fr.swift.adaptor.struct.node.paging;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.adaptor.struct.node.cache.PagingSessionCacheManager;
import com.fr.swift.adaptor.struct.node.impl.BIGroupNodeImpl;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.query.group.info.cursor.ExpanderType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.node.iterator.DFTGroupNodeIterator;
import com.fr.swift.structure.Pair;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Lyon on 2018/5/20.
 */
public class GroupNodePagingHelper implements NodePagingHelper {

    private int dimensionSize;
    private GroupNode root;
    private NodeResultSet resultSet;

    public GroupNodePagingHelper(int dimensionSize, NodeResultSet resultSet) {
        this.dimensionSize = dimensionSize;
        this.root = (GroupNode) resultSet.getNode();
        this.resultSet = resultSet;
    }

    @Override
    public Pair<BIGroupNode, PagingSession> getPage(PagingInfo pagingInfo) {
        PagingSession pagingSession = PagingSessionCacheManager.getInstance().get(pagingInfo.getPagingSessionId());
        if (pagingSession == null) {
            pagingSession = new PagingSession();
        }
        int[] cursor = getCursor(pagingInfo, pagingSession);
        Pair<BIGroupNodeImpl, Pair<int[], int[]>> pair = copyNode(pagingInfo, cursor);
        if (pagingInfo.isNextPage()) {
            pagingSession.setPrevPageStartCursor(pagingSession.getLastRowOfPage());
            pagingSession.setFirstRowOfPage(pagingSession.getNextPageStartCursor());
            pagingSession.setLastRowOfPage(pair.getValue().getKey());
            pagingSession.setNextPageStartCursor(pair.getValue().getValue());
        } else {
            pagingSession.setNextPageStartCursor(pagingSession.getFirstRowOfPage());
            pagingSession.setLastRowOfPage(pagingSession.getPrevPageStartCursor());
            pagingSession.setFirstRowOfPage(pair.getValue().getKey());
            pagingSession.setPrevPageStartCursor(pair.getValue().getValue());
        }
        PagingSessionCacheManager.getInstance().cache(pagingInfo.getPagingSessionId(), pagingSession);
        return Pair.of((BIGroupNode) pair.getKey(), pagingSession);
    }

    private int[] getCursor(PagingInfo pagingInfo, PagingSession pagingSession) {
        if (pagingInfo.isFirstPage()) {
            pagingSession.setNextPageStartCursor(new int[dimensionSize]);
            pagingSession.setPrevPageStartCursor(null);
            pagingSession.setFirstRowOfPage(null);
            pagingSession.setLastRowOfPage(null);
        }
        return pagingInfo.isNextPage() ? pagingSession.getNextPageStartCursor() : pagingSession.getPrevPageStartCursor();
    }

    private Pair<BIGroupNodeImpl, Pair<int[], int[]>> copyNode(PagingInfo pagingInfo, int[] cursor) {
        Iterator<GroupNode> dftIterator = new DFTGroupNodeIterator(pagingInfo.isNextPage(), dimensionSize, root, cursor);
        BIGroupNodeImpl[] cacheNode = new BIGroupNodeImpl[dimensionSize + 1];
        BIGroupNodeImpl copyRoot = new BIGroupNodeImpl(pagingInfo.isNextPage(), dftIterator.next());
        int childIndex = 0;
        cacheNode[childIndex] = copyRoot;
        int rowCount = 0;
        while (dftIterator.hasNext() && rowCount < pagingInfo.getPageSize()) {
            GroupNode node = dftIterator.next();
            childIndex = node.getDepth() + 1;
            cacheNode[childIndex] = new BIGroupNodeImpl(pagingInfo.isNextPage(), node);
            if (isChild(childIndex, cacheNode, pagingInfo.getExpander())) {
                cacheNode[node.getDepth()].addChild(cacheNode[childIndex]);
            }
            if (isRow(childIndex, cacheNode, pagingInfo.getExpander())) {
                rowCount++;
            }
        }
        // 取最后一行的游标
        int[] lastRow = getRowCursor(childIndex, cacheNode);
        // 取下一行的游标
        int[] next = null;
        if (dftIterator.hasNext()) {
            GroupNode node = dftIterator.next();
            childIndex = node.getDepth() + 1;
            cacheNode[childIndex] = new BIGroupNodeImpl(pagingInfo.isNextPage(), node);
            next = getRowCursor(childIndex, cacheNode);
        }
        return Pair.of(copyRoot, Pair.of(lastRow, next));
    }

    private static int[] getRowCursor(int childIndex, BIGroupNodeImpl[] cacheNode) {
        int[] cursor = new int[cacheNode.length - 1];
        Arrays.fill(cursor, DFTGroupNodeIterator.DEFAULT_START_INDEX);
        for (int i = 1; i <= childIndex; i++) {
            if (cacheNode[i] == null) {
                continue;
            }
            cursor[i - 1] = cacheNode[i].getIndex();
        }
        return cursor.length > 0 && cursor[0] == DFTGroupNodeIterator.DEFAULT_START_INDEX ? null : cursor;
    }

    private static boolean isRow(int childIndex, BIGroupNodeImpl[] cacheNode, Expander expander) {
        // TODO: 2018/5/21 通过expander来判断[node.getData() : node in cacheNode[1:]]是否为一行
        if (expander.getType() == ExpanderType.LAZY_EXPANDER && expander.getStringIndexKeys().isEmpty()) {
            return true;
        }
        return childIndex == cacheNode.length - 1;
    }

    private static boolean isChild(int childIndex, BIGroupNodeImpl[] cacheNode, Expander expander) {
        // TODO: 2018/5/21 通过expander来判断是否要添加child
        return true;
    }

    @Override
    public <T extends NodeResultSet> T getNodeResultSet() {
        return (T) resultSet;
    }
}
