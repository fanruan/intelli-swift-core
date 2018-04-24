package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.analysis.bean.operator.add.group.custom.number.NumberMaxAndMinValue;
import com.finebi.conf.internalimp.dashboard.widget.control.number.SingleSliderWidget;
import com.finebi.conf.structure.dashboard.widget.dimension.FineDimension;
import com.finebi.conf.structure.result.control.number.BISingleSliderResult;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;

import java.util.List;

/**
 * @author pony
 * @date 2018/3/22
 */
public class SingleSliderWidgetAdaptor extends AbstractTableWidgetAdaptor {
    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SingleSliderWidgetAdaptor.class);

    public static BISingleSliderResult calculate(SingleSliderWidget widget) {
        NumberMaxAndMinValue value = new NumberMaxAndMinValue();
        try {
            List<FineDimension> dimensions = widget.getDimensionList();
            for (FineDimension dimension : dimensions) {
                setMaxMinNumValue(widget.getWidgetId(), dimension.getFieldId(), widget.getFilters(), value);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
        return new SingleSliderResult(value);
    }

    static class SingleSliderResult implements BISingleSliderResult {
        private NumberMaxAndMinValue value;

        SingleSliderResult(NumberMaxAndMinValue value) {
            this.value = value;
        }

        @Override
        public NumberMaxAndMinValue getMaxAndMinValue() {
            return value;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.INTERVAL_SLIDER;
        }
    }
}

