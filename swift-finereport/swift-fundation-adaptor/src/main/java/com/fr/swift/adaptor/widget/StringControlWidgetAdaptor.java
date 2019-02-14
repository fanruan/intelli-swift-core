package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.AbstractTableWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.StringControlWidget;
import com.finebi.conf.internalimp.dashboard.widget.table.StringListControlWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.BIStringDetailResult;
import com.finebi.conf.structure.result.StringControlResult;
import com.fr.stable.StringUtils;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.transformer.filter.dimension.DimensionFilterAdaptor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.SwiftDetailFilterType;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.filter.info.GeneralFilterInfo;
import com.fr.swift.query.filter.info.SwiftDetailFilterInfo;
import com.fr.swift.segment.column.ColumnKey;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pony on 2018/3/24.
 */
public class StringControlWidgetAdaptor extends AbstractTableWidgetAdaptor {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(StringControlWidgetAdaptor.class);
    private static final int PAGE_SIZE = 100;

    public static BIStringDetailResult calculate(StringControlWidget widget) {
        return calculate(widget, widget.getKeywords(), widget.getTimes(), widget.getSelectedValues());
    }

    public static BIStringDetailResult calculate(StringListControlWidget widget) {
        return calculate(widget, widget.getKeywords(), widget.getTimes(), widget.getSelectedValues());
    }

    private static BIStringDetailResult calculate(AbstractTableWidget widget, List<String> keyWords, int times, List<String> selectValues) {
        StringControlResult value = new StringControlResult();
        try {
            FineDimension dimension = widget.getDimensionList().get(0);
            times = times == 0 ? 1 : times;
            List<FilterInfo> filterInfos = new ArrayList<FilterInfo>();
            if (keyWords != null && !keyWords.isEmpty()) {
                for (String keyWord : keyWords) {
                    if (!StringUtils.isEmpty(keyWord)) {
                        filterInfos.add(new SwiftDetailFilterInfo<String>(new ColumnKey(getColumnName(dimension.getFieldId())), keyWord, SwiftDetailFilterType.KEY_WORDS));
                    }
                }
            }
            if (selectValues != null && !selectValues.isEmpty()) {
                filterInfos.add(new SwiftDetailFilterInfo<Set<String>>(new ColumnKey(getColumnName(dimension.getFieldId())), new HashSet<String>(selectValues), SwiftDetailFilterType.STRING_NOT_IN));
            }
            if (widget.getFilters() != null) {
                filterInfos.add(FilterInfoFactory.transformFineFilter(widget.getTableName(), widget.getFilters()));
            }
            if (dimension.getFilters() != null) {
                filterInfos.add(DimensionFilterAdaptor.transformDimensionFineFilter(dimension));
            }
            List values = QueryUtils.getOneDimensionFilterValues(dimension, new GeneralFilterInfo(filterInfos, GeneralFilterInfo.AND), widget.getWidgetId());

            List showValues = values.subList((times - 1) * PAGE_SIZE, Math.min(times * PAGE_SIZE, values.size()));
            //查询记录数,等分组表那边弄好了再搞。
//            Metric countMetric = new GroupMetric(0, baseDataSource.getSourceKey(), new ColumnKey(fineBusinessField.getName()), filterInfo, new DistinctAggregate());
//            SingleTableGroupQueryInfo countInfo = new SingleTableGroupQueryInfo(new RowCursor(), widget.getWidgetId(), new Dimension[0], new Metric[]{countMetric}, new GroupTarget[0], filterInfo, null);
//            Query<GroupByResultSet> countQuery = QueryBuilder.buildQuery(countInfo);
//            GroupByResultSet countResultSet = countQuery.executeQuery();

            value.setHasNext(values.size() > (times) * PAGE_SIZE);
            value.setValue(showValues);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new StringDetailResult(value);
    }


    static class StringDetailResult implements BIStringDetailResult {

        private StringControlResult result;

        public StringDetailResult(StringControlResult result) {
            this.result = result;
        }

        @Override
        public int rowSize() {
            return 0;
        }

        @Override
        public StringControlResult getResult() {
            return result;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.STRING;
        }
    }
}