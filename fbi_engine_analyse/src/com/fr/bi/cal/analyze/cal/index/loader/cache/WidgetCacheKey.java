package com.fr.bi.cal.analyze.cal.index.loader.cache;

import com.fr.bi.base.BICore;
import com.fr.bi.cal.analyze.cal.result.NodeExpander;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.conf.report.widget.field.target.filter.TargetFilter;
import com.fr.general.ComparatorUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 小灰灰 on 2017/8/1.
 */
public class WidgetCacheKey {
    private long cubeVersion;
    private BICore widgetCore;
    private NodeExpander rowExpander;
    private NodeExpander colExpander;
    private Operator rowOp;
    private int[] rowStartIndex;
    private Operator colOp;
    private int[] colStartIndex;
    private List<TargetFilter> targetFilterList;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WidgetCacheKey widgetCacheKey = (WidgetCacheKey) o;
        if (!ComparatorUtils.equals(widgetCore, widgetCacheKey.widgetCore)) {
            return false;
        }
        if (rowExpander != null ? !ComparatorUtils.equals(rowExpander, widgetCacheKey.rowExpander) : widgetCacheKey.rowExpander != null) {
            return false;
        }
        if (colExpander != null ? !ComparatorUtils.equals(colExpander, widgetCacheKey.colExpander) : widgetCacheKey.colExpander != null) {
            return false;
        }
        if (rowOp.getMaxRow() != widgetCacheKey.rowOp.getMaxRow()){
            return false;
        }
        if (colOp.getMaxRow() != widgetCacheKey.colOp.getMaxRow()){
            return false;
        }
        if (!ComparatorUtils.equals(rowOp.getClickedValue(), widgetCacheKey.rowOp.getClickedValue())) {
            return false;
        }
        if (!ComparatorUtils.equals(colOp.getClickedValue(), widgetCacheKey.colOp.getClickedValue())) {
            return false;
        }
        if (rowStartIndex != null ? !ComparatorUtils.equals(rowStartIndex, widgetCacheKey.rowStartIndex) : widgetCacheKey.rowStartIndex != null) {
            return false;
        }
        if (colStartIndex != null ? !ComparatorUtils.equals(colStartIndex, widgetCacheKey.colStartIndex) : widgetCacheKey.colStartIndex != null) {
            return false;
        }
        return targetFilterList != null ? ComparatorUtils.equals(targetFilterList, widgetCacheKey.targetFilterList) : widgetCacheKey.targetFilterList == null;

    }

    @Override
    public int hashCode() {
        int result = widgetCore.hashCode();
        result = 31 * result + (rowExpander != null ? rowExpander.hashCode() : 0);
        result = 31 * result + (colExpander != null ? colExpander.hashCode() : 0);
        result = 31 * result + rowOp.getMaxRow();
        result = 31 * result + colOp.getMaxRow();
        result = 31 * result + Arrays.hashCode(rowOp.getClickedValue());
        result = 31 * result + Arrays.hashCode(colOp.getClickedValue());
        result = 31 * result + Arrays.hashCode(rowStartIndex);
        result = 31 * result + Arrays.hashCode(colStartIndex);
        result = 31 * result + (targetFilterList != null ? targetFilterList.hashCode() : 0);
        return result;
    }
}
