package com.fr.swift.result.node.cal;

import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.aggregator.DoubleAmountAggregatorValue;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.node.iterator.BFTGroupNodeIterator;
import com.fr.swift.structure.Pair;
import com.fr.swift.structure.iterator.Filter;
import com.fr.swift.structure.iterator.FilteredIterator;
import com.fr.swift.util.Util;
import com.fr.swift.util.function.Function;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2018/5/16.
 */
public abstract class AbstractRelationTargetCalculator extends AbstractTargetCalculator {
    private GroupNode root;
    private Function<GroupNode, List<AggregatorValue[]>> aggFunc;
    protected Pair<Integer, GroupType> decrease;
    protected GroupType decType;
    protected List<Map<Integer, Object>> dic;
    private Decreaser decreaser;

    public AbstractRelationTargetCalculator(int[] paramIndex, int resultIndex, GroupNode root, List<Map<Integer, Object>> dic, Function<GroupNode, List<AggregatorValue[]>> aggFunc, List<Pair<Integer, GroupType>> brotherGroupIndex) {
        super(paramIndex[0], resultIndex, null);
        this.root = root;
        this.dic = dic;
        this.aggFunc = aggFunc;
        initIndexTypePair(brotherGroupIndex);
        initDecreaser();
    }

    protected void initDecreaser() {
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

    ;

    /**
     * @param brotherGroupIndex 第一个出现的日期字段的维度序号与分组类型，序号-1的表示设置了同期的分组方式
     */
    protected abstract void initIndexTypePair(List<Pair<Integer, GroupType>> brotherGroupIndex);

    @Override
    public Object call() throws Exception {
        Iterator<GroupNode> iterator = new BFTGroupNodeIterator(root);
        FilteredIterator<GroupNode> filteredIterator = new FilteredIterator<GroupNode>(iterator, new Filter<GroupNode>() {
            @Override
            public boolean accept(GroupNode biGroupNode) {
                return biGroupNode.getChildrenSize() == 0;
            }
        });
        while (filteredIterator.hasNext()) {
            GroupNode last = filteredIterator.next();
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

    private Double getRelationValue(int i, GroupNode last) {
        GroupNode node = getNode(last);
        if (node == null) {
            return null;
        }
        List<AggregatorValue[]> rows = aggFunc.apply(node);
        return (Double) rows.get(i)[paramIndex].calculateValue();
    }

    private GroupNode getNode(GroupNode last) {
        List values = getDataValue(last);
        GroupNode node = root;
        for (int i = 0; i < values.size(); i++) {
            node = getChildByData(node, values.get(values.size() - i - 1));
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private GroupNode getChildByData(GroupNode parent, Object data) {
        List<GroupNode> children = parent.getChildren();
        for (GroupNode child : children) {
            if (Util.equals(getNodeData(child), data)) {
                return child;
            }
        }
        return null;
    }

    protected List getDataValue(GroupNode last) {
        List list = new ArrayList();
        while (last.getParent() != null) {
            list.add(getNodeData(last));
            last = last.getParent();
        }
        decrease(list);
        return list;
    }

    private Object getNodeData(GroupNode node) {
        return dic.get(node.getDepth()).get(node.getDictionaryIndex());
    }

    protected void decrease(List list) {
        int index = list.size() - decrease.getKey() - 1;
        list.set(index, decrease(list.get(index)));
    }

    private Object decrease(Object o) {
        return decreaser.decrease((Long) o);
    }

    public int getUnit() {
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

        public DateDecreaser(int unit) {
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

        public SeasonDecreaser() {
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
