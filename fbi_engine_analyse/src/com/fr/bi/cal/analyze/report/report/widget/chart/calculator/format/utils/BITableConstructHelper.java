package com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.builder.ITableSCDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.calculator.item.constructor.DataConstructor;
import com.fr.bi.conf.fs.tablechartstyle.BIWidgetBackgroundAttr;
import com.fr.bi.conf.report.WidgetType;
import com.fr.bi.conf.report.conf.settings.BIWidgetSettings;
import com.fr.bi.conf.report.style.table.BITableStyleHelper;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/26.
 */
public class BITableConstructHelper {

    public static DataConstructor buildTableData(ITableSCDataBuilder builder) throws Exception {
        builder.initAttrs();
        builder.amendment();
        builder.createHeaders();
        builder.createItems();
        builder.createTargetStyles();
        return builder.createTableData();
    }

    /*
    * header的数据格式是不需要format的
    * */
    public static void formatCells(DataConstructor data, Map<String, ITableCellFormatOperation> operations, BIWidgetSettings style, BIWidgetBackgroundAttr backgroundColor) throws Exception {
        boolean isDetail = data.getWidgetType() == WidgetType.DETAIL.getType();
        for (ITableHeader header : data.getHeaders()) {
            header.setStyles(BITableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
            formatHeaderText(operations, header);
        }

        if (data.getItems().size() != 0) {
            if (isDetail) {
                for (int i = 0; i < data.getItems().size(); i++) {
                    ITableItem item = data.getItems().get(i);
                    for (ITableItem child : item.getChildren()) {
                        formatText(operations, child);
                        setTextStyle(operations, child, backgroundColor);
                        setDetailCellStyle(i, 1, style, child);
                    }
                }
            } else {
                traversalItems(data.getItems(), operations, 0, 0, style, backgroundColor);
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
    private static void traversalItems(List<ITableItem> items, Map<String, ITableCellFormatOperation> ops, int rowIndex, int layer, BIWidgetSettings style, BIWidgetBackgroundAttr backgroundColor) throws Exception {
        rowIndex++;
        for (ITableItem item : items) {
            rowIndex++;
            boolean isInnerSum = item.getChildren() != null && !item.getChildren().isEmpty();
            boolean isLastSum = item.isSum() || layer == 0;
            formatItemCell(ops, rowIndex, style, item, false, isLastSum, backgroundColor);
            if (item.getValues() != null) {
                for (ITableItem it : item.getValues()) {
                    formatItemCell(ops, rowIndex, style, it, isInnerSum, isLastSum, backgroundColor);
                }
            }

            if (item.getChildren() != null) {
                traversalItems(item.getChildren(), ops, rowIndex, layer + 1, style, backgroundColor);
            }
        }
    }

    private static void formatItemCell(Map<String, ITableCellFormatOperation> ops, int rowIndex, BIWidgetSettings style, ITableItem item, boolean isInnerSum, boolean isLastSum, BIWidgetBackgroundAttr backgroundColor) throws Exception {
        formatText(ops, item);
        setTextStyle(ops, item, backgroundColor);
        setCellStyle(rowIndex, style, item, isInnerSum, isLastSum);
    }

    private static void setCellStyle(int rowIndex, BIWidgetSettings style, ITableItem item, boolean isInnerSum, boolean isLastSum) throws JSONException {
        if (isLastSum) {
            item.setStyles(BITableStyleHelper.getLastSummaryStyles(style.getThemeColor(), style.getTableStyleGroup()));
        } else if (isInnerSum) {
            item.setStyles(BITableStyleHelper.getInnerSumStyles(style.getThemeColor(), style.getTableStyleGroup()));
        } else {
            item.setStyles(BITableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), rowIndex));
        }
    }

    private static void setDetailCellStyle(int rowIndex, int layer, BIWidgetSettings style, ITableItem item) throws JSONException {
        boolean isLastSum = layer == 0 && item.getValues() == null;
        if (isLastSum || item.isSum()) {
            item.setStyles(BITableStyleHelper.getLastSummaryStyles(style.getThemeColor(), style.getTableStyleGroup()));
        } else {
            item.setStyles(BITableStyleHelper.getBodyStyles(style.getThemeColor(), style.getTableStyleGroup(), rowIndex));
        }
    }

    private static void traversalCrossItems(ITableItem item, Map<String, ITableCellFormatOperation> ops, BIWidgetSettings style) throws Exception {
        if (item.getChildren() != null) {
            for (ITableItem childItem : item.getChildren()) {
                traversalCrossItems(childItem, ops, style);
            }
        }
        if (item.getValues() != null) {
            for (ITableItem it : item.getValues()) {
                formatText(ops, it);
            }
        }
        item.setStyles(BITableStyleHelper.getHeaderStyles(style.getThemeColor(), style.getTableStyleGroup()));
        formatText(ops, item);
    }

    private static void formatText(Map<String, ITableCellFormatOperation> ops, ITableItem it) throws Exception {
        if (null != ops.get(it.getDId())) {
            it.setText(ops.get(it.getDId()).formatItemTextValues(String.valueOf(it.getValue())));
        } else {
            it.setText(String.valueOf(it.getValue()));
        }
    }

    private static void formatHeaderText(Map<String, ITableCellFormatOperation> ops, ITableHeader header) throws Exception {
        if (null != ops.get(header.getdID())) {
            header.setText(ops.get(header.getdID()).formatHeaderText(header.getText()));
        }
    }

    private static void setTextStyle(Map<String, ITableCellFormatOperation> ops, ITableItem it, BIWidgetBackgroundAttr backgroundColor) throws Exception {
        JSONObject style=JSONObject.create();
        if (null != ops.get(it.getDId())) {
            style = ops.get(it.getDId()).createItemTextStyle(String.valueOf(it.getValue()));
        }
        it.setTextStyles(style.put("color",BITableStyleHelper.getContrastColor(backgroundColor.getValue())));
    }
}

