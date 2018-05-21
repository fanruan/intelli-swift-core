package com.fr.swift.adaptor.struct.node.paging;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.adaptor.struct.node.impl.BIGroupNodeImpl;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.node.iterator.DFTGroupNodeIterator;
import com.fr.swift.structure.Pair;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Lyon on 2018/5/20.
 */
public class GroupNodePagingHelper implements NodePagingHelper<BIGroupNode> {

    private int dimensionSize;
    private GroupNode root;
    // 前一页的开始游标
    private int[] prevPageStartCursor = null;
    // 后一页的开始游标
    private int[] nextPageStartCursor = null;
    // 向前翻页的第一行（再向前翻一页之后就是下一页的第一行）
    private int[] firstRowOfPrevPage = null;
    // 向后翻页的最后一行（再向后翻一页之后就是前一页的最后一行）
    private int[] lastRowOfNextPage = null;

    public GroupNodePagingHelper(int dimensionSize, GroupNode root) {
        this.dimensionSize = dimensionSize;
        this.root = root;
    }

    @Override
    public BIGroupNode getPage(PagingInfo pagingInfo) {
        int[] cursor = getCursor(pagingInfo);
        Pair<BIGroupNodeImpl, Pair<int[], int[]>> pair = copyNode(pagingInfo, cursor);
        if (pagingInfo.isNextPage()) {
            prevPageStartCursor = lastRowOfNextPage;
            lastRowOfNextPage = pair.getValue().getKey();
            nextPageStartCursor = pair.getValue().getValue();
        } else {
            nextPageStartCursor = firstRowOfPrevPage;
            firstRowOfPrevPage = pair.getValue().getKey();
            prevPageStartCursor = pair.getValue().getValue();
        }
        return pair.getKey();
    }

    private int[] getCursor(PagingInfo pagingInfo) {
        if (pagingInfo.isNextPage()) {
            return nextPageStartCursor == null ? new int[dimensionSize] : nextPageStartCursor;
        }
        assert prevPageStartCursor != null;
        return prevPageStartCursor;
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
        for (int i = 0; i <= childIndex; i++) {
            cursor[i] = cacheNode[i + 1].getIndex();
        }
        return cursor;
    }

    private static boolean isRow(int childIndex, BIGroupNodeImpl[] cacheNode, Expander expander) {
        // TODO: 2018/5/21 通过expander来判断[node.getData() : node in cacheNode[1:]]是否为一行
        return childIndex == cacheNode.length - 1;
    }

    private static boolean isChild(int childIndex, BIGroupNodeImpl[] cacheNode, Expander expander) {
        // TODO: 2018/5/21 通过expander来判断是否要添加child
        return childIndex != cacheNode.length - 1;
    }
}
