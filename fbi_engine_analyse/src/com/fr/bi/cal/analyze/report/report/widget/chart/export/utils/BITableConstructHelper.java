package com.fr.bi.cal.analyze.report.report.widget.chart.export.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BITableDataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.style.BITableWidgetStyle;

import java.util.Map;

/**
 * Created by Kary on 2017/2/26.
 */
public class BITableConstructHelper {

    public static BITableDataConstructor buildTableData(IExcelDataBuilder builder) throws Exception {
        builder.initAttrs();
        builder.amendment();
        builder.createHeaders();
        builder.createItems();
        builder.createTargetStyles();
        return builder.createTableData();
    }

    public static void formatCells(BITableDataConstructor data, Map<String, ITableCellFormatOperation> operations, BITableWidgetStyle style) throws Exception {
        for (ITableHeader header : data.getHeaders()) {
            if (header.isSum()) {
                header.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(style.getThemeColor(), style.getTableStyleGroup()));
            }
            header.setStyles(SummaryTableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
        }


        if (data.getItems().size() != 0) {
            traversalItems(data.getItems(), operations, 0, 0, style);
        }

        if (data.getCrossHeaders() != null) {
            for (ITableHeader crossHeader : data.getCrossHeaders()) {
                crossHeader.setStyles(SummaryTableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
            }
        }

        if (data.getCrossItems() != null) {
            for (ITableItem childItem : data.getCrossItems()) {
                traversalCrossItems(childItem, operations);
            }
        }
    }

    private static void traversalItems(java.util.List<ITableItem> items, Map<String, ITableCellFormatOperation> ops, int layerIndex, int rowIndex, BITableWidgetStyle style) throws Exception {
        for (ITableItem item : items) {
            if (item.getChildren() != null) {
                traversalItems(item.getChildren(), ops, layerIndex + 1, rowIndex, style);
            }
            if (item.getValues() != null) {
                for (ITableItem it : item.getValues()) {
                    it.setStyles(SummaryTableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), rowIndex));
                    if (null != ops.get(it.getDId())) {
                        it.setText(ops.get(it.getDId()).formatValues(it.getValue()));
                        it.setValue(ops.get(it.getDId()).formatValues(it.getValue()));
                    }
                }
            }
            if (item.getText() != null || item.getValue() != null) {
                item.setStyles(SummaryTableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), rowIndex));
            }
            rowIndex++;
        }
    }

    private static void traversalCrossItems(ITableItem item, Map<String, ITableCellFormatOperation> ops) throws Exception {
        if (item.getChildren() != null) {
            for (ITableItem childItem : item.getChildren()) {
                traversalCrossItems(childItem, ops);
            }
        }
        if (item.getValue() != null && item.getDId() != null) {
            if (null != ops.get(item.getDId())) {
                item.setText(ops.get(item.getDId()).formatValues(item.getValue()));
                item.setValue(ops.get(item.getDId()).formatValues(item.getValue()));
            }
        } else {
            item.setText(item.getValue());
        }
    }
}

