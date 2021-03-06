package com.fr.swift.cloud.result.node.cal;

import com.fr.swift.cloud.query.aggregator.AggregatorValue;
import com.fr.swift.cloud.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.cloud.query.group.GroupType;
import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.node.iterator.BFTGroupNodeIterator;
import com.fr.swift.cloud.structure.Pair;
import com.fr.swift.cloud.structure.iterator.Filter;
import com.fr.swift.cloud.structure.iterator.FilteredIterator;
import com.fr.swift.cloud.util.Util;
import com.fr.swift.cloud.util.function.Function;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/5/16.
 */
public abstract class AbstractRelationTargetCalculator extends AbstractTargetCalculator {
    private SwiftNode root;
    private Function<SwiftNode, List<AggregatorValue[]>> aggFunc;
    Pair<Integer, GroupType> decrease;
    GroupType decType;
    private List<Map<Integer, Object>> dic;
    private Decreaser decreaser;

    AbstractRelationTargetCalculator(int[] paramIndex, int resultIndex, SwiftNode root, List<Map<Integer, Object>> dic, Function<SwiftNode, List<AggregatorValue[]>> aggFunc, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(paramIndex[0], resultIndex, null);
        this.root = root;
        this.dic = dic;
        this.aggFunc = aggFunc;
        initIndexTypePair(brotherGroupIndex);
        initDecreaser();
    }

    private void initDecreaser() {
        if (decType == null || decrease == null) {
            decreaser = new EmptyDecreaser();
            return;
        }
        switch (decrease.getValue()) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK_OF_YEAR:
            case WEEK:
            case DAY:
                decreaser = new SingleDecreaser();
                break;
            default:
                decreaser = decType == GroupType.QUARTER ? new SeasonDecreaser() : new DateDecreaser(getUnit());
        }

    }

    /**
     * @param brotherGroupIndex 第一个出现的日期字段的维度序号与分组类型，序号-1的表示设置了同期的分组方式
     */
    protected abstract void initIndexTypePair(List<Pair<Integer, GroupType>> brotherGroupIndex);

    @Override
    public Object call() {
        Iterator<SwiftNode> iterator = new BFTGroupNodeIterator(root);
        FilteredIterator<SwiftNode> filteredIterator = new FilteredIterator<SwiftNode>(iterator, new Filter<SwiftNode>() {
            @Override
            public boolean accept(SwiftNode biGroupNode) {
                return biGroupNode.getChildrenSize() == 0;
            }
        });
        while (filteredIterator.hasNext()) {
            SwiftNode last = filteredIterator.next();
            List<AggregatorValue[]> rows = aggFunc.apply(last);
            for (int i = 0; i < rows.size(); i++) {
                Double value = getRelationValue(i, last);
                if (value != null) {
                    Double result = getValue(value, (Double) rows.get(i)[paramIndex].calculateValue());
                    if (result != null) {
                        rows.get(i)[resultIndex] = new DoubleAmountAggregatorValue(result);
                    }
                }
            }
        }
        return null;
    }

    protected abstract Double getValue(Double relationValue, Double currentValue);

    private Double getRelationValue(int i, SwiftNode last) {
        SwiftNode node = getNode(last);
        if (node == null) {
            return null;
        }
        List<AggregatorValue[]> rows = aggFunc.apply(node);
        return (Double) rows.get(i)[paramIndex].calculateValue();
    }

    private SwiftNode getNode(SwiftNode last) {
        List values = getDataValue(last);
        SwiftNode node = root;
        for (int i = 0; i < values.size(); i++) {
            node = getChildByData(node, values.get(values.size() - i - 1));
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private SwiftNode getChildByData(SwiftNode parent, Object data) {
        List<SwiftNode> children = parent.getChildren();
        for (SwiftNode child : children) {
            if (Util.equals(getNodeData(child), data)) {
                return child;
            }
        }
        return null;
    }

    private List getDataValue(SwiftNode last) {
        List list = new ArrayList();
        while (last.getParent() != null) {
            list.add(getNodeData(last));
            last = last.getParent();
        }
        decrease(list);
        return list;
    }

    private Object getNodeData(SwiftNode node) {
        return dic.get(node.getDepth()).get(node.getDictionaryIndex());
    }

    private void decrease(List list) {
        int index = list.size() - decrease.getKey() - 1;
        list.set(index, decrease(list.get(index)));
    }

    private Object decrease(Object o) {
        return decreaser.decrease((Long) o);
    }

    private int getUnit() {
        switch (decType) {
            case YEAR:
                return Calendar.YEAR;
            case MONTH:
                return Calendar.MONTH;
            case WEEK_OF_YEAR:
                return Calendar.WEEK_OF_YEAR;

        }
        return Calendar.YEAR;
    }

    interface Decreaser {
        Object decrease(Long value);
    }

    class EmptyDecreaser implements Decreaser {

        @Override
        public Object decrease(Long value) {
            return null;
        }
    }

    class SingleDecreaser implements Decreaser {

        @Override
        public Object decrease(Long value) {
            return value == null || value == 1L ? null : value - 1;
        }
    }

    class DateDecreaser implements Decreaser {
        private Calendar c = Calendar.getInstance();

        DateDecreaser(int unit) {
            this.unit = unit;
        }

        private int unit;

        @Override
        public Object decrease(Long value) {
            if (value == null) {
                return null;
            }
            c.setTimeInMillis(value);
            if (c.get(unit) == 0) {
                return null;
            }
            c.add(unit, -1);
            return c.getTimeInMillis();
        }
    }

    class SeasonDecreaser implements Decreaser {
        private Calendar c = Calendar.getInstance();

        SeasonDecreaser() {
        }

        @Override
        public Object decrease(Long value) {
            if (value == null) {
                return null;
            }
            c.setTimeInMillis(value);
            if (c.get(Calendar.MONTH) < Calendar.APRIL) {
                return null;
            }
            c.add(Calendar.MONTH, -3);
            return c.getTimeInMillis();
        }
    }
}
