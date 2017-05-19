package com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BIBasicTableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.BITableDataConstructor;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.ITableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableExportDataHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.SummaryTableStyleHelper;
import com.fr.bi.conf.report.widget.IWidgetStyle;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kary on 2017/2/27.
 */
public class SummaryComplexTableBuilder extends TableAbstractDataBuilder {
    private String data;

    public SummaryComplexTableBuilder(Map<Integer, List<JSONObject>> dimAndTar, JSONObject dataJSON, IWidgetStyle styleSettings) throws Exception {
        super(dimAndTar, dataJSON, styleSettings);
        this.data = dataJSON.toString();
    }

    @Override
    public void initAttrs() throws Exception {
        super.initAllAttrs();
        refreshDimsInfo();
    }

    @Override
    public void amendment() throws Exception {
        //无行表头
        //补全该二维数组
        boolean flag = isColRegionExist() && !isRowRegionExist();
        if (flag) {
            JSONArray clonedData = new JSONArray();
            if (getColRegions().size() == 1) {
                clonedData = new JSONArray().put(new JSONArray().put(new JSONObject(data)));
            } else {
                JSONArray oneDimeArray = new JSONArray();
                for (int i = 0; i < new JSONObject(data).length(); i++) {
                    JSONObject cData = new JSONObject(data).getJSONObject(String.valueOf(i));
                    oneDimeArray.put(cData);
                }
                clonedData.put(oneDimeArray);
            }
            JSONArray oneDimArray = clonedData.getJSONArray(0);
            for (int i = 0; i < oneDimArray.length(); i++) {
                JSONObject cData = oneDimArray.getJSONObject(i);
                JSONObject tempData = new JSONObject();
                tempData.put("t", new JSONObject().put("c", getTopOfCrossByGroupData(cData.getJSONArray("c")))).put("l", new JSONObject().put("s", cData));
                oneDimArray.put(i, tempData);
            }
            clonedData.put(0, oneDimArray);
            data = clonedData.toString();
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
        boolean flag = isColRegionExist() && !isRowRegionExist();
        if (flag) {
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
        //只有指标
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
            item.setChildren(createCommonTableItems(rowTable.getString("c"), 0, dimIds));
            //汇总
            if (showRowTotal && rowTable.has("s")) {
                List<ITableItem> outerValues = new ArrayList<ITableItem>();
                JSONArray s = rowTable.getJSONArray("s");
                if (dimIds.size() > 0) {
                    for (int j = 0; j < s.length(); j++) {
                        createItem(outerValues, s, j);
                    }
                    item.setValues(outerValues);
                } else {
                    //使用第一个值作为一个维度
                    for (int k = 0; k < s.length(); k++) {
                        createItem(outerValues, s, k);
                    }
                    BIBasicTableItem itemNode = new BIBasicTableItem();
                    itemNode.setDId(targetIds.get(0));
                    itemNode.setValue(s.getString(0));
                    itemNode.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                    itemNode.setValues(outerValues);
                    item.getChildren().add(itemNode);
                    List<ITableItem> values = new ArrayList<ITableItem>();
                    values.add(item);
                    item.setValues(values);
                }
            }
            tempItems.add(item);
        }
        parseColTableItems(tempItems);
    }

    private void createItem(List<ITableItem> outerValues, JSONArray s, int j) throws JSONException {
        BIBasicTableItem itemNode = new BIBasicTableItem();
        itemNode.setDId(targetIds.get(j));
        itemNode.setValue(s.getString(j));
        itemNode.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
        outerValues.add(itemNode);
    }


    private void setOtherComplexAttrs() {
    }

    private void createComplexTableHeader() throws Exception {
        createCrossTableHeader();
        //补齐header的长度
        int rowLength = getLargestLengthOfRowRegions();
        int colLength = getLargestLengthOfColRegions();
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
        while (count < colLength - crossDimIds.size()) {
            count++;
            crossHeaders.add(crossDimIds.size(), lastCrossDimHeader);
        }

    }

    private int getLargestLengthOfColRegions() throws JSONException {
        List<JSONArray> regions = getColRegions();
        int length = 0;
        for (JSONArray region : regions) {
            length = Math.max(length, region.length());
        }
        return length;
    }

    public int getLargestLengthOfRowRegions() throws JSONException {
        List<JSONArray> regions = getColRegions();
        int length = 0;
        for (JSONArray region : regions) {
            length = Math.max(length, region.length());
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
        JSONArray dataArray = new JSONArray();
        List<ITableItem> tempItems = new ArrayList<ITableItem>();
        List<ITableItem> tempCrossItems = new ArrayList<ITableItem>();
        // 如果行表头和列表头都只有一个region构造一个二维的数组
        boolean hasOnlyOnRegion = false;
        if (BIJsonUtils.isKeyValueSet(data)) {
            hasOnlyOnRegion = new JSONObject(data).has("l") && new JSONObject(data).has("t");
        }
        if (hasOnlyOnRegion) {
            dataArray = new JSONArray().put(new JSONArray().put(new JSONObject(data)));
        } else {
            if (BIJsonUtils.isArray(data)) {
                dataArray = new JSONArray(data);
            } else if (BIJsonUtils.isKeyValueSet(data)) {
                for (int i = 0; i < new JSONObject(data).length(); i++) {
                    dataArray.put(new JSONObject(data).getJSONArray(String.valueOf(i)));
                }
            }
        }
        for (int i = 0; i < dataArray.length(); i++) {
            JSONArray tableData = dataArray.getJSONArray(i);
            //* rowValues在这个作用域缓存所有tables的数据
            JSONObject rowValues = new JSONObject();
            List<ITableItem> singleTempItems = new ArrayList<ITableItem>();
            for (int j = 0; j < tableData.length(); j++) {
                singleTempItems = createTempItems(tempCrossItems, i, rowValues, tableData.getJSONObject(j), j);
            }
            tempItems.addAll(singleTempItems);
        }
        parseColTableItems(tempItems);
        parseRowTableCrossItems(tempCrossItems);
    }

    /*
    * 这个结构是从前台直接拷过来的，需优化
    *
    * */
    private List<ITableItem> createTempItems(List<ITableItem> tempCrossItems, int i, JSONObject rowValues, JSONObject tableData, int j) throws Exception {
        //parse一个表结构
        Map<Integer, List<JSONObject>> dimAndTar = getDimsByDataPos(i, j);
        BITableDataConstructor singleTable = createSingleCrossTableItems(tableData, dimAndTar);
        for (int k = 0; k < singleTable.getItems().size(); k++) {
            putNewValuesIntoRowTables(singleTable.getItems().get(k), rowValues, "");
        }
        for (int length = 0; length < singleTable.getItems().size(); length++) {
            singleTable.getItems().set(length, expandTableValues(singleTable.getItems().get(length), rowValues));
        }
        if (i == 0) {
            for (int length = 0; length < singleTable.getCrossItems().size(); length++) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.parseJSON(singleTable.getCrossItems().get(i).createJSON());
                if (tempCrossItems.size() > 0) {
                    List<ITableItem> children = tempCrossItems.get(0).getChildren();
                    children.addAll(item.getChildren());
                    tempCrossItems.get(0).setChildren(children);
                } else {
                    tempCrossItems.add(item);
                }
            }
        }
        return singleTable.getItems();
    }

    /*
    * 扩展item中的value值，数据从rowValues获取
    * */
    private ITableItem expandTableValues(ITableItem tableItem, JSONObject rowValues) throws Exception {
        ITableItem item = new BIBasicTableItem();
        item.parseJSON(tableItem.createJSON());
        if (null == rowValues || rowValues.length() == 0) {
            return item;
        }
        //最外层汇总
        if (tableItem.getDId() == null&&tableItem.getChildren()!=null&&tableItem.getChildren().size()>0&&tableItem.getValues()!=null&&tableItem.getValues().size()>0) {
            item.setValues(convertToItemList(rowValues.has(OUTERSUM) ? rowValues.getJSONArray(OUTERSUM) : new JSONArray()));
        } else {
            item.setValues(convertToItemList(rowValues.optJSONArray(String.valueOf(item.getDId()) + String.valueOf(item.getValue()))));
        }
        if (item.getChildren() != null) {
            for (int i = 0; i < item.getChildren().size(); i++) {
                item.getChildren().set(i, expandTableValues(item.getChildren().get(i), rowValues));
            }
        }
        return item;
    }

    private void parseRowTableCrossItems(List<ITableItem> tempCrossItems) throws Exception {
        List<ITableItem> crossArray = new ArrayList<ITableItem>();
        for (ITableItem tempCrossItem : tempCrossItems) {
            for (int i = 0; i < tempCrossItem.createJSON().getJSONArray("children").length(); i++) {
                crossArray.add(tempCrossItem.getChildren().get(i));
            }
        }
        ITableItem item = new BIBasicTableItem();
        item.setChildren(crossArray);
        crossItems.add(item);
    }

    // 处理列 针对于children
    // 要将所有的最外层的values处理成children
    private void parseColTableItems(List<ITableItem> tempItems) throws Exception {
        for (int i = 0; i < tempItems.size(); i++) {
            boolean isSummary = showRowTotal && targetIds.size() > 0 && (isColRegionExist() || isRowRegionExist()) && !isOnlyCrossAndTarget();
            if (isSummary) {
                BIBasicTableItem summaryValueItem = new BIBasicTableItem();
                summaryValueItem.setValue(SUMMARY);
                summaryValueItem.setValues(tempItems.get(i).getValues());
                summaryValueItem.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                List<ITableItem> childrenAddSummaryValue = tempItems.get(i).getChildren();
                childrenAddSummaryValue.add(summaryValueItem);
                tempItems.get(i).setChildren(childrenAddSummaryValue);
                tempItems.get(i).setValues(null);
            }
        }
        this.items.addAll(tempItems);
    }

    //从table中获取信息，缓存再rowValues里面
    //对于首层，可以以行号作为key，其他层以dId + text作为key
    //一般只处理有values（同时有children的表示合计、否则表示相应的值）
    private void putNewValuesIntoRowTables(ITableItem data, JSONObject rowValues, String pId) throws Exception {
        String itemId = "";
        if (data.getDId() != null && data.getValue() != null) {
            itemId = pId + data.getValue();
        }
        //最外层的合计 可以通过data中是否包含dId来确定
        if (data.getChildren() != null && data.getDId() == null) {
            if (!rowValues.has(OUTERSUM)) {
                rowValues.put(OUTERSUM, new JSONArray());
            }
            rowValues.put(OUTERSUM, BIJsonUtils.arrayConcat(rowValues.optJSONArray(OUTERSUM), data.createJSON().optJSONArray("values")));
        }
        boolean rowRegionAvailable = data.getDId() != null || !isRowRegionExist();
        if (data.getValues() != null && rowRegionAvailable) {
            itemId = data.getDId() + data.getValue();
            boolean hasItems = rowValues.has(itemId) && rowValues.getJSONArray(itemId).length() > 0;
            if (!hasItems) {
                rowValues.put(itemId, data.createJSON().get("values"));
            } else {
                rowValues.put(itemId, BIJsonUtils.arrayConcat(rowValues.getJSONArray(itemId), data.createJSON().getJSONArray("values")));
            }
        }
        if (data.getChildren() != null && data.getChildren().size() > 0) {
            for (int i = 0; i < data.getChildren().size(); i++) {
                putNewValuesIntoRowTables(data.getChildren().get(i), rowValues, itemId);
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

    private BITableDataConstructor createSingleCrossTableItems(JSONObject tableData, Map<Integer, List<JSONObject>> dimsByDataPos) throws Exception {
        SummaryCrossTableDataBuilder builder = new SummaryCrossTableDataBuilder(dimsByDataPos, tableData, this.styleSetting);
        builder.initAttrs();
        builder.createHeaders();
        builder.createItems();
        BITableDataConstructor data = builder.createTableData();
        return data;
    }

    //获取有效的列表头区域
    private boolean isRowRegionExist() throws JSONException {
        for (JSONArray array : getRowRegions()) {
            if (array.length() > 0) {
                return true;
            }
        }
        return false;
    }

    //行表头是否存在
    private boolean isColRegionExist() throws JSONException {
        for (JSONArray array : getColRegions()) {
            if (array.length() > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnlyCrossAndTarget() throws JSONException {
        return !this.isRowRegionExist() &&
                this.isColRegionExist() &&
                this.targetIds.size() > 0;
    }

    @Override
    public BITableDataConstructor createTableData() throws JSONException {
        BITableDataConstructor tableDataForExport = new BITableDataConstructor(headers, items, crossHeaders, crossItems, this.styleSetting);
        return tableDataForExport;
    }

    //获取有效的行表头区域
    public List<JSONArray> getRowRegions() throws JSONException {
        List<JSONArray> rowRegions = new ArrayList<JSONArray>();
        for (Integer regionId : dimAndTar.keySet()) {
            JSONArray temp = new JSONArray();
            if (BITableExportDataHelper.isDimensionRegion1ByRegionType(regionId)) {
                List<JSONObject> list = dimAndTar.get(regionId);
                for (JSONObject dIdJson : list) {
                    if (dIdJson.optBoolean("used")) {
                        temp.put(dIdJson.getString("dId"));
                    }
                    if (temp.length() > 0) {
                        rowRegions.add(temp);
                    }

                }
            }
        }
        return rowRegions;
    }

    protected List<ITableItem> createCrossItems(JSONObject top) throws Exception {
        ITableItem crossItem = new BIBasicTableItem();
        List<ITableItem> children = createCrossPartItems(top.getJSONArray("c"), 0);
        crossItem.setChildren(children);
        if (showColTotal) {
            if (isRowRegionExist()) {
                for (int i = 0; i < targetIds.size(); i++) {
                    BIBasicTableItem item = new BIBasicTableItem();
                    item.setDId(targetIds.get(i));
                    item.setValue("summary");
                    item.setStyles(SummaryTableStyleHelper.getHeaderStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                    children.add(item);
                }

            } else {
                for (String targetId : targetIds) {
                    BIBasicTableItem item = new BIBasicTableItem();
                    item.setValue("summary");
                    item.setStyles(SummaryTableStyleHelper.getHeaderStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                    item.setDId(targetId);
                    crossItem.getChildren().add(item);
                }
            }
        }
        List<ITableItem> crossItems = new ArrayList<ITableItem>();
        crossItems.add(crossItem);
        return crossItems;
    }

    //获取有效的列表头区域
    public List<JSONArray> getColRegions() throws JSONException {
        List<JSONArray> colRegions = new ArrayList<JSONArray>();
        for (Integer regionId : dimAndTar.keySet()) {
            if (BITableExportDataHelper.isDimensionRegion2ByRegionType(regionId)) {
                JSONArray array = new JSONArray();
                for (JSONObject dIdJson : dimAndTar.get(regionId)) {
                    if (dIdJson.has("used") && dIdJson.getBoolean("used")) {
                        array.put(dIdJson.getString("dId"));
                    }
                }
                if (array.length() > 0) {
                    colRegions.add(array);
                }
            }
        }
        return colRegions;
    }

    protected void refreshDimsInfo() throws Exception {
        loop:
        for (Integer integer : dimAndTar.keySet()) {
            if (BITableExportDataHelper.isDimensionRegion1ByRegionType(integer)) {
                for (JSONObject s : dimAndTar.get(integer)) {
                    dimIds.add(s.getString("dId"));
                }
                if (dimIds.size() > 0) {
                    break loop;
                }
            }
        }
        loop:
        if (dimAndTar.containsKey(Integer.valueOf(BIReportConstant.REGION.DIMENSION2))) {
            for (JSONObject s : dimAndTar.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2))) {
                crossDimIds.add(s.getString("dId"));
            }
            if (crossDimIds.size() > 0) {
                break loop;
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
