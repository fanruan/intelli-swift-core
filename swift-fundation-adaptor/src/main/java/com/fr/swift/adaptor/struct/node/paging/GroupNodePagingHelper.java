package com.fr.swift.adaptor.struct.node.paging;

import com.finebi.conf.structure.result.table.BIGroupNode;
import com.fr.swift.adaptor.struct.node.impl.BIGroupNodeImpl;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.node.iterator.DFTGroupNodeIterator;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/5/20.
 */
public class GroupNodePagingHelper implements NodePagingHelper<BIGroupNode> {

    private int dimensionSize;
    private GroupNode root;
    private List<GroupNode> start = null;
    private List<GroupNode> end = null;

    public GroupNodePagingHelper(int dimensionSize, GroupNode root) {
        this.dimensionSize = dimensionSize;
        this.root = root;
    }

    @Override
    public BIGroupNode getPage(PagingInfo pageInfo) {
        if (pageInfo.isNextPage()) {

        }
        return null;
    }

    private static class CopyIt implements Iterator<List<GroupNode>> {

        private int[] cursor;
        private Expander expander;
        private Iterator<GroupNode> dftIterator;
        private LimitedStack<GroupNode> items;
        private List<GroupNode> next;

        public CopyIt(int dimensionSize, int[] cursor, Expander expander, GroupNode root, BIGroupNodeImpl copyRoot) {
            this.cursor = cursor;
            this.expander = expander;
            this.dftIterator = new DFTGroupNodeIterator(dimensionSize, root);
            this.items = new ProxyStack(dimensionSize, copyRoot);
            next = getNext();
        }

        private List<GroupNode> getNext() {
            List<GroupNode> ret = null;
            while (dftIterator.hasNext()) {
                GroupNode node = dftIterator.next();
                items.push(node);
                // TODO: 2018/5/20 expander判断是否为一行
                if (items.size() == items.limit()) {
                    ret = items.toList();
                    items.pop();
                    break;
                }
            }
            return ret;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public List<GroupNode> next() {
            List<GroupNode> ret = next;
            next = getNext();
            return ret;
        }

        @Override
        public void remove() {
        }
    }

    private static class ProxyStack implements LimitedStack<GroupNode> {

        private LimitedStack<GroupNode> stack;
        private BIGroupNodeImpl root;
        private BIGroupNodeImpl parent;

        public ProxyStack(int limit, BIGroupNodeImpl root) {
            this.stack = new ArrayLimitedStack<GroupNode>(limit);
            this.root = root;
            this.parent = root;
        }

        @Override
        public boolean isEmpty() {
            return stack.isEmpty();
        }

        @Override
        public int limit() {
            return stack.limit();
        }

        @Override
        public int size() {
            return stack.size();
        }

        @Override
        public void push(GroupNode item) {
            stack.push(item);
            BIGroupNodeImpl child = new BIGroupNodeImpl(item);
            parent.addChild(child);
            if (stack.size() != stack.limit()) {
                // 说明当前item可能作为父节点
                parent = child;
            }
        }

        @Override
        public GroupNode pop() {
            GroupNode tmp = stack.pop();
            if (!stack.isEmpty()) {
                parent = (BIGroupNodeImpl) parent.getParent();
            }
            return tmp;
        }

        @Override
        public GroupNode peek() {
            return stack.peek();
        }

        @Override
        public List<GroupNode> toList() {
            return stack.toList();
        }
    }
}
