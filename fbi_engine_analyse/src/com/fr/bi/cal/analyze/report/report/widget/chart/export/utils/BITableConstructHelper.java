package com.fr.bi.cal.analyze.report.report.widget.chart.export.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.constructor.DataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.style.BITableWidgetStyle;
import com.fr.bi.conf.report.WidgetType;
import com.fr.json.JSONException;

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

        if (data.getItems().size() != 0) {
            if (isDetail) {
                for (int i = 0; i < data.getItems().size(); i++) {
                    ITableItem item = data.getItems().get(i);
                    formatItemText(operations, item);
                    item.setStyles(BITableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), i));
                    for (ITableItem child : item.getChildren()) {
                        child.setStyles(BITableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), i));
                        formatItemText(operations, child);
                    }
                }
            } else {
                for (ITableHeader header : data.getHeaders()) {
                    header.setStyles(BITableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
                    formatHeaderText(operations, header);
                }
                traversalItems(data.getItems(), operations, 0, 0, style);
            }
        }

        if (data.getCrossItems() != null) {
            for (ITableItem childItem : data.getCrossItems()) {
                traversalCrossItems(childItem, operations, style);
            }
        }

        if (data.getCrossHeaders() != null) {
            for (ITableHeader crossHeader : data.getCrossHeaders()) {
                crossHeader.setStyles(BITableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
            }
        }

    }

    /*
    * 首行汇总为深色
    * */
    private static void traversalItems(List<ITableItem> items, Map<String, ITableCellFormatOperation> ops, int rowIndex, int layer, BITableWidgetStyle style) throws Exception {
        for (ITableItem item : items) {
            rowIndex++;
            if (item.getChildren() != null) {
                traversalItems(item.getChildren(), ops, rowIndex, layer + 1, style);
            }
            formatItemText(ops, item);
            setItemTextStyle(ops, item);
            setStyle(rowIndex, layer, style, item);
            if (item.getValues() != null) {
                for (ITableItem it : item.getValues()) {
                    formatItemText(ops, it);
                    setItemTextStyle(ops, it);
                    if ((layer == 0 && item.getValues() != null) || item.isSum()) {
                        it.setStyles(BITableStyleHelper.getLastSummaryStyles(style.getThemeColor(), style.getTableStyleGroup()));
                    } else {
                        it.setStyles(BITableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), rowIndex));
                    }
                }
            }
        }
    }

    private static void setStyle(int rowIndex, int layer, BITableWidgetStyle style, ITableItem item) throws JSONException {
        boolean isOutSummary = layer == 0 && item.getValues() != null;
        if (isOutSummary || item.isSum()) {
            item.setStyles(BITableStyleHelper.getLastSummaryStyles(style.getThemeColor(), style.getTableStyleGroup()));
        } else {
            item.setStyles(BITableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), rowIndex));
        }
    }

    private static void traversalCrossItems(ITableItem item, Map<String, ITableCellFormatOperation> ops, BITableWidgetStyle style) throws Exception {
        if (item.getChildren() != null) {
            for (ITableItem childItem : item.getChildren()) {
                traversalCrossItems(childItem, ops, style);
            }
        }
        if (item.getValues() != null) {
            for (ITableItem it : item.getValues()) {
                formatItemText(ops, it);
            }
        }
        item.setStyles(BITableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
        formatItemText(ops, item);
    }

    private static void formatItemText(Map<String, ITableCellFormatOperation> ops, ITableItem it) throws Exception {
        if (null != ops.get(it.getDId())) {
            it.setText(ops.get(it.getDId()).formatTextValues(it.getValue()));
        } else {
            it.setText(it.getValue());
        }
    }

    private static void formatHeaderText(Map<String, ITableCellFormatOperation> ops, ITableHeader header) throws Exception {
        if (null != ops.get(header.getdID())) {
            header.setText(ops.get(header.getdID()).formatTextValues(header.getText()));
        }
    }

    private static void setItemTextStyle(Map<String, ITableCellFormatOperation> ops, ITableItem it) throws Exception {
        if (null != ops.get(it.getDId())) {
            it.setTextStyles(ops.get(it.getDId()).createTextStyle(it.getValue()));
        }
    }
}

