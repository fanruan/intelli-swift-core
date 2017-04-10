package com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.BIBasicTableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableExportDataHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.SummaryTableStyleHelper;
import com.fr.bi.cal.analyze.report.report.widget.styles.BIStyleSetting;
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
    private JSONArray converData = new JSONArray();
    private String outer_sum = "__outer_sum_";
    boolean showRowTotal = true;

    public SummaryComplexTableBuilder(Map<Integer, List<JSONObject>> dimAndTar, JSONObject dataJSON,BIStyleSetting styleSettings) throws Exception {
        super(dimAndTar, dataJSON, styleSettings);
    }

    @Override
    public void initAttrs() throws JSONException {
        super.initAllAttrs();
        refreshDimsInfo();
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
        //仅有列表头的时候（无指标）
        boolean flag = isColRegionExist() && !isRowRegionExist();
        if (flag) {
            createCrossHeader4OnlyCross();
            createCrossItems4OnlyCross();
            setOtherAttrs4OnlyCross();
            return;
        }
        //仅有列表头的时候(有指标)
        if (isOnlyCrossAndTarget()) {
            JSONObject clondData = new JSONObject(dataJSON);
            dataJSON.put("t", new JSONObject().put("c", getTopOfCrossByGroupData(clondData.getJSONArray("c"))));
            dataJSON.put("l", new JSONObject().put("s", clondData));
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
        if (dataJSON.has("c")) {
            createTableItems();
            return;
        }
        List<ITableItem> tempItems=new ArrayList<ITableItem>();
        for (int i = 0; i < dataJSON.length(); i++) {
            JSONObject rowTable = dataJSON.getJSONObject(String.valueOf(i));
            Map<Integer, List<JSONObject>> dimOb = getDimsByDataPos(i, 0);
            List<JSONObject> list = dimOb.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1));
            List<String> dimIds = new ArrayList<String>();
            for (JSONObject jsonObject : list) {
                dimIds.add(jsonObject.getString("dId"));
            }
            BIBasicTableItem item=new BIBasicTableItem();
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
                    itemNode.setType("bi.target_body_normal_cell");
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
        itemNode.setType("bi.target_body_normal_cell");
        itemNode.setDId(targetIds.get(j));
        itemNode.setText(s.getString(j));
        itemNode.setClicked(new JSONArray().put(new JSONObject()));
        itemNode.setStyle(SummaryTableStyleHelper.getLastSummaryStyles("", ""));
        outerValues.put(itemNode);
    }


    private void setOtherComplexAttrs() {
        setOtherCrossAttrs();
    }

    private void setOtherCrossAttrs() {
    }

    private void createComplexTableHeader() throws Exception {
        createCrossTableHeader();
        //补齐header的长度
        int count = 0;
        int rowLength = getLargestLengthOfRowRegions();
        int clolLength = getLargestLengthOfColRegions();
        JSONObject lastDimHeader = this.headers.get(dimIds.size() - 1).createJSON();
        ITableHeader lastCrossDimHeader = crossHeaders.get(crossDimIds.size() - 1);
// FIXME: 2017/3/6
        while (count < rowLength - dimIds.size()) {
            ITableHeader header = lastCrossDimHeader;
            this.headers.set(dimIds.size(), header);
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
//        JSONArray tempItems = new JSONArray();
        List<ITableItem> tempItems=new ArrayList<ITableItem>();
        JSONArray tempCrossItems = new JSONArray();
        // 如果行表头和列表头都只有一个region构造一个二维的数组

        if (dataJSON.has("l") && dataJSON.has("t")) {
            converData = new JSONArray().put(new JSONArray().put(dataJSON));
        }
        for (int i = 0; i < converData.length(); i++) {
            JSONObject rowValues = new JSONObject();
            JSONArray rowTables = converData.getJSONArray(i);
            for (int j = 0; j < rowTables.length(); j++) {
                //parse一个表结构
                JSONObject tableData = rowTables.getJSONObject(i);
                Map<Integer, List<JSONObject>> dimAndTar = getDimsByDataPos(i, j);
                JSONObject singleTable = createSingleCrossTableItems(tableData, dimAndTar);
                for (int k = 0; k < singleTable.getJSONArray("items").length(); k++) {
                    parseRowTableItems(singleTable.getJSONArray("items").getJSONObject(k), rowValues);
                }
            }
        }
        parseColTableItems(tempItems);
        parseRowTableCrossItems(tempCrossItems);
    }

    private void parseRowTableCrossItems(JSONArray tempCrossItems) throws JSONException {
        JSONArray children = new JSONArray();
        for (int i = 0; i < tempCrossItems.length(); i++) {
            JSONArray tCrossItem = tempCrossItems.getJSONArray(i);
            children.put(tCrossItem);
        }
        crossItems.put(new JSONArray().put(new JSONObject().put("children", children)));
    }

    // 处理列 针对于children
    // 要将所有的最外层的values处理成children
    private void parseColTableItems(List<ITableItem> tempItems) throws JSONException {
//        JSONArray children = new JSONArray();
        List<ITableItem> children=new ArrayList<ITableItem>();
//        for (int i = 0; i < tempItems.length(); i++) {
            for (int i = 0; i < tempItems.size(); i++) {
//            JSONObject tItem = tempItems.getJSONObject(i);
//            children.put(tItem.getJSONArray("children"));
            children.add(tempItems.get(i));
            boolean isSummary = showRowTotal && isColRegionExist() && isRowRegionExist() && isOnlyCrossAndTarget();
            if (isSummary) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.setType("bi.page_table_cell");
                item.setText("BI.i18nText(\"BI-Summary_Values\")");
                item.setTag(UUID.randomUUID().toString());
//                item.setValue(tempItems);
                item.setValue(new JSONArray(tempItems));
                item.setStyle(SummaryTableStyleHelper.getLastSummaryStyles("", ""));
//                children.put(item);
                children.add(item);
            }
            items=new ArrayList<ITableItem>();
            BIBasicTableItem tempItem=new BIBasicTableItem();
            tempItem.setChildren(children);
            items.add(tempItem);
//            items = new JSONArray().put(new JSONObject().put("children", children));
        }
    }

    //对于首层，可以以行号作为key，其他层以dId + text作为key
    //一般只处理有values（同时有children的表示合计、否则表示相应的值）
    private void parseRowTableItems(JSONObject data, JSONObject rowValues) throws JSONException {
        //最外层的合计 可以通过data中是否包含dId来确定
        if (data.has("values")) {
            JSONArray values = data.getJSONArray("values");
            if (data.has("children") && data.has("dId")) {
                if (rowValues.has(outer_sum)) {
                    rowValues.put(outer_sum, new JSONArray().put(data.getString("values")));
                } else {
                    if (BIJsonUtils.isArray(data.getString("values"))) {
                        for (int i = 0; i < values.length(); i++) {
                            if (rowValues.has(outer_sum)) {
                                rowValues.getJSONArray(outer_sum).put(values.getString(i));
                            } else {
                                rowValues.put(outer_sum, new JSONArray());
                            }
                        }
                    }
                }
            }
            if (data.has("dId")) {
                String itemId = data.getString("dId") + data.getString("text");
                if (rowValues.has(itemId)) {
                    rowValues.put(itemId, values);
                } else {
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < values.length(); i++) {
                        array.put(values.getString(i));
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
        List<JSONArray> rowRegions = getRowRegions();
        List<JSONArray> colRegions = getColRegions();
        JSONArray dimIds = rowRegions.get(row);
        JSONArray crossDimIds = colRegions.get(col);

        List<JSONObject> dimIdObj = new ArrayList<JSONObject>();
        for (int i = 0; i < dimIds.length(); i++) {
            dimIdObj.add(new JSONObject().put("dId", dimIds.getString(i)));
        }

        List<JSONObject> crossDimIdObj = new ArrayList<JSONObject>();
        for (int i = 0; i < dimIds.length(); i++) {
            crossDimIdObj.add(new JSONObject().put("dId", crossDimIds.getString(i)));
        }

        Map<Integer, List<JSONObject>> dimAndTars = new HashMap<Integer, List<JSONObject>>();
        dimAndTars.put(Integer.valueOf(BIReportConstant.REGION.DIMENSION1), dimIdObj);
        dimAndTars.put(Integer.valueOf(BIReportConstant.REGION.DIMENSION2), crossDimIdObj);
        return dimAndTars;
    }

    private JSONObject createSingleCrossTableItems(JSONObject tableData, Map<Integer, List<JSONObject>> dimsByDataPos) throws Exception {
        SummaryCrossTableDataBuilder builder = new SummaryCrossTableDataBuilder(null, tableData,styleSetting);
        builder.initAttrs();
        builder.createItems();
        BIExcelTableData data = builder.createTableData();
        JSONObject result = new JSONObject();
        result.put("crossItems", new JSONArray().put(data.getCrossItems()));
        result.put("items", new JSONArray().put(data.getItems()));
        return result;
    }

    //获取有效的列表头区域
    private boolean isRowRegionExist() {
        return dimAndTar.containsKey(BIReportConstant.REGION.DIMENSION1) && dimAndTar.get(BIReportConstant.REGION.DIMENSION1).size() > 0;
    }

    //行表头是否存在
    private boolean isColRegionExist() {
        return dimAndTar.containsKey(BIReportConstant.REGION.DIMENSION1) && dimAndTar.get(BIReportConstant.REGION.DIMENSION2).size() > 0;
    }

    private boolean isOnlyCrossAndTarget() {
        return !this.isRowRegionExist() &&
                this.isColRegionExist() &&
                this.targetIds.size() > 0;
    }

    @Override
    public BIExcelTableData createTableData() throws JSONException {
        return null;
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
            }
            rowRegions.add(temp);
        }
        return rowRegions;
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
            }
            colRegions.add(temp);
        }
        return colRegions;

    }


}
