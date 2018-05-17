package com.fr.swift.result.node.cal;

import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.BrotherGroupTarget;
import com.fr.swift.query.adapter.target.cal.CalTargetType;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.node.iterator.CurrentDimensionIterator;
import com.fr.swift.result.node.iterator.LastDimensionIterator;
import com.fr.swift.util.function.Function;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/4/8.
 */
public class TargetCalculatorFactory {

    public static TargetCalculator create(GroupTarget target, GroupNode groupNode, List<Map<Integer, Object>> dic) {
        CalTargetType type = target.type();
        Iterator<Iterator<List<AggregatorValue[]>>> iterator = createIterator(type, groupNode);
        switch (type) {
            case ALL_SUM_OF_ALL:
            case GROUP_SUM_OF_ALL: {
                Double[] values;
                if (groupNode instanceof XLeftNode) {
                    List<AggregatorValue[]> aggregatorValues = ((XLeftNode) groupNode).getValueArrayList();
                    values = new Double[aggregatorValues.size()];
                    for (int i = 0; i < aggregatorValues.size(); i++) {
                        values[i] = aggregatorValues.get(i)[target.paramIndexes()[0]].calculate();
                    }
                } else {
                    // Double value = groupNode.getAggregatorValue()[target.paramIndexes()[0]].calculate();
                    values = new Double[]{null};
                }
                return new SumOfAllCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, values);
            }
            case ALL_AVG:
            case GROUP_AVG:
                return new AverageCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case ALL_SUM_OF_ABOVE:
            case GROUP_SUM_OF_ABOVE:
                return new SumOfAboveCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case ALL_MAX:
            case GROUP_MAX:
                return new MaxOrMinCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, true);
            case ALL_MIN:
            case GROUP_MIN:
                return new MaxOrMinCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, false);
            case ALL_RANK_ASC:
            case GROUP_RANK_ASC:
                return new RankCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, true);
            case ALL_RANK_DEC:
            case GROUP_RANK_DEC:
                return new RankCalculator(target.paramIndexes()[0], target.resultIndex(), iterator, false);
            case DIMENSION_PERCENT:
                return new DimensionPercentCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case TARGET_PERCENT:
                return new TargetPercentCalculator(target.paramIndexes()[0], target.resultIndex(), iterator);
            case BROTHER_VALUE:
                return new BrotherValueTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, getFunctionByNodeType(groupNode), ((BrotherGroupTarget) target).getBrotherGroupIndex());
            case BROTHER_RATE:
                return new BrotherRateTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, getFunctionByNodeType(groupNode), ((BrotherGroupTarget) target).getBrotherGroupIndex());
            case COUSIN_VALUE:
                return new CousinValueTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, getFunctionByNodeType(groupNode), ((BrotherGroupTarget) target).getBrotherGroupIndex());
            case COUSIN_RATE:
                return new CousinRateTargetCalculator(target.paramIndexes(), target.resultIndex(), groupNode, dic, getFunctionByNodeType(groupNode), ((BrotherGroupTarget) target).getBrotherGroupIndex());
        }
        return null;
    }

    private static Function<GroupNode, List<AggregatorValue[]>> getFunctionByNodeType(GroupNode groupNode) {
        return groupNode instanceof XLeftNode ? xLeftNodeMapper() : groupNodeMapper();
    }

    private static Iterator<Iterator<List<AggregatorValue[]>>> createIterator(CalTargetType type, GroupNode root) {
        switch (type) {
            case ALL_SUM_OF_ALL:
            case ALL_AVG:
            case ALL_SUM_OF_ABOVE:
            case ALL_MAX:
            case ALL_MIN:
            case ALL_RANK_ASC:
            case ALL_RANK_DEC:
            case TARGET_PERCENT:
            case DIMENSION_PERCENT:
                return new RootIterator(root, getFunctionByNodeType(root));
            case GROUP_SUM_OF_ALL:
            case GROUP_AVG:
            case GROUP_SUM_OF_ABOVE:
            case GROUP_MAX:
            case GROUP_MIN:
            case GROUP_RANK_ASC:
            case GROUP_RANK_DEC:
                return new GroupIterator(root, getFunctionByNodeType(root));
        }
        return new RootIterator(root, getFunctionByNodeType(root));
    }


    private static class RootIterator implements Iterator<Iterator<List<AggregatorValue[]>>> {
        private Function<GroupNode, List<AggregatorValue[]>> function;
        private GroupNode root;
        private boolean hasNext = true;

        private RootIterator(GroupNode root, Function<GroupNode, List<AggregatorValue[]>> function) {
            this.root = root;
            this.function = function;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public Iterator<List<AggregatorValue[]>> next() {
            hasNext = false;
            return new LastDimensionIterator(root, function);
        }

        @Override
        public void remove() {

        }
    }

    private static class GroupIterator implements Iterator<Iterator<List<AggregatorValue[]>>> {
        private GroupNode current;
        private Function<GroupNode, List<AggregatorValue[]>> function;

        private GroupIterator(GroupNode root, Function<GroupNode, List<AggregatorValue[]>> function) {
            initCurrent(root);
            this.function = function;
        }

        private void initCurrent(GroupNode root) {
            current = root;
            while (current.getChildrenSize() != 0) {
                current = current.getChild(0);
            }
            current = current.getParent();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Iterator<List<AggregatorValue[]>> next() {
            GroupNode node = current;
            current = current.getSibling();
            return new CurrentDimensionIterator(node, function);
        }

        @Override
        public void remove() {

        }
    }

    private static Function<GroupNode, List<AggregatorValue[]>> groupNodeMapper() {
        return new Function<GroupNode, List<AggregatorValue[]>>() {
            @Override
            public List<AggregatorValue[]> apply(final GroupNode p) {
                return Arrays.<AggregatorValue[]>asList(p.getAggregatorValue());
            }
        };
    }

    private static Function<GroupNode, List<AggregatorValue[]>> xLeftNodeMapper() {
        return new Function<GroupNode, List<AggregatorValue[]>>() {
            @Override
            public List<AggregatorValue[]> apply(final GroupNode p) {
                return ((XLeftNode) p).getValueArrayList();
            }
        };
    }
}
