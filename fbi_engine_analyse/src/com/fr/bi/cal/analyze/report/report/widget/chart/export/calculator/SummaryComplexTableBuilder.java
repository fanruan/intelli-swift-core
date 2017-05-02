package com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.*;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableExportDataHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.SummaryTableStyleHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.node.ReportNode;
import com.fr.bi.cal.analyze.report.report.widget.style.BITableWidgetStyle;
import com.fr.bi.conf.report.widget.IWidgetStyle;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by Kary on 2017/2/27.
 */
public class SummaryComplexTableBuilder extends TableAbstractDataBuilder {
    private String outer_sum = "__outer_sum_";
    boolean showRowTotal = true;
    private List<DimAndTargetStyle> dimAndTargetStyles;
    private String data;

    public SummaryComplexTableBuilder(Map<Integer, List<JSONObject>> dimAndTar, List<DimAndTargetStyle> dimAndTargetStyles, JSONObject dataJSON, IWidgetStyle styleSettings) throws Exception {
        super(dimAndTar, dataJSON, styleSettings);
        this.data = dataJSON.toString();
        this.dimAndTargetStyles = dimAndTargetStyles;
    }

    @Override
    public void initAttrs() throws Exception {
        super.initAllAttrs();
        refreshDimsInfo();

    }

    @Override
    public void amendment() throws Exception {
        //无行表头
        boolean isRegionExist = isColRegionExist() && !isRowRegionExist();
        if (isRegionExist) {
            amendmentData();
        }
    }

    @Override
    public void createHeaders() throws Exception {

    }

    @Override
    public void createItems() throws Exception {
        //正常复杂表
        if (this.isColRegionExist() && this.isRowRegionExist()) {
            createComplexTableItems();
            createComplexTableHeader();
            setOtherComplexAttrs();
            return;
        }
        //无行表头
        //补全该二维数组
        boolean isRegionExist = isColRegionExist() && !isRowRegionExist();
        JSONArray oneDimTableData = new JSONArray();
        if (isRegionExist) {
            JSONArray clonedData = new JSONArray();
            if (getColRegions().size() == 1) {
                clonedData.put(data);
            }
            for (int i = 0; i < clonedData.length(); i++) {
                JSONObject cData = new JSONObject(clonedData.getString(i));
                JSONObject t = new JSONObject().put("c", getTopOfCrossByGroupData(cData.getJSONArray("c")));
                JSONObject l = new JSONObject().put("s", cData);
                JSONObject tableData = new JSONObject();
                tableData.put("t", t).put("l", l);
                oneDimTableData.put(i, tableData);
            }
            data = new JSONArray().put(oneDimTableData).toString();
            createComplexTableItems();
            createComplexTableHeader();
            setOtherComplexAttrs();
            return;
        }
        //无列表头 有指标 当作多个普通分组表
        if (isRowRegionExist() && !isColRegionExist() && targetIds.size() > 0) {
            createTableHeader();
            createMultiGroupItems();
            setOtherAttrs();
            return;
        }
        createTableHeader();
        createTableItems();
        setOtherAttrs();
    }

    //仅有行表头和指标的情况
    private void createMultiGroupItems() throws Exception {
        JSONObject dataJson = new JSONObject(data);
        if (dataJson.has("c")) {
            createTableItems();
            return;
        }
        List<ITableItem> tempItems = new ArrayList<ITableItem>();
        for (int i = 0; i < dataJson.length(); i++) {
            JSONObject rowTable = dataJson.getJSONObject(String.valueOf(i));
            Map<Integer, List<JSONObject>> dimOb = getDimsByDataPos(i, 0);
            List<JSONObject> list = dimOb.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1));
            List<String> dimIds = new ArrayList<String>();
            for (JSONObject jsonObject : list) {
                dimIds.add(jsonObject.getString("dId"));
            }
            BIBasicTableItem item = new BIBasicTableItem();
            item.setChildren(createCommonTableItems(rowTable.getString("c"), 0, null, dimIds, null));
            //汇总
            if (showRowTotal && rowTable.has("s")) {
                JSONArray outerValues = new JSONArray();
                JSONArray s = rowTable.getJSONArray("s");
                if (dimIds.size() > 0) {
                    for (int j = 0; j < s.length(); j++) {
                        createItem(outerValues, s, j);
                    }
                    item.setValue(outerValues);
                } else {
                    //使用第一个值作为一个维度
                    for (int k = 0; k < s.length(); k++) {
                        createItem(outerValues, s, k);
                    }
                    BIBasicTableItem itemNode = new BIBasicTableItem();
                    itemNode.setType("bi.my_table_cell");
                    itemNode.setDId(targetIds.get(0));
                    itemNode.setText(s.getString(0));
                    itemNode.setClicked(new JSONArray().put(new JSONObject()));
                    itemNode.setStyle(SummaryTableStyleHelper.getLastSummaryStyles("", ""));
                    itemNode.setSum(true);
                    itemNode.setTag(UUID.randomUUID().toString());
                    itemNode.setValue(outerValues);
                    item.getChildren().add(itemNode);
                    item.setValue(new JSONArray().put(item));
                }
            }
            tempItems.add(item);
        }
        parseColTableItems(tempItems);
    }

    private void createItem(JSONArray outerValues, JSONArray s, int j) throws JSONException {
        BIBasicTableItem itemNode = new BIBasicTableItem();
        itemNode.setType("bi.my_table_cell");
        itemNode.setDId(targetIds.get(j));
        itemNode.setText(s.getString(j));
        itemNode.setClicked(new JSONArray().put(new JSONObject()));
        itemNode.setStyle(SummaryTableStyleHelper.getLastSummaryStyles("", ""));
        outerValues.put(itemNode);
    }


    private void setOtherComplexAttrs() {
    }

    private void createComplexTableHeader() throws Exception {
        createCrossTableHeader();
        //补齐header的长度
        int rowLength = getLargestLengthOfRowRegions();
        int clolLength = getLargestLengthOfColRegions();
        ITableHeader lastDimHeader = headers.size() > dimIds.size() ? headers.get(dimIds.size() - 1) : null;
        ITableHeader lastCrossDimHeader = crossDimIds.size() > crossHeaders.size() ? crossHeaders.get(crossDimIds.size() - 1) : null;
// FIXME: 2017/3/6
        int count = 0;
        while (count < rowLength - dimIds.size()) {
            ITableHeader header = lastDimHeader;
            if (null != header) {
                this.headers.set(dimIds.size(), header);
            }
            count++;
        }
        count = 0;
        while (count < rowLength - crossDimIds.size()) {
            count++;
            crossHeaders.add(crossDimIds.size(), lastCrossDimHeader);
        }

    }

    private int getLargestLengthOfColRegions() throws JSONException {
        List<JSONArray> regions = getRowRegions();
        int length = 0;
        for (JSONArray region : regions) {
            if (region.length() > length) {
                length = region.length();
            }
        }
        return length;
    }

    public int getLargestLengthOfRowRegions() throws JSONException {
        List<JSONArray> regions = getColRegions();
        int length = 0;
        for (JSONArray region : regions) {
            if (region.length() > length) {
                length = region.length();
            }
        }
        return length;
    }

    /**
     * 基本的复杂表结构
     * 有几个维度的分组表示就有几个表
     * view: {10000: [a, b], 10001: [c, d]}, 20000: [e, f], 20001: [g, h], 20002: [i, j], 30000: [k]}
     * 表示横向（类似与交叉表）会有三个表，纵向会有两个表
     */
    private void createComplexTableItems() throws Exception {
        List<ITableItem> tempItems = new ArrayList<ITableItem>();
        List<ITableItem> tempCrossItems = new ArrayList<ITableItem>();
        // 如果行表头和列表头都只有一个region构造一个二维的数组

//        if (new JSONObject(data).has("l") && new JSONObject(data).has("t")) {
        if (!BIJsonUtils.isArray(data)) {
            JSONObject rowValues = new JSONObject();
            createTempItems(tempItems, tempCrossItems, 0, rowValues, new JSONObject(data), 0);
        } else {
            for (int i = 0; i < new JSONArray(data).length(); i++) {
                JSONObject rowValues = new JSONObject();
                JSONArray rowTables = new JSONArray(data).getJSONArray(i);
                for (int j = 0; j < rowTables.length(); j++) {
                    JSONObject tableData = rowTables.getJSONObject(j);
                    createTempItems(tempItems, tempCrossItems, i, rowValues, tableData, j);
                }
            }
        }
        parseColTableItems(tempItems);
        parseRowTableCrossItems(tempCrossItems);
    }

    private void createTempItems(List<ITableItem> tempItems, List<ITableItem> tempCrossItems, int i, JSONObject rowValues, JSONObject tableData, int j) throws Exception {
        //parse一个表结构
        Map<Integer, List<JSONObject>> dimAndTar = getDimsByDataPos(i, j);
        BIExcelTableData singleTable = createSingleCrossTableItems(tableData, dimAndTar);
        for (int k = 0; k < singleTable.createJSON().getJSONArray("items").length(); k++) {
            parseRowTableItems(singleTable.createJSON().getJSONArray("items").getJSONObject(k), rowValues);
        }
        if (j == 0) {
            for (int length = 0; length < singleTable.createJSON().getJSONArray("items").length(); length++) {
                tempItems.add(createItemByRowValues(singleTable.getItems().get(length), rowValues));
            }
        }
        if (i == 0) {
            for (int length = 0; length < singleTable.createJSON().getJSONArray("crossItems").length(); length++) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.parseJSON(singleTable.createJSON().getJSONArray("crossItems").getJSONObject(length));
                tempCrossItems.add(item);
            }
        }
    }

    private ITableItem createItemByRowValues(ITableItem item, JSONObject rowValues) throws JSONException {
        if (null == rowValues || rowValues.length() == 0) {
            return item;
        }
        item.setValue(rowValues.has(outer_sum) ? rowValues.getJSONArray(outer_sum) : new JSONArray());
        for (ITableItem tableItem : item.getChildren()) {
            tableItem.setValue(rowValues.getJSONArray(tableItem.getDId() + tableItem.getText()));
        }
        return item;
    }

    private void parseRowTableCrossItems(List<ITableItem> tempCrossItems) throws Exception {
        JSONArray crossArray = new JSONArray();
        for (ITableItem tempCrossItem : tempCrossItems) {
            crossArray.put(tempCrossItem.createJSON());
        }
        crossItems.put(new JSONObject().put("children", crossArray));
    }

    // 处理列 针对于children
    // 要将所有的最外层的values处理成children
    private void parseColTableItems(List<ITableItem> tempItems) throws Exception {
        List<ITableItem> children = new ArrayList<ITableItem>();
        for (int i = 0; i < tempItems.size(); i++) {
            children.add(tempItems.get(i));
            boolean isSummary = showRowTotal && isColRegionExist() && isRowRegionExist() && isOnlyCrossAndTarget();
            if (isSummary) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.setType("bi.page_table_cell");
                item.setText("summary");
                item.setTag(UUID.randomUUID().toString());
                JSONArray tempArray = new JSONArray();
                for (ITableItem tempItem : tempItems) {
                    tempArray.put(tempItem.createJSON());
                }
                item.setValue(tempArray);
                item.setStyle(SummaryTableStyleHelper.getLastSummaryStyles("", ""));
                children.add(item);
            }
            items = new ArrayList<ITableItem>();
            BIBasicTableItem tempItem = new BIBasicTableItem();
            tempItem.setChildren(children);
            items.add(tempItem);
        }
    }

    //对于首层，可以以行号作为key，其他层以dId + text作为key
    //一般只处理有values（同时有children的表示合计、否则表示相应的值）
    private void parseRowTableItems(JSONObject data, JSONObject rowValues) throws JSONException {
        //最外层的合计 可以通过data中是否包含dId来确定
        if (data.has("values")) {

            JSONArray values = data.getJSONArray("values");
            if (data.has("children") && !data.has("dId")) {
                if (rowValues.has(outer_sum)) {
                    for (int i = 0; i < data.getJSONArray("values").length(); i++) {
                        rowValues.getJSONArray(outer_sum).put(data.getJSONArray("values").get(i));
                    }
                } else {
                    if (BIJsonUtils.isArray(data.getString("values"))) {
                        for (int i = 0; i < values.length(); i++) {
                            if (!rowValues.has(outer_sum)) {
                                rowValues.put(outer_sum, new JSONArray());
                            }
                            rowValues.getJSONArray(outer_sum).put(values.get(i));
                        }
                    }
                }
            }
            if (data.has("dId")) {
                String itemId = data.getString("dId") + data.getString("text");
                if (rowValues.has(itemId)) {
                    for (int i = 0; i < values.length(); i++) {
                        rowValues.getJSONArray(itemId).put(values.get(i));
                    }
                } else {
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < values.length(); i++) {
                        array.put(values.get(i));
                    }
                    rowValues.put(itemId, array);
                }
            }
        }
        if (data.has("children") && data.getJSONArray("children").length() > 0) {
            for (int i = 0; i < data.getJSONArray("children").length(); i++) {
                JSONObject child = data.getJSONArray("children").getJSONObject(i);
                parseRowTableItems(child, rowValues);
            }
        }
    }

    private Map<Integer, List<JSONObject>> getDimsByDataPos(int row, int col) throws JSONException {
        JSONObject allDims = new JSONObject();
        for (Integer integer : this.dimAndTar.keySet()) {
            for (JSONObject object : this.dimAndTar.get(integer)) {
                allDims.put(object.getString("dId"), object);
            }
        }
        List<JSONArray> rowRegions = getRowRegions();
        List<JSONArray> colRegions = getColRegions();
        JSONArray dimIds = rowRegions.size() > 0 ? rowRegions.get(row) : new JSONArray();
        JSONArray crossDimIds = colRegions.size() > 0 ? colRegions.get(col) : new JSONArray();

        List<JSONObject> dimIdObj = new ArrayList<JSONObject>();
        for (int i = 0; i < dimIds.length(); i++) {
            dimIdObj.add(allDims.getJSONObject(dimIds.getString(i)));
        }

        List<JSONObject> crossDimIdObj = new ArrayList<JSONObject>();
        for (int i = 0; i < crossDimIds.length(); i++) {
            crossDimIdObj.add(allDims.getJSONObject(crossDimIds.getString(i)));
        }
        List<JSONObject> targetIdObj = new ArrayList<JSONObject>();
        for (String targetId : targetIds) {
            targetIdObj.add(allDims.getJSONObject(targetId));
        }
        Map<Integer, List<JSONObject>> dimAndTars = new HashMap<Integer, List<JSONObject>>();
        dimAndTars.put(Integer.valueOf(BIReportConstant.REGION.DIMENSION1), dimIdObj);
        dimAndTars.put(Integer.valueOf(BIReportConstant.REGION.DIMENSION2), crossDimIdObj);
        dimAndTars.put(Integer.valueOf(BIReportConstant.REGION.TARGET1), targetIdObj);
        return dimAndTars;
    }

    private BIExcelTableData createSingleCrossTableItems(JSONObject tableData, Map<Integer, List<JSONObject>> dimsByDataPos) throws Exception {
        SummaryCrossTableDataBuilder builder = new SummaryCrossTableDataBuilder(dimsByDataPos, tableData, new BITableWidgetStyle());
        builder.initAttrs();
        builder.createHeaders();
        builder.createItems();
        BIExcelTableData data = builder.createTableData();
        return data;
    }

    //获取有效的列表头区域
    private boolean isRowRegionExist() throws JSONException {
        return getRowRegions().size() > 0;
    }

    //行表头是否存在
    private boolean isColRegionExist() throws JSONException {
//        return dimAndTar.containsKey(Integer.valueOf(BIReportConstant.REGION.DIMENSION1)) && dimAndTar.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1)).size() > 0;
        return getColRegions().size() > 0;
    }

    private boolean isOnlyCrossAndTarget() throws JSONException {
        return !this.isRowRegionExist() &&
                this.isColRegionExist() &&
                this.targetIds.size() > 0;
    }

    @Override
    public BIExcelTableData createTableData() throws JSONException {
        BIExcelTableData tableDataForExport = new BIExcelTableData(headers, items, crossHeaders, crossItems);
        return tableDataForExport;
    }

    //获取有效的行表头区域
    public List<JSONArray> getRowRegions() throws JSONException {
        List<JSONArray> rowRegions = new ArrayList<JSONArray>();
        for (Integer regionId : dimAndTar.keySet()) {
            JSONArray temp = new JSONArray();
            if (BITableExportDataHelper.isDimensionRegion1ByRegionType(regionId)) {
                List<JSONObject> list = dimAndTar.get(regionId);
                for (JSONObject object : list) {
                    temp.put(object.getString("dId"));
                }
                rowRegions.add(temp);
            }
        }
        return rowRegions;
    }

    protected JSONArray createCrossItems(JSONObject top) throws Exception {
        JSONObject crossItem = new JSONObject();
        List children = createCrossPartItems(top.getJSONArray("c"), 0, new ReportNode());
        crossItem.put("children", children);
        if (showColTotal) {
            if (getRowRegions().size() > 0) {
                for (int i = 0; i < targetIds.size(); i++) {
                    BIBasicTableItem item = new BIBasicTableItem();
                    item.setType("bi.my_table_cell");
                    item.setDId(targetIds.get(i));
                    item.setText("summary");
                    item.setTag(String.valueOf(UUID.randomUUID()));
                    item.setStyle(SummaryTableStyleHelper.getHeaderStyles("self.getThemeColor()", styleSetting.getWsTableStyle()));
                    item.setSum(true);
                    children.add(item.createJSON());
//                    crossItem.put("children", crossItem.getJSONArray("children").put(item.createJSON()));
                }

            } else {
                for (String targetId : targetIds) {
                    BIBasicTableItem item = new BIBasicTableItem();
                    item.setType("bi.my_table_cell");
                    item.setText("summary");
                    item.setStyle(SummaryTableStyleHelper.getHeaderStyles("self.getThemeColor()", styleSetting.getWsTableStyle()));
                    item.setTag(UUID.randomUUID().toString());
                    item.setSum(true);
                    item.setDId(targetId);
                    crossItem.getJSONArray("children").put(item.createJSON());
                }
            }
        }
        return new JSONArray().put(crossItem);
    }

    //获取有效的列表头区域
    public List<JSONArray> getColRegions() throws JSONException {
        List<JSONArray> colRegions = new ArrayList<JSONArray>();
        for (Integer regionId : dimAndTar.keySet()) {
            JSONArray temp = new JSONArray();
            if (BITableExportDataHelper.isDimensionRegion2ByRegionType(regionId)) {
                List<JSONObject> list = dimAndTar.get(regionId);
                for (JSONObject object : list) {
                    temp.put(object.getString("dId"));
                }
                colRegions.add(temp);
            }
        }
        return colRegions;
    }

    protected void refreshDimsInfo() throws Exception {
        if (dimAndTar.containsKey(Integer.valueOf(BIReportConstant.REGION.DIMENSION1))) {
            for (JSONObject s : dimAndTar.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1))) {
                dimIds.add(s.getString("dId"));
            }
        }
        if (dimAndTar.containsKey(Integer.valueOf(BIReportConstant.REGION.DIMENSION2))) {
            for (JSONObject s : dimAndTar.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2))) {
                crossDimIds.add(s.getString("dId"));
            }
        }
        //使用中的指标
        if (dimAndTar.containsKey(Integer.valueOf(BIReportConstant.REGION.TARGET1))) {
            for (JSONObject s : dimAndTar.get(Integer.valueOf(BIReportConstant.REGION.TARGET1))) {
                if (BITableExportDataHelper.isDimUsed(dimAndTar, s.getString("dId"))) {
                    targetIds.add(s.getString("dId"));
                }
            }
        }
    }
}
