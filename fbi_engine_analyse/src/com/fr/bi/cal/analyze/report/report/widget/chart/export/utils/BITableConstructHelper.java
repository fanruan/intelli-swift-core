package com.fr.bi.cal.analyze.report.report.widget.chart.export.utils;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator.IExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.TableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BIBasicTableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BITableDataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;
import com.fr.general.ComparatorUtils;
import com.fr.json.JSONArray;

import java.util.List;

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

    public static void formatCells(BITableDataConstructor data, List<TableCellFormatOperation> operations) throws Exception {
        for (ITableItem item : data.getItems()) {
            recurisiveFormatText(item, operations);
        }
        //todo json转对象
        JSONArray resultCrossItems = new JSONArray();
        for (int i = 0; i < data.getCrossItems().length(); i++) {
            ITableItem item = new BIBasicTableItem();
            item.parseJSON(data.getCrossItems().getJSONObject(i));
            recurisiveFormatText(item, operations);
            resultCrossItems.put(item.createJSON());
        }
        data.setCrossItems(resultCrossItems);
    }

    private static void recurisiveFormatText(ITableItem item, List<TableCellFormatOperation> formSettings) {
        for (ITableItem child : item.getChildren()) {
            recurisiveFormatText(item, formSettings);
        }
        for (ITableItem child : item.getValues()) {
            recurisiveFormatText(item, formSettings);
        }
        if (item.getText() != null && item.getDId() != null) {
            for (TableCellFormatOperation operation : formSettings) {
                if (ComparatorUtils.equals(operation.getdId(), item.getdId())) {
                    item.setValue(operation.formatText(item.getText()));
                    break;
                }
            }
        }
    }
}

