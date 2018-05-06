package com.fr.swift.query.filter.detail.impl;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.aggregator.StringAggregateValue;
import com.fr.swift.result.AbstractSwiftNode;
import com.fr.swift.result.SwiftNode;
import junit.framework.TestCase;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class BaseFilterTest extends TestCase {

    public final static String NULL_VALUE = "NULL";
    protected static Random random = new Random(23854);

    protected static <T> T getRandomDetail(List<T> details) {
        while (true) {
            T s = details.get(random.nextInt(details.size()));
            if (!s.equals(NULL_VALUE)) {
                return s;
            }
        }
    }

    protected static <T> T getRandomMatchedDetail(List<T> details, List<Integer> expectedIndexes) {
        if (expectedIndexes.size() == details.size()) {
            return null;
        }
        while (true) {
            int i = random.nextInt(details.size());
            if (expectedIndexes.size() == 0 || expectedIndexes.contains(i)) {
                return details.get(i);
            }
        }
    }

    protected <T> T getRandomNotMatchedDetail(List<T> details, List<Integer> expectedIndexes) {
        if (expectedIndexes.size() == details.size()) {
            return null;
        }
        while (true) {
            int i = random.nextInt(details.size());
            if (!expectedIndexes.contains(i)) {
                return details.get(i);
            }
        }
    }

    protected static SwiftNode createNode(int index, int groupSize) {
        return new AbstractSwiftNode() {
            @Override
            public Object getData() {
                return null;
            }

            @Override
            public void setData(Object data) {

            }

            @Override
            public SwiftNode getChild(int index) {
                return null;
            }

            @Override
            public void addChild(SwiftNode child) {

            }

            @Override
            public SwiftNode getSibling() {
                return null;
            }

            @Override
            public void setSibling(SwiftNode sibling) {

            }

            @Override
            public SwiftNode getParent() {
                return new AbstractSwiftNode() {
                    @Override
                    public Object getData() {
                        return null;
                    }

                    @Override
                    public void setData(Object data) {

                    }

                    @Override
                    public SwiftNode getChild(int index) {
                        return null;
                    }

                    @Override
                    public void addChild(SwiftNode child) {

                    }

                    @Override
                    public SwiftNode getSibling() {
                        return null;
                    }

                    @Override
                    public void setSibling(SwiftNode sibling) {

                    }

                    @Override
                    public SwiftNode getParent() {
                        return null;
                    }

                    @Override
                    public void setParent(SwiftNode parent) {

                    }

                    @Override
                    public int getChildrenSize() {
                        return groupSize;
                    }

                    @Override
                    public int getIndex() {
                        return 0;
                    }

                    @Override
                    public int getDepth() {
                        return 0;
                    }

                    @Override
                    public void clearChildren() {

                    }

                    @Override
                    public List getChildren() {
                        return null;
                    }

                    @Override
                    public void setAggregatorValue(int key, AggregatorValue value) {

                    }

                    @Override
                    public AggregatorValue getAggregatorValue(int key) {
                        return null;
                    }

                    @Override
                    public AggregatorValue[] getAggregatorValue() {
                        return new AggregatorValue[0];
                    }

                    @Override
                    public void setAggregatorValue(AggregatorValue[] aggregatorValues) {

                    }
                };
            }

            @Override
            public void setParent(SwiftNode parent) {

            }

            @Override
            public int getChildrenSize() {
                return 0;
            }

            @Override
            public int getIndex() {
                return index;
            }

            @Override
            public int getDepth() {
                return 0;
            }

            @Override
            public void clearChildren() {

            }

            @Override
            public List getChildren() {
                return null;
            }

            @Override
            public void setAggregatorValue(int key, AggregatorValue value) {

            }

            @Override
            public AggregatorValue getAggregatorValue(int key) {
                return null;
            }

            @Override
            public AggregatorValue[] getAggregatorValue() {
                return new AggregatorValue[0];
            }

            @Override
            public void setAggregatorValue(AggregatorValue[] aggregatorValues) {

            }
        };
    }

    protected static SwiftNode createNode(Object data) {
        return createNode(data, null);
    }

    protected static SwiftNode createNode(Object data, Comparator comparator) {
        return new AbstractSwiftNode() {
            @Override
            public Object getData() {
                return data;
            }

            @Override
            public void setData(Object data) {

            }

            @Override
            public SwiftNode getChild(int index) {
                return null;
            }

            @Override
            public void addChild(SwiftNode child) {

            }

            @Override
            public SwiftNode getSibling() {
                return null;
            }

            @Override
            public void setSibling(SwiftNode sibling) {

            }

            @Override
            public SwiftNode getParent() {
                return null;
            }

            @Override
            public void setParent(SwiftNode parent) {

            }

            @Override
            public int getChildrenSize() {
                return 0;
            }

            @Override
            public void setAggregatorValue(int key, AggregatorValue value) {

            }

            @Override
            public AggregatorValue getAggregatorValue(int key) {
                return data == null ? new StringAggregateValue() : new DoubleAmountAggregatorValue(((Number)data).doubleValue());
            }

            @Override
            public AggregatorValue[] getAggregatorValue() {
                return new AggregatorValue[0];
            }

            @Override
            public void setAggregatorValue(AggregatorValue[] aggregatorValues) {

            }

            @Override
            public int getIndex() {
                return 0;
            }

            @Override
            public int getDepth() {
                return 0;
            }

            @Override
            public void clearChildren() {

            }

            @Override
            public List getChildren() {
                return null;
            }
        };
    }
}
