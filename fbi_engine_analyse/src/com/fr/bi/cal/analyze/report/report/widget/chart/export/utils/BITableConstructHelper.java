package com.fr.bi.cal.analyze.report.report.widget.chart.export.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.operation.ITableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BITableDataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;

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

    public static void formatCells(BITableDataConstructor data, Map<String, ITableCellFormatOperation> operations) throws Exception {
        for (ITableItem item : data.getItems()) {
            recurisiveFormatText(item, operations);
        }
        if (data.getCrossItems() == null) {
            return;
        } else {
            for (ITableItem childItem : data.getCrossItems()) {
                recurisiveFormatText(childItem, operations);
            }
        }
    }

    private static void recurisiveFormatText(ITableItem item, Map<String, ITableCellFormatOperation> ops) throws Exception {
        if (item.getChildren() != null) {
            for (ITableItem childItem : item.getChildren()) {
                recurisiveFormatText(childItem, ops);
            }
        }
        if (item.getValues() != null) {
            for (ITableItem child : item.getValues()) {
                recurisiveFormatText(child, ops);
            }
        }
        if (item.getValue() != null && item.getDId() != null) {
            if (null != ops.get(item.getDId())) {
                item.setText(ops.get(item.getDId()).formatValues(item.getValue()));
                item.setValue(ops.get(item.getDId()).formatValues(item.getValue()));
            }
        }else {
            item.setText(item.getValue());
        }
    }
}

