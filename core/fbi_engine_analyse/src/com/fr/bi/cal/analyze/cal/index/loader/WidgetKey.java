package com.fr.bi.cal.analyze.cal.index.loader;

import com.fr.bi.base.BICore;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.general.ComparatorUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 小灰灰 on 2017/2/3.
 */
public class WidgetKey {
    private BICore widgetCore;
    private boolean isCross;
    private boolean isHor;
    private NodeExpander expander;
    private  Operator op;
    private int[] startIndex;
    private List<TargetFilter> targetFilterList;

    public WidgetKey(BICore widgetCore, boolean isCross, boolean isHor, NodeExpander expander, Operator op, int[] startIndex, List<TargetFilter> targetFilterList) {
        this.widgetCore = widgetCore;
        this.isCross = isCross;
        this.isHor = isHor;
        this.expander = expander;
        this.op = op;
        this.startIndex = startIndex;
        this.targetFilterList = targetFilterList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WidgetKey widgetKey = (WidgetKey) o;
        if (isCross != widgetKey.isCross) {
            return false;
        }
        if (isHor != widgetKey.isHor) {
            return false;
        }
        if (!ComparatorUtils.equals(widgetCore, widgetKey.widgetCore)) {
            return false;
        }
        if (expander != null ? !ComparatorUtils.equals(expander, widgetKey.expander) : widgetKey.expander != null) {
            return false;
        }
        if (op.getMaxRow() != widgetKey.op.getMaxRow()){
            return false;
        }
        if (!ComparatorUtils.equals(op.getClickedValue(), widgetKey.op.getClickedValue())) {
            return false;
        }
        if (startIndex != null ? !ComparatorUtils.equals(startIndex, widgetKey.startIndex) : widgetKey.startIndex != null) {
            return false;
        }
        return targetFilterList != null ? ComparatorUtils.equals(targetFilterList, widgetKey.targetFilterList) : widgetKey.targetFilterList == null;

    }

    @Override
    public int hashCode() {
        int result = widgetCore.hashCode();
        result = 31 * result + (isCross ? 1 : 0);
        result = 31 * result + (isHor ? 1 : 0);
        result = 31 * result + (expander != null ? expander.hashCode() : 0);
        result = 31 * result + op.getMaxRow();
        result = 31 * result + Arrays.hashCode(op.getClickedValue());
        result = 31 * result + Arrays.hashCode(startIndex);
        result = 31 * result + (targetFilterList != null ? targetFilterList.hashCode() : 0);
        return result;
    }
}
