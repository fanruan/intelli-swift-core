package com.fr.bi.cal.analyze.report.report.widget.chart.export.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.constructor.DataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.style.BITableWidgetStyle;
import com.fr.bi.conf.report.WidgetType;

import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/26.
 */
public class BITableConstructHelper {

    public static DataConstructor buildTableData(IExcelDataBuilder builder) throws Exception {
        builder.initAttrs();
        builder.amendment();
        builder.createHeaders();
        builder.createItems();
        builder.createTargetStyles();
        return builder.createTableData();
    }

    /*
    * 明细表样式特殊处理
    * */
    public static void formatCells(DataConstructor data, Map<String, ITableCellFormatOperation> operations, BITableWidgetStyle style) throws Exception {
        boolean isDetail = data.getWidgetType() == WidgetType.DETAIL.getType();
        for (ITableHeader header : data.getHeaders()) {
            if (header.isSum()) {
                header.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(style.getThemeColor(), style.getTableStyleGroup()));
            }
            header.setStyles(SummaryTableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
        }

        if (data.getItems().size() != 0) {
            traversalItems(data.getItems(), operations, 0, 0, style, isDetail);
        }

        if (data.getCrossItems() != null) {
            for (ITableItem childItem : data.getCrossItems()) {
                traversalCrossItems(childItem, operations);
            }
        }

        if (data.getCrossHeaders() != null) {
            for (ITableHeader crossHeader : data.getCrossHeaders()) {
                crossHeader.setStyles(SummaryTableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
            }
        }

    }

    private static void traversalItems(List<ITableItem> items, Map<String, ITableCellFormatOperation> ops, int layerIndex, int rowIndex, BITableWidgetStyle style, boolean isDetail) throws Exception {
        for (ITableItem item : items) {
            if (item.getChildren() != null) {
                traversalItems(item.getChildren(), ops, layerIndex + 1, rowIndex, style, isDetail);
            }
            setText(ops, item);
            if (item.getValues() != null) {
                for (ITableItem it : item.getValues()) {
                    setText(ops,it);
                }
                if (item.getChildren() != null) {
                    for (ITableItem it : item.getValues()) {
                        it.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(style.getThemeColor(), style.getTableStyleGroup()));
                    }
                } else {
                    for (ITableItem it : item.getValues()) {
                        it.setStyles(SummaryTableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), rowIndex));
                    }
                }
            }
            //第一列换行
            if (!isDetail || (isDetail && layerIndex == 0)) {
                rowIndex++;
            }
        }
    }

    private static void traversalCrossItems(ITableItem item, Map<String, ITableCellFormatOperation> ops) throws Exception {
        if (item.getChildren() != null) {
            for (ITableItem childItem : item.getChildren()) {
                traversalCrossItems(childItem, ops);
            }
        }
        if (item.getValues() != null) {
            for (ITableItem it : item.getValues()) {
                setText(ops, it);
            }
        }
        setText(ops, item);
    }

    private static void setText(Map<String, ITableCellFormatOperation> ops, ITableItem it) throws Exception {
        if (null != ops.get(it.getDId())) {
            it.setText(ops.get(it.getDId()).formatValues(it.getValue()));
        } else {
            it.setText(it.getValue());
        }
    }
}

