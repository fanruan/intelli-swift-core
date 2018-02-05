package com.fr.swift.result;

import com.fr.swift.cal.result.group.GroupResultQueryTest;
import com.fr.swift.query.aggregator.AggregatorValue;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class NodeFactoryTest extends TestCase {

    private RowResultCollector collector;
    private List<Map.Entry<RowIndexKey, double[]>> expectedResultList;

    private void prepare(int segmentCount, int dimensionCount, int metricCount, int rowCount) {
        GroupResultQueryTest test = new GroupResultQueryTest(segmentCount, dimensionCount, metricCount, rowCount);
        collector = test.getCollector();
        Map<RowIndexKey, double[]> expectedResult = new TreeMap<>(new NodeFactory.RowIndexKeyComparator(collector.getIndexSorts()));
        expectedResult.putAll(test.getExpectedResult());
        expectedResultList = new ArrayList<>(expectedResult.entrySet());
    }

    public void testNodeWithOnlyOneSegment() {
        prepare(1, 3, 2, 100);
        checkNode();
    }

    public void testNode() {
        prepare(3, 3, 2, 100);
        checkNode();
    }

    private void checkNode() {
        Node root = NodeFactory.createNode(collector);
        Iterator<IndexNode> iterator = new NodeIterator(root);
        while (iterator.hasNext()) {
            IndexNode node = iterator.next();
            if (node.getChildrenSize() == 0) {
                // 测试聚合结果，除了汇总行
                AggregatorValue[] values = node.getAggregatorValue();
                Map.Entry<RowIndexKey, double[]> entry = expectedResultList.remove(0);
                double[] expected = entry.getValue();
                assertEquals(values.length, expected.length);
                for (int i = 0; i < expected.length; i++) {
                    assertEquals(values[i].calculate(), expected[i]);
                }
            }
        }
    }

    private static class NodeIterator implements Iterator<IndexNode> {

        private boolean hasNext;
        private IndexNode nextNode;

        public NodeIterator(IndexNode root) {
            hasNext = root != null;
            nextNode = hasNext ? root : null;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public IndexNode next() {
            if (!hasNext) {
                throw new NoSuchElementException();
            }
            IndexNode tmp = nextNode;
            if (nextNode.getSibling() == null && nextNode.getParent() == null) {
                // nextNode为root，结束
                hasNext = false;
            } else if (nextNode.getChildrenSize() == 0) {
                if (nextNode.getSibling() == null) {
                    nextNode = nextNode.getParent();
                    next();
                } else {
                    nextNode = nextNode.getSibling();
                }
            } else {
                nextNode = nextNode.getChild(0);
            }
            return tmp;
        }

        @Override
        public void remove() {
        }
    }
}
