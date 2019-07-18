package com.fr.swift.query.aggregator;

import com.fr.swift.query.aggregator.funnel.FunnelPathKey;
import com.fr.swift.query.column.ComplexColumn;
import com.fr.swift.query.filter.detail.DetailFilter;
import com.fr.swift.query.filter.match.MatchFilter;
import com.fr.swift.query.info.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.funnel.FunnelPathsAggregationBean;
import com.fr.swift.query.info.funnel.FunnelVirtualStep;
import com.fr.swift.query.info.funnel.group.time.TimeGroup;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.structure.iterator.RowTraversal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 选出要执行计算的Path进行漏斗计算后，计算转化率
 * @author yee
 * @date 2019-07-10
 */
public class FunnelPathsAggregator extends MultiColumnAggregator<FunnelPathsAggregatorValue> {

    private FunnelPathsAggregationBean bean;
    private List<DetailFilter> eventFilters;

    public FunnelPathsAggregator(FunnelPathsAggregationBean bean) {
        this.bean = bean;
    }

    @Override
    public FunnelPathsAggregatorValue aggregate(RowTraversal traversal, Map<ColumnKey, Column<?>> columns) {
        FunnelAggregationIterator iterator = new FunnelAggregationIterator(bean);
        Map<FunnelPathKey, FunnelAggregatorValue> value = new HashMap<FunnelPathKey, FunnelAggregatorValue>();
        while (iterator.hasNext()) {
            FunnelAggregationBean next = iterator.next();
            FunnelAggregator funnelAggregator = new FunnelAggregator(next);
            funnelAggregator.setEventFilters(eventFilters);
            funnelAggregator.setTimeGroupFilter(MatchFilter.TRUE);
            FunnelAggregatorValue aggregate = funnelAggregator.aggregate(traversal, columns);
            List<FunnelVirtualStep> events = next.getSteps();
            for (int i = events.size(); i > 1; i--) {
                FunnelPathKey key = new FunnelPathKey(events.subList(0, i));
                value.put(key, aggregate);
            }
        }

        return new FunnelPathsAggregatorValue(value);
    }

    @Override
    public FunnelPathsAggregatorValue aggregate(RowTraversal traversal, Column<?> column) {
        return aggregate(traversal, ((ComplexColumn) column).getColumns());
    }

    @Override
    public FunnelPathsAggregatorValue createAggregatorValue(AggregatorValue<?> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AggregatorType getAggregatorType() {
        return AggregatorType.FUNNEL;
    }

    @Override
    public void combine(FunnelPathsAggregatorValue current, FunnelPathsAggregatorValue other) {
        current.combine(other);
    }

    public void setEventFilters(List<DetailFilter> eventFilters) {
        this.eventFilters = eventFilters;
    }

    private class FunnelAggregationIterator implements Iterator<FunnelAggregationBean> {

        private Iterator[] iterators;
        private List<FunnelVirtualStep> source;
        private FunnelPathsAggregationBean bean;
        private String[] tmp;
        private int lastIterator = -1;
        private int preLastIterator = -1;


        public FunnelAggregationIterator(FunnelPathsAggregationBean bean) {
            this.bean = bean;
            this.source = bean.getSteps();
            iterators = new Iterator[source.size()];
            this.tmp = new String[source.size()];
            for (int i = 0; i < source.size(); i++) {
                iterators[i] = source.get(i).getEvents().iterator();
            }
        }

        @Override
        public boolean hasNext() {
            for (Iterator iterator : iterators) {
                if (null != iterator && iterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public FunnelAggregationBean next() {
            for (int i = tmp.length - 1; i >= 0; i--) {
                if (lastIterator == -1) {
                    lastIterator = i;
                }
                if (iterators[i].hasNext()) {
                    if (tmp[i] == null) {
                        tmp[i] = (String) iterators[i].next();
                    } else if (i == lastIterator) {
                        tmp[i] = (String) iterators[i].next();
                        if (preLastIterator != -1) {
                            lastIterator = preLastIterator;
                        }
                    }
                } else if (i == lastIterator) {
                    iterators[i] = (source.get(i).getEvents()).iterator();
                    tmp[i] = (String) iterators[i].next();
                    if (preLastIterator == -1) {
                        preLastIterator = lastIterator;
                    }
                    lastIterator = -1;
                }

            }
            FunnelAggregationBean result = new FunnelAggregationBean();
            List<FunnelVirtualStep> eventBeans = new ArrayList<FunnelVirtualStep>();
            for (String event : tmp) {
                FunnelVirtualStep eventBean = new FunnelVirtualStep();
                eventBean.setName(event);
                eventBean.setEvents(Collections.singletonList(event));
                eventBeans.add(eventBean);
            }
            result.setSteps(eventBeans);
            result.setTimeGroup(TimeGroup.ALL);
            result.setTimeFilter(bean.getTimeFilter());
            result.setTimeWindow(bean.getTimeWindow());
            result.setTimeFilter(bean.getTimeFilter());
            result.setAssociation(bean.getAssociation());
            result.setParamColumns(bean.getParamColumns());
            result.setColumn(bean.getColumn());
            return result;
        }

        @Override
        public void remove() {

        }
    }
}
