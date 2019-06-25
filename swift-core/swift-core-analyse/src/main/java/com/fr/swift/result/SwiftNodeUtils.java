package com.fr.swift.result;

import com.fr.swift.query.aggregator.AggregatorValueRow;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.structure.iterator.IteratorChain;
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

    public static List<SwiftNode> getRow(SwiftNode root, int index) {
        return IteratorUtils.iterator2List(node2RowListIterator(root)).get(index);
    }

    public static List<SwiftNode> getLastRow(SwiftNode root) {
        int dimensionSize = getDimensionSize(root);
        SwiftNode prev = root;
        List<SwiftNode> row = new ArrayList<SwiftNode>();
        for (int i = 0; i < dimensionSize; i++) {
            SwiftNode node = prev.getChild(prev.getChildrenSize() - 1);
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
            return root.getAggregatorValue().data();
        }
        Iterator<List<SwiftNode>> iterator = node2RowListIterator(root);

        MapperIterator<List<SwiftNode>, Iterator<Row>> rows = new MapperIterator<List<SwiftNode>, Iterator<Row>>(iterator, new Function<List<SwiftNode>, Iterator<Row>>() {
            @Override
            public Iterator<Row> apply(List<SwiftNode> p) {
                return nodes2Rows(p);
            }
        });

        return new IteratorChain<Row>(rows);
    }

    public static Iterator<Row> nodes2Rows(List<SwiftNode> row) {
        final List<Object> data = new ArrayList<Object>();
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
            return new MapperIterator<AggregatorValueRow, Row>(leafNode.getAggregatorValue().iterator(), new Function<AggregatorValueRow, Row>() {
                @Override
                public Row apply(AggregatorValueRow p) {
                    List<Object> result = new ArrayList<Object>(data);
                    result.addAll(p.data());
                    return new ListBasedRow(result);
                }
            });
        } else {
            return Collections.<Row>singleton(new ListBasedRow(data)).iterator();
        }
    }

    public static SwiftNode[] splitNode(SwiftNode root, int numberOfNodes, int rowCount) {
        Iterator<SwiftNode> iterator = SwiftNodeUtils.dftNodeIterator(root);
        iterator.next();    // 跳过root
        SwiftNode[] cachedNodes = new SwiftNode[getDimensionSize(root)];
        SwiftNode[] result = new SwiftNode[numberOfNodes];
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

    private static void copy(SwiftNode[] cachedNodes, Iterator<SwiftNode> iterator, int size) {
        int rowCount = 0;
        boolean uninitialized = true;
        while (iterator.hasNext() && rowCount < size) {
            SwiftNode node = iterator.next();
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

    private static void newCacheNodes(SwiftNode[] cachedNodes, int index) {
        GroupNode[] newCachedNodes = new GroupNode[cachedNodes.length];
        newCachedNodes[0] = new GroupNode(-1, null);
        for (int i = 0; i < index; i++) {
            GroupNode child = new GroupNode(i, cachedNodes[i + 1].getDictionaryIndex());
            child.setData(cachedNodes[i + 1].getData());
            newCachedNodes[i + 1] = child;
            newCachedNodes[i].addChild(child);
        }
        System.arraycopy(newCachedNodes, 0, cachedNodes, 0, cachedNodes.length);
    }

    public static Iterator<SwiftNode> dftNodeIterator(int dimensionSize, SwiftNode root) {
        return new DFTGroupNodeIterator(dimensionSize, root);
    }

    public static Iterator<SwiftNode> dftNodeIterator(SwiftNode root) {
        return new DFTGroupNodeIterator(getDimensionSize(root), root);
    }

    private static class DFTGroupNodeIterator implements Iterator<SwiftNode> {

        private SwiftNode root;
        private LimitedStack<Iterator<SwiftNode>> iterators;
        private SwiftNode next;

        /**
         * @param dimensionSize <= 0的情况下返回根节点
         * @param root
         */
        DFTGroupNodeIterator(int dimensionSize, SwiftNode root) {
            this.root = root;
            this.iterators = dimensionSize <= 0 ? null : new ArrayLimitedStack<Iterator<SwiftNode>>(dimensionSize);
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

        private SwiftNode getNext() {
            SwiftNode ret = null;
            while (iterators != null && !iterators.isEmpty()) {
                Iterator<SwiftNode> it = iterators.peek();
                if (it.hasNext()) {
                    SwiftNode node = it.next();
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
        public SwiftNode next() {
            SwiftNode ret = next;
            next = getNext();
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
