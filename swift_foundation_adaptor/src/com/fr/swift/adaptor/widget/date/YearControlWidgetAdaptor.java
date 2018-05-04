package com.fr.swift.adaptor.widget.date;

import com.finebi.conf.constant.BIConfConstants.CONF.GROUP.DATE;
import com.finebi.conf.internalimp.bean.dashboard.widget.dimension.group.TypeGroupBean;
import com.finebi.conf.internalimp.dashboard.widget.control.time.YearControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.dimension.group.DimensionTypeGroup;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.time.BIYearControlResult;
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
public class YearControlWidgetAdaptor {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(YearControlWidgetAdaptor.class);

    public static BIYearControlResult calculate(YearControlWidget widget) {
        try {
            FineDimension dimension = widget.getDimensionList().get(0);

            DimensionTypeGroup typeGroup = new DimensionTypeGroup();
            typeGroup.setType(DATE.YEAR);
            TypeGroupBean bean = new TypeGroupBean();
            bean.setType(DATE.YEAR);
            typeGroup.setValue(bean);

            //设置下年分组
            dimension.setGroup(typeGroup);
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getTableName(), widget.getFilters());
            List<Long> values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            List<Integer> years = new ArrayList<Integer>();
            for (Long v : values) {
                years.add(v.intValue());
            }
            return new YearControlResult(years);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    static class YearControlResult implements BIYearControlResult {
        private List<Integer> values;

        public YearControlResult(List<Integer> values) {
            this.values = values;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.YEAR;
        }

        @Override
        public List<Integer> getYears() {
            return values;
        }
    }
}
