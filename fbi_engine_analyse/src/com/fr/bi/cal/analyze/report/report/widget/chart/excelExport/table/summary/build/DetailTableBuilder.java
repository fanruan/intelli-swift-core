package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIBasicTableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.utils.SummaryTableStyleHelper;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/3/30.
 */
public class DetailTableBuilder extends TableAbstractDataBuilder {
    public DetailTableBuilder(Map<Integer, List<JSONObject>> dimAndTar, JSONObject dataJSON) throws Exception {
        super(dimAndTar, dataJSON);
    }

    @Override
    public void initAttrs() throws JSONException {
        initAllAttrs();
        refreshDimsInfo();
    }

    @Override
    public void createHeadersAndItems() throws Exception {
        createTableHeader();
        createTableItems();
    }

    @Override
    public BIExcelTableData createTableData() throws JSONException {
        BIExcelTableData tableDataForExport = new BIExcelTableData(headers, items);
        return tableDataForExport;
    }

    protected void createTableItems() throws Exception {
        JSONArray array = dataJSON.getJSONArray("value");
//        List<ITableItem> tableItems = new ArrayList<ITableItem>();
        for (int i = 0; i < array.length(); i++) {
            ITableItem rowItem = createRowItem(array.getJSONArray(i));
//            tableItems.add(rowItem);
            items.put(rowItem.createJSON());
        }
    }

    private ITableItem createRowItem(JSONArray itemArray) throws JSONException {
        List<ITableItem> rowItems = new ArrayList<ITableItem>();
        for (int j = 0; j < itemArray.length(); j++) {
            if (isDimensionUsable(dimIds.get(j))) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.setType("bi.detail_table_cell");
                item.setdId(dimIds.get(j));
                item.setText(itemArray.length() > j ? itemArray.getString(j) : "");
                item.setStyle(SummaryTableStyleHelper.getBodyStyles("", "").toString());
                rowItems.add(item);
            }
        }
        BIBasicTableItem rowItem = new BIBasicTableItem();
        rowItem.setChildren(rowItems);
        return rowItem;
    }

    private boolean isDimensionUsable(String s) {
        return true;
    }
}
