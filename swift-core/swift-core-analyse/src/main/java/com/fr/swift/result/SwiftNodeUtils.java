package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.structure.iterator.Tree2RowIterator;
import com.fr.swift.structure.stack.ArrayLimitedStack;
import com.fr.swift.structure.stack.LimitedStack;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Lyon on 2018/6/13.
 */
public class SwiftNodeUtils {

    public static <T extends SwiftNode> List<T> getRow(T root, int index) {
        return (List<T>) IteratorUtils.iterator2List(node2RowListIterator(root)).get(index);
    }

    public static <T extends SwiftNode> List<T> getLastRow(T root) {
        int dimensionSize = getDimensionSize(root);
        T prev = root;
        List<T> row = new ArrayList<T>();
        for (int i = 0; i < dimensionSize; i++) {
            T node = (T) prev.getChild(prev.getChildrenSize() - 1);
            row.add(node);
            prev = node;
        }
        return row;
    }

    public static int getDimensionSize(SwiftNode root) {
        int size = 0;
        SwiftNode tmp = root;
        while (tmp.getChildrenSize() > 0) {
            size++;
            tmp = tmp.getChild(0);
        }
        return size;
    }

    public static int countRows(SwiftNode root) {
        return IteratorUtils.iterator2List(node2RowListIterator(root)).size();
    }

    public static Iterator<List<SwiftNode>> node2RowListIterator(SwiftNode root) {
        return new Tree2RowIterator<SwiftNode>(getDimensionSize(root), root.getChildren().iterator(), new Function<SwiftNode, Iterator<SwiftNode>>() {
            @Override
            public Iterator<SwiftNode> apply(SwiftNode p) {
                return p.getChildren().iterator();
            }
        });
    }

    public static Iterator<Row> node2RowIterator(SwiftNode root) {
        if (getDimensionSize(root) == 0) {
            Row row = new ListBasedRow(aggValue2Object(root.getAggregatorValue()));
            return Collections.singletonList(row).iterator();
        }
        Iterator<List<SwiftNode>> iterator = node2RowListIterator(root);
        return new MapperIterator<List<SwiftNode>, Row>(iterator, new Function<List<SwiftNode>, Row>() {
            @Override
            public Row apply(List<SwiftNode> p) {
                return nodes2Row(p);
            }
        });
    }

    public static Row nodes2Row(List<SwiftNode> row) {
        List<Object> data = new ArrayList<Object>();
        if (null != row) {
            for (SwiftNode col : row) {
                if (null != col) {
                    data.add(col.getData());
                } else {
                    data.add(null);
                }
            }
        }
        if (null != row) {
            SwiftNode leafNode = row.get(row.size() - 1);
            AggregatorValue[] values = leafNode.getAggregatorValue();
            values = values == null ? new AggregatorValue[0] : values;
            data.addAll(aggValue2Object(values));
        }
        return new ListBasedRow(data);
    }

    private static List<Object> aggValue2Object(AggregatorValue[] values) {
        List<Object> objects = new ArrayList<Object>();
        if (values != null) {
            for (AggregatorValue value : values) {
                objects.add(value == null ? null : value.calculateValue());
            }
        }
        return objects;
    }

    public static GroupNode[] splitNode(GroupNode root, int numberOfNodes, int rowCount) {
        Iterator<GroupNode> iterator = SwiftNodeUtils.dftNodeIterator(root);
        iterator.next();    // 跳过root
        GroupNode[] cachedNodes = new GroupNode[getDimensionSize(root)];
        GroupNode[] result = new GroupNode[numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            if (i == numberOfNodes - 1) {
                copy(cachedNodes, iterator, Integer.MAX_VALUE);
            } else {
                copy(cachedNodes, iterator, rowCount);
            }
            result[i] = cachedNodes[0].getChildrenSize() == 0 ? null : cachedNodes[0];
        }
        return result;
    }

    private static void copy(GroupNode[] cachedNodes, Iterator<GroupNode> iterator, int size) {
        int rowCount = 0;
        boolean uninitialized = true;
        while (iterator.hasNext() && rowCount < size) {
            GroupNode node = iterator.next();
            int depth = node.getDepth();
            if (uninitialized) {
                newCacheNodes(cachedNodes, depth);
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

    private static void newCacheNodes(GroupNode[] cachedNodes, int index) {
        GroupNode[] newCachedNodes = new GroupNode[cachedNodes.length];
        newCachedNodes[0] = new GroupNode(-1, null);
        for (int i = 0; i < index; i++) {
            GroupNode child = new GroupNode(i, cachedNodes[i + 1].getDictionaryIndex());
            child.setData(cachedNodes[i + 1].getData());
            newCachedNodes[i + 1] = child;
            newCachedNodes[i].addChild(child);
        }
        for (int i = 0; i < cachedNodes.length; i++) {
            cachedNodes[i] = newCachedNodes[i];
        }
    }

    public static Iterator<GroupNode> dftNodeIterator(int dimensionSize, GroupNode root) {
        return new DFTGroupNodeIterator(dimensionSize, root);
    }

    public static Iterator<GroupNode> dftNodeIterator(GroupNode root) {
        return new DFTGroupNodeIterator(getDimensionSize(root), root);
    }

    private static class DFTGroupNodeIterator implements Iterator<GroupNode> {

        private GroupNode root;
        private LimitedStack<Iterator<GroupNode>> iterators;
        private GroupNode next;

        /**
         * @param dimensionSize <= 0的情况下返回根节点
         * @param root
         */
        public DFTGroupNodeIterator(int dimensionSize, GroupNode root) {
            this.root = root;
            this.iterators = dimensionSize <= 0 ? null : new ArrayLimitedStack<Iterator<GroupNode>>(dimensionSize);
            init();
        }

        private void init() {
            if (iterators != null) {
                iterators.push(root.getChildren().iterator());
            }
            next = root;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        private GroupNode getNext() {
            GroupNode ret = null;
            while (iterators != null && !iterators.isEmpty()) {
                Iterator<GroupNode> it = iterators.peek();
                if (it.hasNext()) {
                    GroupNode node = it.next();
                    ret = node;
                    if (iterators.size() != iterators.limit()) {
                        iterators.push(node.getChildren().iterator());
                    }
                    break;
                } else {
                    iterators.pop();
                }
            }
            return ret;
        }

        @Override
        public GroupNode next() {
            GroupNode ret = next;
            next = getNext();
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
