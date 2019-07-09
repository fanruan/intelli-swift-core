package com.fr.swift.query.segment.group;

import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.aggregator.AggregatorType;
import com.fr.swift.query.funnel.TimeWindowBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelAggregationBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.FunnelEventBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.ParameterColumnsBean;
import com.fr.swift.query.info.bean.element.aggregation.funnel.filter.DayFilterInfo;
import com.fr.swift.query.info.bean.element.aggregation.funnel.group.time.TimeGroup;
import com.fr.swift.query.info.bean.element.filter.impl.InFilterBean;
import com.fr.swift.query.info.bean.query.FunnelQueryBean;
import com.fr.swift.query.query.Query;
import com.fr.swift.result.FunnelResultSet;
import com.fr.swift.result.funnel.FunnelQueryResultSet;
import com.fr.swift.result.qrs.QueryResultSet;
import com.fr.swift.segment.Segment;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * This class created on 2018/12/13
 *
 * @author Lucifer
 * @description
 */
public class FunnelSegmentQuery implements Query<QueryResultSet<FunnelResultSet>> {

    private FunnelCalculator calculator;

    public FunnelSegmentQuery(Segment segment, FunnelQueryBean bean) {
        FunnelAggregationBean funnelAggregationBean = new FunnelAggregationBean();
        funnelAggregationBean.setTimeGroup(TimeGroup.WORK_DAY);
        funnelAggregationBean.setColumn("eventType");
        funnelAggregationBean.setType(AggregatorType.FUNNEL_PATHS);
        funnelAggregationBean.setColumns(new ParameterColumnsBean("id", "currentTime"));
        FunnelEventBean first = new FunnelEventBean();
        first.setName("login");
        first.setSteps(Collections.singletonList("login"));
        first.setFilter(new InFilterBean("city", "深圳"));
        FunnelEventBean second = new FunnelEventBean();
        second.setName("browseGoods");
        second.setSteps(Collections.singletonList("searchGoods"));
        funnelAggregationBean.setEvents(Arrays.asList(first, second));
        TimeWindowBean timeWindow = new TimeWindowBean();
        timeWindow.setDuration(30);
        timeWindow.setUnit(TimeUnit.DAYS);
        funnelAggregationBean.setTimeWindow(timeWindow);
        funnelAggregationBean.setTimeFilter(new DayFilterInfo("currentTime", "20180601", 30));
        calculator = new FunnelCalculator(segment, funnelAggregationBean);
//        this.bean = bean;
//        this.segment = segment;
//        Column event = segment.getColumn(new ColumnKey(bean.getAggregation().getColumns().getEvent()));
//        this.eventDict = event.getDictionaryEncodedColumn();
//        this.globalDic = new HashMap<String, Long>();
//        initGlobalDic();
//        initDates();
    }
//
//    private void initGlobalDic() {
//        int dicSize = eventDict.size();
//        // eventType不为空
//        for (long i = 1; i < dicSize; i++) {
//            String dicValue = String.valueOf(eventDict.getValue((int) i));
//            globalDic.put(dicValue, i - 1);
//        }
//    }
//
//    private void initDates() {
//        dates = new String[45];
//        long start = 0;
//        try {
//            start = format.parse(bean.getAggregation().getDayFilter().getTimeStart()).getTime();
//        } catch (ParseException e) {
//            Crasher.crash(e);
//        }
//        for (int i = 0; i < 45; i++) {
//            dates[i] = format.format(new Date(start));
//            start += 24 * 60 * 60 * 1000;
//        }
//    }

    @Override
    public FunnelQueryResultSet getQueryResult() throws SwiftMetaDataException {
        return calculator.getQueryResult();
    }

}
