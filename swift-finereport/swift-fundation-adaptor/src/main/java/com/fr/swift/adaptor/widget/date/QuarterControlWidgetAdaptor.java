package com.fr.swift.adaptor.widget.date;

import com.finebi.conf.constant.BIConfConstants.CONF.GROUP.DATE;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.TypeGroupBean;
import com.finebi.conf.internalimp.dashboard.widget.control.time.QuarterControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.DimensionTypeGroup;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.time.BIQuarterResult;
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
public class QuarterControlWidgetAdaptor {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(MonthControlWidgetAdaptor.class);

    public static BIQuarterResult calculate(QuarterControlWidget widget) {
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

            //设置下季度分组
            DimensionTypeGroup quarterGroup = new DimensionTypeGroup();
            quarterGroup.setType(DATE.QUARTER);
            TypeGroupBean quarterBean = new TypeGroupBean();
            quarterGroup.setValue(quarterBean);
            quarterBean.setType(DATE.QUARTER);
            dimension.setGroup(quarterGroup);

            List<Long> values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            List<Integer> quarters = new ArrayList<Integer>();
            for (Long v : values) {
                quarters.add(v.intValue());
            }
            return new QuarterResult(years, quarters);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    static class QuarterResult implements BIQuarterResult {
        private List<Integer> years;
        private List<Integer> quarters;

        public QuarterResult(List<Integer> years, List<Integer> quarters) {
            this.years = years;
            this.quarters = quarters;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.QUARTER;
        }


        @Override
        public List<Integer> getQuarters() {
            return quarters;
        }

        @Override
        public List<Integer> getYears() {
            return years;
        }
    }
}
