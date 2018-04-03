package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.control.string.ListLabelWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.string.BIListLabelResult;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.filter.info.FilterInfo;

import java.util.List;

/**
 * Created by pony on 2018/3/24.
 */
public class ListLabelWidgetAdaptor {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(ListLabelWidgetAdaptor.class);

    public static BIListLabelResult calculate(ListLabelWidget widget) {
        try {
            FineDimension dimension = widget.getDimensionList().get(0);
            FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());
            List values = QueryUtils.getOneDimensionFilterValues(dimension, filterInfo, widget.getWidgetId());
            return new ListLabelResult(true, values);
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return null;
    }

    static class ListLabelResult implements BIListLabelResult {
        private boolean hasNext;
        private List<String> values;

        public ListLabelResult(boolean hasNext, List<String> values) {
            this.hasNext = hasNext;
            this.values = values;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.LIST_LABEL;
        }

        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public List<String> getLabels() {
            return values;
        }
    }
}
