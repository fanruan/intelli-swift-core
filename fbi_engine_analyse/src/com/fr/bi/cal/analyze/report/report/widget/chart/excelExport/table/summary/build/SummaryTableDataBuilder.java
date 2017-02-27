package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.build;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.node.ReportNode;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.node.ReportNodeTree;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.basic.BIExcelDataBuilder;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableData;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.summary.basic.BIExcelTableItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.table.utils.ExportDataHelper;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by Kary on 2017/2/16.
 */
public class SummaryTableDataBuilder implements BIExcelDataBuilder {
    JSONArray crossHeaders;
    JSONArray crossItems;
    List<String> crossDimIds;
    List<JSONArray> crossItemSums;
    //fixme 尽量避免使用json来代替对象
    protected JSONArray items;
    protected JSONArray headers;

    protected TableWidget widget;
    protected JSONObject dataJSON;
    protected ReportNodeTree tree;
    protected List<String> dimIds;
    protected List<String> targetIds;
    private boolean showColTotal = true;

    public SummaryTableDataBuilder(TableWidget widget, JSONObject dataJSON) throws Exception {
        this.widget = widget;
        this.dataJSON = dataJSON;


    }

    @Override
    public void initAttrs() throws JSONException {
        initAllAttrs();
        refreshDimsInfo();
        //仅有列表头的时候(有指标) 修正数据
        if (this.dimIds.size() == 0 && this.crossDimIds.size() > 0 && this.targetIds.size() > 0) {
            amendment();
        }
    }

    @Override
    public void createHeadersAndItems() throws Exception {
        //正常交叉表
        if (null != dataJSON && dataJSON.has("t")) {
            getNormalCrossTable();
            return;
        }
        //仅有列表头的时候（无指标）
        if (this.dimIds.size() == 0 && this.crossDimIds.size() > 0 && this.targetIds.size() > 0) {
            getNoneTarCrossTable();
            return;
        }
        //无列表头(普通汇总表)
        tableWithoutDims();
    }

    @Override
    public BIExcelTableData createTableData() throws JSONException {
        BIExcelTableData tableDataForExport = new BIExcelTableData(headers, items, crossHeaders, crossItems);
        return tableDataForExport;

    }

    protected void initAllAttrs() {
        tree = new ReportNodeTree();
        dimIds = new ArrayList<>();
        targetIds = new ArrayList<>();
        crossItems = new JSONArray();
        crossDimIds = new ArrayList<>();
        crossItemSums = new ArrayList<>();
        items = new JSONArray();
        headers = new JSONArray();
        crossHeaders = new JSONArray();
    }

    public void amendment() throws JSONException {
        JSONObject cloneData = new JSONObject(this.dataJSON);
        this.dataJSON.put("t", new JSONObject().put("c", getTopOfCrossByGroupData(this.dataJSON.getJSONArray("c"))));
        this.dataJSON.put("s", cloneData);
    }

    private void refreshDimsInfo() {
        List<String> allBITargetAndDimensionIds = getAllBITargetAndDimensionIds();
        Map<Integer, List<String>> views = widget.getView();
        if (views.containsKey(Integer.valueOf(BIReportConstant.REGION.DIMENSION1))) {
            for (String s : views.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION1))) {
                if (allBITargetAndDimensionIds.contains(s)) {
                    dimIds.add(s);
                }
            }
        }
        if (views.containsKey(Integer.valueOf(BIReportConstant.REGION.DIMENSION2))) {
            for (String s : views.get(Integer.valueOf(BIReportConstant.REGION.DIMENSION2))) {
                if (allBITargetAndDimensionIds.contains(s)) {
                    crossDimIds.add(s);
                }
            }
        }
        if (views.containsKey(Integer.valueOf(BIReportConstant.REGION.TARGET1))) {
            for (String s : views.get(Integer.valueOf(BIReportConstant.REGION.TARGET1))) {
                if (allBITargetAndDimensionIds.contains(s)) {
                    targetIds.add(s);
                }
            }
        }
    }

    protected void tableWithoutDims() throws Exception {
        createTableHeader();
        createTableItems();
    }

    protected void createTableItems() throws Exception {
        int currentLayer = 0;
        JSONObject item = new JSONObject();
        List children = new ArrayList();
        if (dataJSON.has("c")) {
            children = createCommonTableItems(dataJSON.getString("c"), currentLayer, new ReportNode(), dimIds, new JSONArray());
        }
        item.put("children", children);
        //汇总
        if (!showColTotal || !dataJSON.has("s")) {
            return;
        }
        JSONArray outerValues = new JSONArray();
        JSONArray s = dataJSON.getJSONArray("s");
        if (dimIds.size() > 0) {
            for (int i = 0; i < s.length(); i++) {
                BIExcelTableItem temp = new BIExcelTableItem();
                temp.setType("bi.target_body_normal_cell");
                temp.setText(s.getString(i));
                temp.setdId(targetIds.get(i));
                temp.setStyle("");
//[{}]
                temp.setClicked(new JSONArray().put(new JSONObject()));
                outerValues.put(temp.createJSON());
            }
            item.put("values", outerValues);
        } else {
            //使用第一个值作为一个维度
            for (int i = 0; i < s.length(); i++) {
                BIExcelTableItem temp = new BIExcelTableItem();
                temp.setType("bi.target_body_normal_cell");
                temp.setText(s.getString(i));
                temp.setdId(targetIds.get(i));
//[{}]
                temp.setClicked(new JSONArray().put(new JSONObject()));
            }
            BIExcelTableItem temp = new BIExcelTableItem();
            temp.setType("bi.target_body_normal_cell");
            temp.setText(dataJSON.getJSONArray("s").getString(0));
            temp.setdId(targetIds.get(0));
            temp.setClicked(new JSONArray().put(new JSONObject()));
            temp.setTag(UUID.randomUUID().toString());
            temp.setSum(true);
            temp.setValue(outerValues);
            item.getJSONArray("children").put(temp);
            item.put("valus", item);
        }
        items = new JSONArray().put(item);
    }

    private void createTableHeader() throws Exception {
        List<String> allIds = new ArrayList<>(dimIds);
        allIds.addAll(targetIds);
        for (String dimId : allIds) {
            BIExcelTableHeader header = new BIExcelTableHeader();
            header.setdID(dimId);
            header.setText(ExportDataHelper.getDimensionNameByID(widget, dimId));
            headers.put(header.createJSON());
        }
    }

    private void getNoneTarCrossTable() throws Exception {
        createCrossHeader4OnlyCross();
        createCrossItems4OnlyCross();
        setOtherAttrs4OnlyCross();
    }

    private void setOtherAttrs4OnlyCross() throws Exception {
        parseSizeOfCrossItems(this.crossItems);
    }

    private void parseSizeOfCrossItems(JSONArray items) throws Exception {
        for (int i = 0; i < items.length(); i++) {
            if (items.getJSONObject(i).has("children")) {
                parseHeader(items.getJSONObject(i).getJSONArray("children"));
            }
        }
    }

    private void createCrossItems4OnlyCross() throws Exception {
        JSONObject crossItem = new JSONObject();
        //交叉表items
        List<BIExcelTableItem> c = createCrossPartItems(dataJSON.getJSONArray("c"), 0, null);
        crossItem.put("children", c);
        crossItems = new JSONArray().put(crossItem);
    }

    //仅有列表头的交叉表
    private void createCrossHeader4OnlyCross() throws Exception {
        for (int i = 0; i < crossDimIds.size(); i++) {
            BIExcelTableHeader header = new BIExcelTableHeader();
            header.setdID(crossDimIds.get(i));
            header.setText(ExportDataHelper.getDimensionNameByID(widget, crossDimIds.get(i)));
            header.setStyle(null);
            crossHeaders.put(header.createJSON());

        }
    }


    private List<String> getAllBITargetAndDimensionIds() {
        List<String> allBITargetAndDimensionIds = new ArrayList<>();
        for (BISummaryTarget target : widget.getTargets()) {
            allBITargetAndDimensionIds.add(target.getId());
        }
        for (BIDimension dimension : widget.getDimensions()) {
            allBITargetAndDimensionIds.add(dimension.getId());
        }
        return allBITargetAndDimensionIds;
    }


    //从分组表样式的数据获取交叉表数据样式
    // TODO: 2017/2/21
    private JSONArray getTopOfCrossByGroupData(JSONArray c) {
        JSONArray newC = new JSONArray();
        return newC;
    }

    private void getNormalCrossTable() throws Exception {
        createCrossTableItems();
        createCrossTableHeader();
    }

    /**
     * 交叉表 items and crossItems
     */
    private void createCrossTableItems() throws Exception {
        JSONObject top = dataJSON.getJSONObject("t");
        JSONObject left = dataJSON.getJSONObject("l");
        //根据所在的层，汇总情况——是否含有汇总
        crossItemSums.add(0, new JSONArray());
        if (left.has("s")) {
            crossItemSums.set(0, new JSONArray().put(true));
        }
        initCrossItemsSum(0, left.getJSONArray("c"), crossItemSums);
        //交叉表items
        createCrossItems(top);
        //用cross parent value来对应到联动的时候的列表头值
        JSONArray crossPV = new JSONArray();
        parseCrossItem2Array(crossItems, crossPV, new JSONArray());
        //无行表头 有列表头、指标
        if (isOnlyCrossAndTarget()) {
            items = createItems4OnlyCrossAndTarget(this.dataJSON, crossPV);
            return;
        }
        createCrossItems(left, crossPV);
    }

    private void createCrossItems(JSONObject left, JSONArray crossPV) throws Exception {
        JSONObject item = new JSONObject();
        item.put("children", createCommonTableItems(left.getString("c"), 0, null, dimIds, crossPV));
        if (showColTotal) {
            //汇总值
            JSONArray sums = new JSONArray();
            JSONObject ob = new JSONObject().put("index", 0);
            boolean hasSC = BIJsonUtils.isKeyValueSet(left.getString("s")) && left.getJSONObject("s").has("c");
            boolean hasSS = BIJsonUtils.isKeyValueSet(left.getString("s")) && left.getJSONObject("s").has("s");
            if (hasSC && hasSS) {
                createTableSumItems(left.getJSONObject("s").getString("c"), sums, new JSONArray(), ob, true, 0, crossPV);
            } else {
                if (BIJsonUtils.isArray(left.getString("s"))) {
                    createTableSumItems(left.getString("s"), sums, new JSONArray(), ob, true, 0, crossPV);
                }

            }
            JSONArray outerValues = new JSONArray();
            JSONArray ss = left.getJSONObject("s").getJSONArray("s");
            for (int i = 0; i < ss.length(); i++) {
                if (targetIds.size() > 0) {
                    String tId = targetIds.get(i);
                    BIExcelTableItem tempItem = new BIExcelTableItem();
                    tempItem.setType("bi.target_body_normal_cell");
                    tempItem.setText(ss.getString(i));
                    tempItem.setdId(tId);
                    tempItem.setStyle("BI.SummaryTableHelper.getLastSummaryStyles(self.themeColor, self.tableStyle)");
                    tempItem.setClicked(new JSONArray());
                    outerValues.put(tempItem.createJSON());
                }
            }
            for (int i = 0; i < sums.length(); i++) {
                JSONObject sum = sums.getJSONObject(i);
                sum.put("cls", "summary-cell last");
                sums.put(i, sum);
            }
            sums.put(outerValues);
            item.put("values", sums);
        }
        this.items = new JSONArray().put(item);
    }

    private void createCrossItems(JSONObject top) throws Exception {
        JSONObject crossItem = new JSONObject();
        List children = createCrossPartItems(top.getJSONArray("c"), 0, new ReportNode());
        crossItem.put("children", children);
        if (showColTotal) {
            if (isOnlyCrossAndTarget()) {
                BIExcelTableItem item = new BIExcelTableItem();
                item.setType("bi.page_table_cell");
                item.setText("BI.i18nText(\"BI-Summary_Values\")");
                item.setStyle("");
                crossItem.put("children", crossItem.getJSONArray("children").put(item.createJSON()));
            } else {
                for (String targetId : targetIds) {
                    BIExcelTableItem item = new BIExcelTableItem();
                    item.setType("bi.normal_header_cell");
                    item.setText("BI.i18nText(\"BI-Summary_Values\")");
                    item.setStyle("");
                    item.setTag(UUID.randomUUID().toString());
                    item.setSum(true);
                    crossItem.put("children", crossItem.getJSONArray("children").put(item.createJSON()));
                }
            }
        }
        crossItems = new JSONArray().put(crossItem);
    }


    //仅有列表头和指标 l: {s: {c: [{s: [1, 2]}, {s: [3, 4]}], s: [100, 200]}}
    private JSONArray createItems4OnlyCrossAndTarget(JSONObject dataJSON, JSONArray crossPV) throws Exception {
        JSONObject l = dataJSON.getJSONObject("l");
        for (int i = 0; i < targetIds.size(); i++) {
            JSONObject ob = new JSONObject();
            ob.put("type", "bi.page_table_cell");

            ob.put("text", ExportDataHelper.getDimensionNameByID(widget, targetIds.get(i)));
            ob.put("title", "BI.SummaryTableHelper.getBodyStyles(self.themeColor, self.tableStyle, i)");
            items.put(new JSONObject().put("children", ob));
        }

        createItems(items, l.getJSONObject("s"), new JSONObject().put("cIndex", 0), crossPV);
        return items;
    }

    private void createItems(JSONArray items, JSONObject data, JSONObject indexOB, JSONArray crossPV) throws JSONException {
        if (BIJsonUtils.isArray(data.getString("c")) && data.getJSONArray("c").length() > 0) {
            JSONArray c = data.getJSONArray("c");
            for (int i = 0; i < c.length(); i++) {
                JSONObject child = c.getJSONObject(i);
                if (child.has("s") && child.has("c")) {
                    createItems(items, child, indexOB, crossPV);
                } else if (child.has("s")) {
                    for (int j = 0; j < child.getJSONArray("s").length(); j++) {

                        if (items.getJSONObject(j).getJSONArray("children").getJSONObject(0).has("values")) {
                            items.getJSONObject(j).getJSONArray("children").put(0, new JSONArray());
                        }
                        JSONObject ob = new JSONObject();
                        ob.put("type", "bi.target_body_normal_cell");
                        ob.put("text", child.getJSONArray("s").getString(j));
                        ob.put("styles", "BI.SummaryTableHelper.getBodyStyles(self.themeColor, self.tableStyle, j)");
                        ob.put("dId", targetIds.get(j));
                        ob.put("clicked", crossPV.getString(indexOB.getInt("cIndex")));
                        items.getJSONObject(j).getJSONArray("children").put(ob);
                    }
                    indexOB.put("cIndex", indexOB.getInt("cIndex") + 1);
                }
            }
        }
        if (showColTotal) {
            if (BIJsonUtils.isArray(data.getString("s")) && data.getJSONArray("s").length() > 0) {
                JSONArray s = data.getJSONArray("s");
                for (int j = 0; j < s.length(); j++) {
                    if (items.getJSONObject(j).getJSONArray("children").getJSONObject(0).has("values")) {
                        items.getJSONObject(j).getJSONArray("children").getJSONObject(0).put("values", new JSONArray());
                    }
                    JSONObject ob = new JSONObject();
                    ob.put("type", "bi.target_body_normal_cell");
                    ob.put("text", s.getString(j));
                    ob.put("styles", "BI.SummaryTableHelper.getBodyStyles(self.themeColor, self.tableStyle, j)");
                    ob.put("dId", targetIds.get(j));
                    ob.put("clicked", crossPV.getString(indexOB.getInt("cIndex")));
                    items.getJSONObject(j).getJSONArray("children").getJSONObject(0).getJSONArray("values").put(ob);
                }
            }
            indexOB.put("cIndex", indexOB.getInt("cIndex") + 1);
        }
    }

    private void parseCrossItem2Array(JSONArray crossItems, JSONArray pValues, JSONArray pv) throws Exception {
        for (int i = 0; i < crossItems.length(); i++) {
            JSONObject crossItem = crossItems.getJSONObject(i);
            if (crossItem.has("children")) {
                JSONArray tempPV = new JSONArray();
                if (crossItem.has("dId")) {
                    if (BIJsonUtils.isArray(crossItem.getString("values")) && crossItem.getJSONArray("values").length() > 0) {
                        for (int j = 0; j < crossItem.getJSONArray("values").length(); j++) {
                            JSONObject object = new JSONObject().put("dId", crossItem.getString("dId")).put("value", getClickedValue4Group(crossItem.getString("text"), crossItem.getString("dId")));
                            tempPV = pv.put(new JSONArray().put(object));
                        }
                    }
                    //显示列汇总的时候需要构造汇总
                    else {
                        JSONObject object = new JSONObject().put("dId", crossItem.getString("dId")).put("value", getClickedValue4Group(crossItem.getString("text"), crossItem.getString("dId")));
                        tempPV = pv.put(new JSONArray().put(object));
                    }
                    parseCrossItem2Array(crossItem.getJSONArray("children"), pValues, tempPV);
                    //汇总
                    if (BIJsonUtils.isArray(crossItem.getString("values")) && crossItem.getJSONArray("values").length() > 0) {
                        for (int j = 0; j < crossItem.getJSONArray("values").length(); j++) {
                            JSONObject object = new JSONObject().put("dId", crossItem.getString("dId")).put("value", getClickedValue4Group(crossItem.getString("text"), crossItem.getString("dId")));
                            pValues.put(object);
                        }
                    } else if (crossItem.has("dId")) {
                        if (BIJsonUtils.isArray(crossItem.getString("values")) && crossItem.getJSONArray("values").length() > 0) {
                            for (int j = 0; j < crossItem.getJSONArray("values").length(); j++) {
                                JSONObject object = new JSONObject().put("dId", crossItem.getString("dId")).put("value", getClickedValue4Group(crossItem.getString("text"), crossItem.getString("dId")));
                                pValues.put(object);
                            }

                        } else {
                            pValues.put(new JSONArray());
                        }
                    } else if (crossItem.has("isSum")) {
                        pValues.put(pv);
                    }
                }
            }
        }
    }

    /**
     * 交叉表——header and crossHeader
     */
    private void createCrossTableHeader() throws Exception {
        for (String dimId : dimIds) {
            BIExcelTableHeader header = new BIExcelTableHeader();
            header.setdID(dimId);
            header.setText(ExportDataHelper.getDimensionNameByID(widget, dimId));
            headers.put(header.createJSON());
        }
        for (String dimId : crossDimIds) {
            BIExcelTableHeader header = new BIExcelTableHeader();
            header.setdID(dimId);
            header.setText(ExportDataHelper.getDimensionNameByID(widget, dimId));
            crossHeaders.put(header.createJSON());
        }

        //根据crossItems创建部分header
        if (!isOnlyCrossAndTarget()) {
            createCrossPartHeader();
        }
    }

    /**
     * 交叉表——crossHeader
     */
    private void createCrossPartHeader() throws Exception {
        //可以直接根据crossItems确定header的后半部分
        parseHeader(crossItems);
    }

    private void parseHeader(JSONArray items) throws Exception {
        for (int i = 0; i < items.length(); i++) {
            String dName = ExportDataHelper.getDimensionNameByID(widget, targetIds.get(i % (targetIds.size())));
            JSONObject item = items.getJSONObject(i);
            if (item.has("children")) {
                parseHeader(item.getJSONArray("children"));
                if (item.has("values") && showColTotal) {
                    //合计
                    for (String targetId : targetIds) {
                        BIExcelTableHeader header = new BIExcelTableHeader();
                        header.setText("BI.i18nText(\"BI-Summary_Values\") + \":\"" + ExportDataHelper.getDimensionNameByID(widget, targetId));
                        header.setTitle("BI.i18nText(\"BI-Summary_Values\") + \":\"" + ExportDataHelper.getDimensionNameByID(widget, targetId));
                        header.setTag(UUID.randomUUID().toString());
                        headers.put(header.createJSON());
                    }
                }
            } else if (item.has("isSum")) {
                //合计
                item.put("text", "BI.i18nText(\"BI-Summary_Values\") + \":\"" + ExportDataHelper.getDimensionNameByID(widget, item.getString("dID")));
                item.put("cls", "cross-table-target-header");
                headers.put(item);
            } else if (!item.isNull("values")) {
                //单指标情况下，指标不显示，合并到上面
                if (targetIds.size() == 1) {
                    headers.put(item);
                } else {
                    JSONArray values = item.getJSONArray("values");
                    for (int j = 0; j < values.length(); j++) {
                        JSONObject header = new JSONObject();
                        header.put("text", ExportDataHelper.getDimensionNameByID(widget, targetIds.get(j)));
                        header.put("title", ExportDataHelper.getDimensionNameByID(widget, targetIds.get(j)));
                        header.put("tag", UUID.randomUUID().toString());
                        headers.put(header);
                    }
                }
            } else {
                JSONObject header = new JSONObject();
                header.put("text", dName);
                header.put("title", dName);
                header.put("tag", UUID.randomUUID().toString());
                headers.put(header);
            }
        }
    }


    /**
     * 初始化 crossItemsSum
     */
    private void initCrossItemsSum(int currentLayer, JSONArray sums, List<JSONArray> crossItemsSums) throws JSONException {
        currentLayer++;
        for (int i = 0; i < sums.length(); i++) {
            JSONObject v = sums.getJSONObject(i);
            if (null != v && v.has("c")) {
                initCrossItemsSum(currentLayer, v.getJSONArray("c"), crossItemsSums);
            }
            if (crossItemsSums.size() <= currentLayer) {
                crossItemsSums.add(new JSONArray());
            }
            crossItemsSums.set(currentLayer, crossItemsSums.get(currentLayer).put(v.has("s")));
        }
    }

    /**
     * 交叉表——crossItems
     */
    private List<BIExcelTableItem> createCrossPartItems(JSONArray c, int currentLayer, ReportNode parent) throws Exception {
        List items = new ArrayList();
        List crossHeaderIItems = new ArrayList();
        currentLayer++;
        for (int i = 0; i < c.length(); i++) {
            JSONObject child = c.getJSONObject(i);
            boolean flag;
            boolean hasC = child.has("c");
            boolean existInTargets = targetIds.contains(child.getString("n"));
            boolean existInDims = crossDimIds.contains(child.getString("n"));
            flag = !hasC && (existInTargets || existInDims);
            if (flag) {
                return items;
            }
            String cId = child.has("n") ? UUID.randomUUID().toString() : child.getString("n");
            String currDid = widget.getDimensions()[currentLayer - 1].getText();
            String currValue = child.getString("n");
            String nodeId = null != parent ? parent.getId() + cId : cId;
            ReportNode node = new ReportNode(nodeId);
            node.setName(child.getString("n"));
            node.setdId(currDid);
            this.tree.addNode(parent, node);
            List pValues = new ArrayList();
            int tempLayer = currentLayer;
            String tempNodeId = nodeId;
            while (tempLayer > 0) {
                String dId = crossDimIds.get(tempLayer - 1);
                ReportNode itemNode = tree.getNode(tempNodeId);
                JSONObject json = new JSONObject();
                json.put("value", itemNode.getName() + dId);
                json.put("dId", dId);
                pValues.add(json);
                tempNodeId = itemNode.getParent().getId();
                tempLayer--;
            }
            BIExcelTableItem item = setPartItem(currentLayer, i, child, currDid, currValue, node);
            crossHeaderIItems.add(item.createJSON());
        }
        return crossHeaderIItems;
    }

    private BIExcelTableItem setPartItem(int currentLayer, int i, JSONObject child, String currDid, String currValue, ReportNode node) throws Exception {
        BIExcelTableItem item = new BIExcelTableItem();
        item.setType("bi.normal_expander_cell");
        item.setText(currValue);
        item.setdId(currDid);
        item.setCross(true);
        if (currentLayer < crossDimIds.size()) {
            item.setNeedExpand(true);
            item.setExpanded(false);
        }
        if (child.has("c")) {
            List children = createCrossPartItems(child.getJSONArray("c"), currentLayer, node);
            if (children.size() > 0) {
                item.setChildren(createCrossPartItems(child.getJSONArray("c"), currentLayer, node));
                item.setExpanded(true);
            }
        }
        boolean hasSum = crossItemSums.get(currentLayer).length() >= i && crossItemSums.get(currentLayer).getBoolean(i);
        // FIXME: 2017/2/23 这个地方有问题，获取的data明显和前台不一致，top数据直接默认为max了
        boolean showColAndSums = showColTotal && hasSum;
        boolean childExist = null != item.getChildren() && item.getChildren().size() > 0;
        if (showColAndSums && childExist) {
            JSONArray itemList = new JSONArray();
            if (isOnlyCrossAndTarget()) {
                itemList.put(new JSONArray());
            } else {
                int size = targetIds.size();
                JSONArray jsonArray = new JSONArray();
                for (int j = 0; j < size; j++) {
                    jsonArray.put(j);
                }
                for (int j = 0; j < size; j++) {
                    itemList.put(jsonArray);
                }
            }
            item.setValue(itemList);
        }
        if (showColTotal || null != item.getChildren()) {
            JSONArray itemList = new JSONArray();
            if (isOnlyCrossAndTarget()) {
                itemList.put(new BIExcelTableItem().createJSON());
            } else {
                for (int j = 0; j < targetIds.size(); j++) {
                    itemList.put(new String());
                }
            }
            item.setValue(itemList);
        }
        return item;
    }

    /**
     * 通用的创建items方法
     *
     * @param strC         json结构中的c节点
     * @param currentLayer 当前所在层数
     * @param parentNode   父节点node
     * @param dimIds       行表头
     * @param crossPV      交叉表部分的parentValues 为了通用于交叉表和复杂表
     * @returns {Array}
     * @private
     */
    protected List createCommonTableItems(String strC, int currentLayer, ReportNode parentNode, List<String> dimIds, JSONArray crossPV) throws Exception {
        List items = new ArrayList();
        currentLayer++;
        if (BIJsonUtils.isArray(strC)) {
            JSONArray c = new JSONArray(strC);
            for (int i = 0; i < c.length(); i++) {
                JSONObject child = c.getJSONObject(i);
                String cId = child.has("n") ? child.getString("n") : BIStringUtils.emptyString();
                String nodeId = null != parentNode ? parentNode.getId() + cId : cId;
                ReportNode node = new ReportNode();
                node.setId(nodeId);
                String currDid = dimIds.get(currentLayer - 1);
                String currValue = child.getString("n");
                node.setName(currValue);
                node.setdId(currDid);
                this.tree.addNode(parentNode, node);
                JSONArray pValues = new JSONArray();
                int tempLayer = currentLayer;
                String tempNodeId = nodeId;
                while (tempLayer > 0) {
                    String pv = tree.getNode(tempNodeId).getName();
                    String dId = dimIds.get(tempLayer - 1);
                    JSONArray value = getClickedValue4Group(pv, dId);
                    pValues.put(new JSONObject().put("value", value).put("dId", dId));
                    tempLayer--;
                    tempNodeId = tree.getNode(tempNodeId).getParent().getdId();
                }
                BIExcelTableItem item = new BIExcelTableItem();
                item.setType("bi.normal_expander_cell");
                item.setText(child.getString("n"));
                item.setdId(currDid);
                item.setStyle("");
                //展开情况——最后一层没有这个展开按钮
                if (currentLayer < dimIds.size()) {
                    item.setNeedExpand(true);
                    item.setExpanded(false);
                }
                //有c->说明有children，构造children，并且需要在children中加入汇总情况（如果有并且需要）
                if (child.has("c")) {
                    hasChildren(currentLayer, dimIds, crossPV, child, node, pValues, item);
                } else {
                    hasNoneChildren(crossPV, i, child, pValues, item);
                }
                items.add(item.createJSON());
            }
        }
        return items;
    }

    private void hasChildren(int currentLayer, List<String> dimIds, JSONArray crossPV, JSONObject child, ReportNode node, JSONArray pValues, BIExcelTableItem item) throws Exception {
        List children = createCommonTableItems(child.getString("c"), currentLayer, node, dimIds, crossPV);
        item.setChildren(children);
        //在tableForm为 行展开模式 的时候 如果不显示汇总行 只是最后一行不显示汇总
        // TODO: 2017/2/22 如何定义tableFrom属性从而来确定是否为行展开？
        boolean openCol = true;
        if (showColTotal || openCol) {
            JSONArray vs = new JSONArray();
            JSONArray summary = getOneRowSummary(child.getString("s"));
            int tartSize = targetIds.size();
            for (int j = 0; j < summary.length(); j++) {
                BIExcelTableItem tarItem = new BIExcelTableItem();
                tarItem.setType("bi.target_body_normal_cell");
                tarItem.setText(summary.getString(j));
                tarItem.setdId(targetIds.get(j % tartSize));
                tarItem.setClicked(pValues);
                vs.put(tarItem.createJSON());
            }
            item.setValue(vs);
        }
        item.setExpanded(true);
    }

    private void hasNoneChildren(JSONArray crossPV, int i, JSONObject child, JSONArray pValues, BIExcelTableItem item) throws Exception {
        if (child.has("s")) {
            JSONArray values = new JSONArray();
            boolean hasSC = BIJsonUtils.isKeyValueSet(child.getString("s")) && child.getJSONObject("s").has("c");
            boolean isArraySS = BIJsonUtils.isKeyValueSet(child.getString("s")) && BIJsonUtils.isArray(child.getJSONObject("s").getString("s"));
            if (hasSC || isArraySS) {
                JSONObject childS = child.getJSONObject("s");
                //交叉表，pValue来自于行列表头的结合
                JSONObject ob = new JSONObject().put("index", 0);
                createTableSumItems(childS.getString("c"), values, pValues, ob, false, i, crossPV);
                //显示列汇总 有指标
                if (showColTotal && targetIds.size() > 0) {
                    createTableSumItems(childS.getString("s"), values, pValues, ob, false, i, crossPV);
                }
            } else {
                JSONArray array = child.getJSONArray("s");
                for (int j = 0; j < array.length(); j++) {
                    String tId = targetIds.get(j);
                    BIExcelTableItem tarItem = new BIExcelTableItem();
                    tarItem.setType("bi.target_body_normal_cell");
                    tarItem.setText(array.getString(j));
                    tarItem.setClicked(pValues);
                    tarItem.setdId(tId);
                    values.put(tarItem.createJSON());
                }
            }
            item.setValue(values);
        }
    }

    /**
     * 交叉表的(指标)汇总值
     *
     * @param s        json中的s节点数据
     * @param sum      汇总格子列表
     * @param pValues  parentValues
     * @param ob       记录index
     * @param isLast   是否为最后一个
     * @param rowIndex 行号（用于样式）
     * @param crossPV  交叉部分的parentValues
     * @private
     */
    private void createTableSumItems(String s, JSONArray sum, JSONArray pValues, JSONObject ob, boolean isLast, int rowIndex, JSONArray crossPV) throws Exception {
        if (!BIJsonUtils.isArray(s)) {
            return;
        }
        JSONArray jsonArray = new JSONArray(s);
        for (int i = 0; i < jsonArray.length(); i++) {
            String v = jsonArray.getString(i);
            if (BIJsonUtils.isKeyValueSet(v)) {
                String sums = new JSONObject(v).has("s") ? new JSONObject(v).getString("s") : null;
                String child = new JSONObject(v).has("c") ? new JSONObject(v).getString("c") : null;
                if (null != sums && null != child) {
                    createTableSumItems(child, sum, pValues, ob, isLast, rowIndex, crossPV);
                    if (showColTotal) {
                        createTableSumItems(sums, sum, pValues, ob, isLast, rowIndex, crossPV);
                    }
                } else if (null != sums) {
                    createTableSumItems(sums, sum, pValues, ob, isLast, rowIndex, crossPV);
                }
            } else {
                String tId = targetIds.get(i);
                if (targetIds.size() == 0) {
                    tId = crossDimIds.get(i);
                }
                BIExcelTableItem tarItem = new BIExcelTableItem();
                tarItem.setdId(tId);
                tarItem.setType("bi.target_body_normal_cell");
                tarItem.setText(v);
                if (crossPV.length() > ob.getInt("index")) {
                    JSONArray array = pValues.put(crossPV.get(ob.getInt("index")));
                    tarItem.setClicked(array);
                } else {
                    tarItem.setClicked(pValues);
                }
                sum.put(tarItem.createJSON());
                ob.put("index", ob.getInt("index") + 1);
            }
        }
    }

    private boolean isOnlyCrossAndTarget() {
        return dimIds.size() == 0 && crossDimIds.size() > 0 && targetIds.size() > 0;
    }

    protected JSONArray getOneRowSummary(String sums) throws JSONException {
        JSONArray summary = new JSONArray();
        //对于交叉表的汇总 s: {c: [{s: [200, 300]}, {s: [0, 0]}], s: [100, 500]}
        if (BIJsonUtils.isKeyValueSet(sums)) {
            JSONArray array = new JSONArray(sums);
            for (int i = 0; i < array.length(); i++) {
                String sum = array.getString(i);
                if (BIJsonUtils.isKeyValueSet(sum)) {
                    summary.put(getOneRowSummary(sum));
                }
            }
        } else if (BIJsonUtils.isKeyValueSet(sums)) {
            JSONObject jsonObject = new JSONObject(sums);
            String c = jsonObject.getString("c");
            String s = jsonObject.getString("s");
            //是否显示列汇总 并且有指标
            if (null != c && null != s) {
                summary.put(getOneRowSummary(s));
                if (showColTotal && targetIds.size() > 0) {
                    summary.put(getOneRowSummary(s));
                }
            } else if (null != s) {
                summary.put(getOneRowSummary(s));
            }
        }
        return summary;
    }

    //根据text dId 获取clicked 处理分组的情况
    protected JSONArray getClickedValue4Group(String pv, String dId) throws Exception {
        String group = ExportDataHelper.getDimensionNameByID(widget, dId);
        int filedType = ExportDataHelper.getFieldTypeByDimensionID(widget, dId);
        JSONArray clicked = new JSONArray();
        if (null != group) {
            if (filedType == DBConstant.COLUMN.STRING) {

            } else if (filedType == DBConstant.COLUMN.NUMBER) {

            }
        }
        return clicked;
    }


}
