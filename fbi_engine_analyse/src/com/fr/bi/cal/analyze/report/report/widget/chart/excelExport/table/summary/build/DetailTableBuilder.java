package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build;

import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIBasicTableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.utils.BITableExportDataHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.utils.SummaryTableStyleHelper;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
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
    public void createHeaders() throws Exception {
        createTableHeader();
        removeUnusedHeader();
    }

    private void removeUnusedHeader() {
        Iterator<ITableHeader> iterator = headers.iterator();
        while (iterator.hasNext()){
            ITableHeader header = iterator.next();
            if (!header.isUsed()){
               iterator.remove();
            }
        }
    }

    @Override
    public void createItems() throws Exception {
        createTableItems();
    }

    @Override
    public BIExcelTableData createTableData() throws JSONException {
        BIExcelTableData tableDataForExport = new BIExcelTableData(headers, items);
        return tableDataForExport;
    }

    protected void createTableItems() throws Exception {
        JSONArray array = dataJSON.getJSONArray("value");
        for (int i = 0; i < array.length(); i++) {
            ITableItem rowItem = createRowItem(array.getJSONArray(i));
            items.add(rowItem);
        }
    }

    private ITableItem createRowItem(JSONArray itemArray) throws Exception {
        List<ITableItem> rowItems = new ArrayList<ITableItem>();
        for (int j = 0; j < itemArray.length(); j++) {
            if (isDimensionUsable(dimIds.get(j))) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.setType("bi.detail_table_cell");
                item.setDId(dimIds.get(j));
                item.setText(itemArray.isNull(j) ? "" : itemArray.getString(j));
                item.setStyle(SummaryTableStyleHelper.getBodyStyles("", ""));
                rowItems.add(item);
            }
        }
        BIBasicTableItem rowItem = new BIBasicTableItem();
        rowItem.setChildren(rowItems);
        return rowItem;
    }

    private boolean isDimensionUsable(String id) throws Exception {
        return BITableExportDataHelper.isDimUsed(dimAndTar, id);
    }
}
