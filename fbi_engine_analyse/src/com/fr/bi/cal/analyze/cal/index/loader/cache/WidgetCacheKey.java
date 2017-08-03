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
    private BICore widgetCore;
    private NodeExpander rowExpander;
    private NodeExpander colExpander;
    private Operator rowOp;
    private int[] rowStartIndex;
    private Operator colOp;
    private int[] colStartIndex;
    private List<TargetFilter> targetFilterList;

    public static WidgetCacheKey createKey(BICore widgetCore, NodeExpander rowExpander, NodeExpander colExpander, Operator rowOp, int[] rowStartIndex, Operator colOp, int[] colStartIndex, List<TargetFilter> targetFilterList){
        return new WidgetCacheKey(widgetCore, rowExpander, colExpander, rowOp, rowStartIndex, colOp, colStartIndex, targetFilterList);
    }

    private WidgetCacheKey(BICore widgetCore, NodeExpander rowExpander, NodeExpander colExpander, Operator rowOp, int[] rowStartIndex, Operator colOp, int[] colStartIndex, List<TargetFilter> targetFilterList) {
        this.widgetCore = widgetCore;
        this.rowExpander = rowExpander;
        this.colExpander = colExpander;
        this.rowOp = rowOp;
        this.rowStartIndex = rowStartIndex;
        this.colOp = colOp;
        this.colStartIndex = colStartIndex;
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

        WidgetCacheKey that = (WidgetCacheKey) o;

        if (!ComparatorUtils.equals(widgetCore, that.widgetCore)) {
            return false;
        }
        if (rowExpander != null ? !ComparatorUtils.equals(rowExpander, that.rowExpander) : that.rowExpander != null) {
            return false;
        }
        if (colExpander != null ? !ComparatorUtils.equals(colExpander, that.colExpander) : that.colExpander != null) {
            return false;
        }
        if (rowOp != null ? !ComparatorUtils.equals(rowOp, that.rowOp) : that.rowOp != null) {
            return false;
        }
        if (!ComparatorUtils.equals(rowStartIndex, that.rowStartIndex)) {
            return false;
        }
        if (colOp != null ? !colOp.equals(that.colOp) : that.colOp != null) {
            return false;
        }
        if (!ComparatorUtils.equals(colStartIndex, that.colStartIndex)) {
            return false;
        }
        return targetFilterList != null ? ComparatorUtils.equals(targetFilterList, that.targetFilterList) : that.targetFilterList == null;
    }

    @Override
    public int hashCode() {
        int result = widgetCore.hashCode();
        result = 31 * result + (rowExpander != null ? rowExpander.hashCode() : 0);
        result = 31 * result + (colExpander != null ? colExpander.hashCode() : 0);
        result = 31 * result + (rowOp != null ? rowOp.getMaxRow() : 0);
        result = 31 * result + Arrays.hashCode(rowStartIndex);
        result = 31 * result + (colOp != null ? colOp.getMaxRow() : 0);
        result = 31 * result + Arrays.hashCode(colStartIndex);
        result = 31 * result + (targetFilterList != null ? targetFilterList.hashCode() : 0);
        return result;
    }
}
