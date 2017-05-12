package com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.format.TableCellFormatOperation;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BIBasicTableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BITableDataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableExportDataHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.SummaryTableStyleHelper;
import com.fr.bi.conf.report.widget.IWidgetStyle;
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
    private List<TableCellFormatOperation> styles;

    public DetailTableBuilder(Map<Integer, List<JSONObject>> viewMap, List<TableCellFormatOperation> tableCellFormatOperations, JSONObject dataJSON, IWidgetStyle styleSettings) throws Exception {
        super(viewMap, dataJSON, styleSettings);
        this.styles = tableCellFormatOperations;
    }

    @Override
    public void initAttrs() throws Exception {
        initAllAttrs();
        refreshDimsInfo();
    }

    @Override
    public void amendment() throws Exception {

    }

    @Override
    public void createTargetStyles() {

    }

    @Override
    public void createHeaders() throws Exception {
        createTableHeader();
        removeUnusedHeader();
    }

    private void removeUnusedHeader() {
        Iterator<ITableHeader> iterator = headers.iterator();
        while (iterator.hasNext()) {
            ITableHeader header = iterator.next();
            if (!header.isUsed()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void createItems() throws Exception {
        createTableItems();
    }

    @Override
    public BITableDataConstructor createTableData() throws JSONException {
        BITableDataConstructor tableDataForExport = new BITableDataConstructor(headers, items,this.styleSetting);
        return tableDataForExport;
    }

    protected void createTableItems() throws Exception {
        JSONArray array = data.getJSONArray("value");
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
                item.setDId(dimIds.get(j));
                item.setText(itemArray.isNull(j) ? "" : itemArray.getString(j));
                item.setStyle(SummaryTableStyleHelper.getBodyStyles(styleSetting.getThemeStyle(), styleSetting.getTableStyleGroup(), j));
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
