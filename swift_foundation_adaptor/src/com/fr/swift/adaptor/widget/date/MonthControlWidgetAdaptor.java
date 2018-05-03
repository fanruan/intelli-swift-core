package com.fr.swift.adaptor.widget.date;

import com.finebi.conf.constant.BIConfConstants.CONF.GROUP.DATE;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.TypeGroupBean;
import com.finebi.conf.internalimp.dashboard.widget.control.time.MonthControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.DimensionTypeGroup;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.time.BIMonthControlResult;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.QueryUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2018/3/26
 */
public class MonthControlWidgetAdaptor {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(MonthControlWidgetAdaptor.class);

    public static BIMonthControlResult calculate(MonthControlWidget widget) {
        try {
            FineDimension dimension = widget.getDimensionList().get(0);

            //设置下年分组
            DimensionTypeGroup yearGroup = new DimensionTypeGroup();
            yearGroup.setType(DATE.YEAR);
            TypeGroupBean yearBean = new TypeGroupBean();
            yearGroup.setValue(yearBean);
            yearBean.setType(DATE.YEAR);
            dimension.setGroup(yearGroup);

            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getTableName(), widget.getFilters());
            List<Long> yearValues = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            List<Integer> years = new ArrayList<Integer>();
            for (Long v : yearValues) {
                years.add(v.intValue());
            }

            //设置下月分组
            DimensionTypeGroup monthGroup = new DimensionTypeGroup();
            monthGroup.setType(DATE.MONTH);
            TypeGroupBean monthBean = new TypeGroupBean();
            monthGroup.setValue(monthBean);
            monthBean.setType(DATE.MONTH);
            dimension.setGroup(monthGroup);

            List<Long> monthValues = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            List<Integer> months = new ArrayList<Integer>();
            for (Long v : monthValues) {
                months.add(v.intValue());
            }
            return new MonthControlResult(years, months);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    static class MonthControlResult implements BIMonthControlResult {
        private List<Integer> years;
        private List<Integer> months;

        public MonthControlResult(List<Integer> years, List<Integer> months) {
            this.years = years;
            this.months = months;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.MONTH;
        }

        @Override
        public List<Integer> getMonths() {
            return months;
        }

        @Override
        public List<Integer> getYeas() {
            return years;
        }
    }
}
