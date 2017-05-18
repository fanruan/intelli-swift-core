package com.fr.bi.cal.analyze.report.report.widget.chart.export.calculator;

import com.fr.bi.cal.analyze.report.report.widget.chart.export.item.*;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.BITableExportDataHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.SummaryTableStyleHelper;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.node.ReportNode;
import com.fr.bi.cal.analyze.report.report.widget.chart.export.utils.node.ReportNodeTree;
import com.fr.bi.conf.report.widget.IWidgetStyle;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.utils.program.BIJsonUtils;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.general.Inter;
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
public abstract class TableAbstractDataBuilder implements IExcelDataBuilder {
    List<ITableHeader> crossHeaders;
    List<ITableItem> crossItems;
    List<String> crossDimIds;
    List<JSONArray> crossItemSums;
    //fixme 尽量避免使用json来代替对象
    protected List<ITableItem> items;
    protected List<ITableHeader> headers;

    protected JSONObject data;
    Map<Integer, List<JSONObject>> dimAndTar;
    protected IWidgetStyle styleSetting;
    protected ReportNodeTree tree;
    protected List<String> dimIds;
    protected List<String> targetIds;
    protected boolean showColTotal;
    protected boolean showRowTotal;
    protected static final String EMPTY_VALUE = "--";
    protected static final String SUMMARY = Inter.getLocText("BI-Summary_Values");
    protected static String OUTERSUM = "__outer_sum_";

    public TableAbstractDataBuilder(Map<Integer, List<JSONObject>> dimAndTar, JSONObject dataJSON, IWidgetStyle styleSettings) throws Exception {
        this.data = dataJSON;
        this.dimAndTar = dimAndTar;
        this.styleSetting = styleSettings;
    }

    protected void initAllAttrs() {
        tree = new ReportNodeTree();
        dimIds = new ArrayList<String>();
        targetIds = new ArrayList<String>();
        crossItems = new ArrayList<ITableItem>();
        crossDimIds = new ArrayList<String>();
        crossItemSums = new ArrayList<JSONArray>();
        items = new ArrayList<ITableItem>();
        headers = new ArrayList<ITableHeader>();
        crossHeaders = new ArrayList<ITableHeader>();
        showColTotal = this.styleSetting.isShowColTotal();
        showRowTotal = this.styleSetting.isShowRowTotal();
    }

    protected void amendmentData() throws JSONException {
        JSONObject cloneData = new JSONObject(this.data.toString());
        this.data.put("t", new JSONObject().put("c", getTopOfCrossByGroupData(cloneData.getJSONArray("c"))));
        this.data.put("l", new JSONObject().put("s", cloneData));
    }

    protected void createTableItems() throws Exception {
        int currentLayer = 0;
        BIBasicTableItem item = new BIBasicTableItem();
        List<ITableItem> children = new ArrayList<ITableItem>();
        if (data.has("c")) {
            children = createCommonTableItems(data.getString("c"), currentLayer, null, dimIds);
        }
        item.setChildren(children);
        //汇总
        boolean isArrayAvailable = data.has("s") && isValidArray(data.getJSONArray("s"));
        if (showColTotal && isArrayAvailable) {
            List<ITableItem> outerValues = new ArrayList<ITableItem>();
            JSONArray s = data.getJSONArray("s");
            if (dimIds.size() > 0) {
                List<ITableItem> values = new ArrayList<ITableItem>();
                for (int i = 0; i < s.length(); i++) {
                    ITableItem temp = new BIBasicTableItem();
                    temp.setValue(s.optString(i));
                    temp.setDId(targetIds.get(i));
                    temp.setStyles(SummaryTableStyleHelper.getBodyStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup(), i));
                    values.add(temp);
                }
                item.addValues(values);
            } else {
                //使用第一个值作为一个维度
                for (int i = 0; i < s.length(); i++) {
                    BIBasicTableItem temp = new BIBasicTableItem();
                    temp.setStyles(SummaryTableStyleHelper.getBodyStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup(), i));
                    temp.setValue(s.getString(i));
                    temp.setDId(targetIds.get(i));
                }
                ITableItem temp = new BIBasicTableItem();
                temp.setValue(data.getJSONArray("s").getString(0));
                temp.setDId(targetIds.get(0));
                temp.setValues(outerValues);
                item.getChildren().add(temp);
            }
        }
        items.add(item);
    }

    private boolean isValidArray(JSONArray array) throws JSONException {

        try {
            for (int i = 0; i < array.length(); i++) {
                array.get(i);
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    protected void createTableHeader() throws Exception {
        List<String> allIds = new ArrayList<String>(dimIds);
        allIds.addAll(targetIds);
        for (String dimId : allIds) {
            BITableHeader header = new BITableHeader();
            header.setdID(dimId);
            header.setText(BITableExportDataHelper.getDimensionNameByID(dimAndTar, dimId));
            header.setUsed(BITableExportDataHelper.isDimUsed(dimAndTar, dimId));
            header.setStyles(SummaryTableStyleHelper.getHeaderStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
            headers.add(header);
        }
    }


    protected void setOtherAttrs4OnlyCross() throws Exception {
        parseSizeOfCrossItems(this.crossItems);
    }

    private void parseSizeOfCrossItems(List<ITableItem> items) throws Exception {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getChildren() != null && items.get(i).getChildren().size() > 0) {
                parseHeader(items.get(i).getChildren());
            }
        }
    }

    protected void createCrossItems4OnlyCross() throws Exception {
        ITableItem crossItem = new BIBasicTableItem();
        //交叉表items
        List<ITableItem> c = createCrossPartItems(data.getJSONArray("c"), 0, null);
        crossItem.setChildren(c);
        List<ITableItem> itemList = new ArrayList<ITableItem>();
        itemList.add(crossItem);
        crossItems = itemList;
    }

    //仅有列表头的交叉表
    protected void createCrossHeader4OnlyCross() throws Exception {
        for (int i = 0; i < crossDimIds.size(); i++) {
            BITableHeader header = new BITableHeader();
            header.setdID(crossDimIds.get(i));
            header.setText(BITableExportDataHelper.getDimensionNameByID(dimAndTar, crossDimIds.get(i)));
            header.setStyles(null);
            crossHeaders.add(header);
        }
    }


    //从分组表样式的数据获取交叉表数据样式
    // TODO: 2017/2/21
    protected JSONArray getTopOfCrossByGroupData(JSONArray c) throws JSONException {
        JSONArray newC = new JSONArray();
        for (int i = 0; i < c.length(); i++) {
            JSONObject obj = new JSONObject();
            JSONObject child = c.getJSONObject(i);
            if (child.has("c")) {
                obj.put("c", getTopOfCrossByGroupData(child.getJSONArray("c")));
                if (child.has("n")) {
                    obj.put("n", child.getJSONObject("n"));
                }
                newC.put(obj);
                return newC;
            }
            if (child.has("n")) {
                JSONArray tempArray = new JSONArray();
                for (int j = 0; j < targetIds.size(); j++) {
                    tempArray.put(new JSONObject().put("n", targetIds.get(j)));
                }
                newC.put(new JSONObject().put("c", tempArray).put("n", child.getString("n")));
            }
        }
        return newC;
    }

    /**
     * 交叉表 items and crossItems
     */
    protected void createCrossTableItems() throws Exception {
        JSONObject top = data.getJSONObject("t");
        JSONObject left = data.getJSONObject("l");
        //交叉表items
        this.crossItems = createCrossItems(top);
        //无行表头 有列表头、指标
        if (isOnlyCrossAndTarget()) {
            items = createItems4OnlyCrossAndTarget(this.data);
            return;
        }
        createItems4Cross(left);
    }

    private void createItems4Cross(JSONObject left) throws Exception {
        BIBasicTableItem item = new BIBasicTableItem();
        item.setChildren(createCommonTableItems(left.optString("c"), 0, null, dimIds));
        if (showRowTotal) {
            //汇总值
            List<ITableItem> sums = new ArrayList<ITableItem>();
            boolean hasSC = BIJsonUtils.isKeyValueSet(left.getString("s")) && left.getJSONObject("s").has("c");
            boolean hasSS = BIJsonUtils.isKeyValueSet(left.getString("s")) && left.getJSONObject("s").has("s");
            if (hasSC && hasSS) {
                createTableSumItems(left.getJSONObject("s").getString("c"), sums);
            } else {
                if (BIJsonUtils.isArray(left.getString("s"))) {
                    createTableSumItems(left.getString("s"), sums);
                }
            }
            if (showColTotal) {
                JSONArray ss = left.getJSONObject("s").getJSONArray("s");
                for (int i = 0; i < ss.length(); i++) {
                    if (targetIds.size() > 0) {
                        String tId = targetIds.get(i);
                        BIBasicTableItem tempItem = new BIBasicTableItem();
                        tempItem.setValue(ss.optString(i));
                        tempItem.setDId(tId);
                        tempItem.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                        sums.add(tempItem);
                    }
                }
            }
            item.setValues(sums);
        }
        this.items = new ArrayList<ITableItem>();
        items.add(item);
    }

    protected List<ITableItem> createCrossItems(JSONObject top) throws Exception {
        ITableItem crossItem = new BIBasicTableItem();
        List<ITableItem> children = createCrossPartItems(top.getJSONArray("c"), 0, new ReportNode());
        crossItem.setChildren(children);
        if (showColTotal) {
            if (isOnlyCrossAndTarget()) {
                BIBasicTableItem item = new BIBasicTableItem();
                item.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                item.setValue(SUMMARY);
                crossItem.getChildren().add(item);
            } else {
                for (String targetId : targetIds) {
                    BIBasicTableItem item = new BIBasicTableItem();
                    item.setValue(SUMMARY);
                    item.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                    item.setDId(targetId);
                    crossItem.getChildren().add(item);
                }
            }
        }
        List<ITableItem> crossItems = new ArrayList<ITableItem>();
        crossItems.add(crossItem);
        return crossItems;
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
        if (dimAndTar.containsKey(Integer.valueOf(BIReportConstant.REGION.TARGET1))) {
            for (JSONObject s : dimAndTar.get(Integer.valueOf(BIReportConstant.REGION.TARGET1))) {
                targetIds.add(s.getString("dId"));
            }
        }
    }

    //仅有列表头和指标 l: {s: {c: [{s: [1, 2]}, {s: [3, 4]}], s: [100, 200]}}
    private List<ITableItem> createItems4OnlyCrossAndTarget(JSONObject dataJSON) throws Exception {
        JSONObject l = dataJSON.getJSONObject("l");
        for (int i = 0; i < targetIds.size(); i++) {
            BIBasicTableItem ob = new BIBasicTableItem();
            ob.setValue(BITableExportDataHelper.getDimensionNameByID(dimAndTar, targetIds.get(i)));
            ob.setStyles(SummaryTableStyleHelper.getBodyStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup(), i));
            BIBasicTableItem child = new BIBasicTableItem();
            List<ITableItem> childItems = new ArrayList<ITableItem>();
            childItems.add(ob);
            child.setChildren(childItems);
            items.add(child);
        }
        createItems(items, l.getJSONObject("s"));
        return items;
    }

    private void createItems(List<ITableItem> items, JSONObject data) throws Exception {
        if (BIJsonUtils.isArray(data.getString("c")) && data.getJSONArray("c").length() > 0) {
            JSONArray c = data.getJSONArray("c");
            for (int i = 0; i < c.length(); i++) {
                JSONObject child = c.getJSONObject(i);
                if (child.has("s") && child.has("c")) {
                    createItems(items, child);
                } else if (child.has("s")) {
                    for (int j = 0; j < child.getJSONArray("s").length(); j++) {
                        ITableItem children = items.get(j).getChildren().get(0);
                        if (!children.hasValues()) {
                            children.setValues(new ArrayList<ITableItem>());
                        }
                        BIBasicTableItem ob = new BIBasicTableItem();
                        ob.setValue(child.getJSONArray("s").getString(j));
                        ob.setStyles(SummaryTableStyleHelper.getBodyStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup(), j));
                        List<ITableItem> values = null == children.getValues() ? new ArrayList<ITableItem>() : children.getValues();
                        values.add(ob);
                        children.setValues(values);
                    }
                }
            }
        }
        if (showColTotal) {
            if (BIJsonUtils.isArray(data.getString("s")) && data.getJSONArray("s").length() > 0) {
                JSONArray s = data.getJSONArray("s");
                for (int j = 0; j < s.length(); j++) {
                    if (!items.get(j).getChildren().get(0).hasValues()) {
                        items.get(j).getChildren().get(0).setValues(new ArrayList<ITableItem>());
                    }
                    BIBasicTableItem ob = new BIBasicTableItem();
                    ob.setValue(s.getString(j));
                    ob.setStyles(SummaryTableStyleHelper.getBodyStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup(), j));
                    ob.setDId(targetIds.get(j));
                    items.get(j).getChildren().get(0).getValues().add(ob);
                }
            }
        }
    }

    /**
     * 交叉表——header and crossHeader
     */
    protected void createCrossTableHeader() throws Exception {
        for (String dimId : dimIds) {
            BITableHeader header = new BITableHeader();
            header.setdID(dimId);
            header.setText(BITableExportDataHelper.getDimensionNameByID(dimAndTar, dimId));
            this.headers.add(header);
        }
        for (String crossDims : crossDimIds) {
            BITableHeader crossHeader = new BITableHeader();
            crossHeader.setdID(crossDims);
            crossHeader.setText(BITableExportDataHelper.getDimensionNameByID(dimAndTar, crossDims));
            crossHeaders.add(crossHeader);
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

    private void parseHeader(List<ITableItem> items) throws Exception {
        for (int i = 0; i < items.size(); i++) {
            String dName;
            if (targetIds.size() == 0) {
                dName = EMPTY_VALUE;
            } else {
                dName = BITableExportDataHelper.getDimensionNameByID(dimAndTar, targetIds.get(i % (targetIds.size())));
            }
            ITableItem item = items.get(i);
            if (item.getChildren() != null && item.getChildren().size() != 0) {
                parseHeader(item.getChildren());
                if (item.getValues() != null && showColTotal) {
                    //合计
                    for (String targetId : targetIds) {
                        BITableHeader header = new BITableHeader();
                        header.setText(SUMMARY + BITableExportDataHelper.getDimensionNameByID(dimAndTar, targetId));
                        header.setTitle(SUMMARY + BITableExportDataHelper.getDimensionNameByID(dimAndTar, targetId));
                        header.setTag(UUID.randomUUID().toString());
                        header.setType("bi.page_table_cell");
                        header.setStyles(SummaryTableStyleHelper.getHeaderStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                        headers.add(header);
                    }
                }
            } else if (isSUm(item.getDId())) {
                //合计
                BITableHeader header = new BITableHeader();
                header.setText(SUMMARY + BITableExportDataHelper.getDimensionNameByID(dimAndTar, item.getDId()));
                header.parseJson(item.createJSON());
                header.setSum(true);
                headers.add(header);
            } else if (!(item.getValues() == null || item.getValues().size() == 0)) {
                //单指标情况下，指标不显示，合并到上面
                if (targetIds.size() == 1) {
                    BITableHeader header = new BITableHeader();
                    header.setText(item.getValue());
                    headers.add(header);
                } else {
                    List<ITableItem> values = item.getValues();
                    for (int j = 0; j < values.size(); j++) {
                        BITableHeader header = new BITableHeader();
                        header.setText(BITableExportDataHelper.getDimensionNameByID(dimAndTar, targetIds.get(j)));
                        header.setTitle(BITableExportDataHelper.getDimensionNameByID(dimAndTar, targetIds.get(j)));
                        header.setTag(UUID.randomUUID().toString());
                        header.setType("bi.page_table_cell");
                        headers.add(header);
                    }
                }
            } else {
                BITableHeader header = new BITableHeader();
                header.setText(dName);
                header.setType("bi.page_table_cell");
                header.setTitle(dName);
                header.setTag(UUID.randomUUID().toString());
                headers.add(header);
            }
        }
    }

    private boolean isSUm(String dId) {
        return !BIStringUtils.isBlankString(dId) && targetIds.contains(dId);
    }


    /**
     * 初始化 crossItemsSum
     */
//    private void initCrossItemsSum(int currentLayer, JSONArray sums, List<JSONArray> crossItemsSums) throws JSONException {
//        currentLayer++;
//        for (int i = 0; i < sums.length(); i++) {
//            JSONObject v = sums.getJSONObject(i);
//            if (null != v && v.has("c")) {
//                initCrossItemsSum(currentLayer, v.getJSONArray("c"), crossItemsSums);
//            }
////            if (crossItemsSums.size() <= currentLayer) {
////                crossItemsSums.add(currentLayer, new JSONArray());
////            }
//            if (crossItemsSums.size() < currentLayer) {
//                crossItemsSums.add(crossItemsSums.size(), new JSONArray());
//                crossItemsSums.get(crossItemsSums.size() - 1).put(!v.isNull("c"));
//            }
//        }
//    }

    /**
     * 交叉表——crossItems
     */
    protected List<ITableItem> createCrossPartItems(JSONArray c, int currentLayer, ReportNode parent) throws Exception {
        List items = new ArrayList();
        List<ITableItem> crossHeaderIItems = new ArrayList();
        currentLayer++;
        for (int i = 0; i < c.length(); i++) {
            JSONObject child = c.getJSONObject(i);
            boolean hasC = child.has("c");
            boolean existInTargets = targetIds.contains(child.getString("n"));
            boolean existInDims = crossDimIds.contains(child.getString("n"));
            boolean isExisted = existInTargets || existInDims;
            if (!hasC && isExisted) {
                return items;
            }
            String cId = child.has("n") ? UUID.randomUUID().toString() : child.getString("n");
            String currDid = crossDimIds.get(currentLayer - 1);
            String currValue = child.getString("n");
            String nodeId = null != parent ? parent.getId() + cId : cId;
            ReportNode node = new ReportNode(nodeId);
            node.setName(child.getString("n"));
            node.setdId(currDid);
            this.tree.addNode(parent, node);
            int tempLayer = currentLayer;
            String tempNodeId = nodeId;
            while (tempLayer > 0) {
                String dId = crossDimIds.get(tempLayer - 1);
                ReportNode itemNode = tree.getNode(tempNodeId);
                JSONObject json = new JSONObject();
                json.put("value", itemNode.getName() + dId);
                json.put("dId", dId);
                tempNodeId = itemNode.getParent().getId();
                tempLayer--;
            }
            BIBasicTableItem item = setPartItem(currentLayer, i, child, currDid, currValue, node);
            crossHeaderIItems.add(item);
        }
        return crossHeaderIItems;
    }

    private BIBasicTableItem setPartItem(int currentLayer, int i, JSONObject child, String currDid, String currValue, ReportNode node) throws Exception {
        BIBasicTableItem item = new BIBasicTableItem();
        item.setValue(currValue);
        item.setDId(currDid);
        if (currentLayer < crossDimIds.size()) {
            item.setNeedExpand(true);
            item.setExpanded(false);
        }
        if (child.has("c") && child.getJSONArray("c").length() > 0) {
            List children = createCrossPartItems(child.getJSONArray("c"), currentLayer, node);
            if (children.size() > 0) {
                item.setChildren(createCrossPartItems(child.getJSONArray("c"), currentLayer, node));
                item.setExpanded(true);
            }
        }
        boolean hasSum = crossItemSums.size() > currentLayer && crossItemSums.get(currentLayer).length() > i && crossItemSums.get(currentLayer).getBoolean(i);
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
            item.setValues(convertToItemList(itemList));
        }
        if (showColTotal || null == item.getChildren()) {
            JSONArray itemList = new JSONArray();
            if (isOnlyCrossAndTarget()) {
                itemList.put(new BIBasicTableItem().createJSON());
            } else {
                for (int j = 0; j < targetIds.size(); j++) {
                    itemList.put(new String());
                }
            }
            item.setValues(convertToItemList(itemList));
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
     * @returns {Array}
     * @private
     */
    protected List<ITableItem> createCommonTableItems(String strC, int currentLayer, ReportNode parentNode, List<String> dimIds) throws Exception {
        List<ITableItem> items = new ArrayList<ITableItem>();
        currentLayer++;
        if (BIJsonUtils.isArray(strC)) {
            JSONArray c = new JSONArray(strC);
            for (int i = 0; i < c.length(); i++) {
                JSONObject child = c.getJSONObject(i);
                //可以直接使用每一层中的树节点的parent.id + child.n作为id，第一层无需考虑，因为第一层不可能有相同值
                //考虑到空字符串问题
                String cId = child.has("n") ? child.getString("n") : BIStringUtils.emptyString();
                String nodeId = null != parentNode ? parentNode.getId() + cId : cId;
                ReportNode node = new ReportNode();
                node.setId(nodeId);
                String currDid = dimIds.get(currentLayer - 1);
                String currValue = child.getString("n");
                node.setName(currValue);
                node.setdId(currDid);
                this.tree.addNode(parentNode, node);
//                JSONArray pValues = new JSONArray();
                int tempLayer = currentLayer;
                String tempNodeId = nodeId;
                while (tempLayer > 0) {
                    tempLayer--;
                    tempNodeId = tree.getNode(tempNodeId).getParent().getId();
                }
                BIBasicTableItem item = new BIBasicTableItem();
                item.setValue(child.getString("n"));
                item.setDId(currDid);
                item.setStyles(SummaryTableStyleHelper.getBodyStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup(), i));
                //展开情况——最后一层没有这个展开按钮
                item.setNeedExpand(currentLayer < dimIds.size());
                item.setExpanded(child.has("c"));
                //有c->说明有children，构造children，并且需要在children中加入汇总情况（如果有并且需要）
                if (child.has("c")) {
                    hasChildren(currentLayer, dimIds, child, node, item);
                } else if (child.has("s")) {
                    hasNoneChildren(child, item);
                }
                items.add(item);
            }
        }
        return items;
    }

    private void hasChildren(int currentLayer, List<String> dimIds, JSONObject child, ReportNode node, BIBasicTableItem item) throws Exception {
        List children = createCommonTableItems(child.getString("c"), currentLayer, node, dimIds);
        item.setChildren(children);
        if (showRowTotal) {
            List<ITableItem> vs = new ArrayList<ITableItem>();
            JSONArray summary = getOneRowSummary(child.getString("s"));
            int tartSize = targetIds.size();
            for (int j = 0; j < summary.length(); j++) {
                BIBasicTableItem tarItem = new BIBasicTableItem();
                tarItem.setValue(summary.getString(j));
                tarItem.setDId(targetIds.get(j % tartSize));
                tarItem.setStyles(item.getStyles());
                vs.add(tarItem);
            }
            item.setValues(vs);
        }
    }

    private void hasNoneChildren(JSONObject child, BIBasicTableItem item) throws Exception {
        if (child.has("s")) {
            List<ITableItem> values = new ArrayList<ITableItem>();
            boolean hasSC = BIJsonUtils.isKeyValueSet(child.getString("s")) && child.getJSONObject("s").has("c");
            boolean isArraySS = BIJsonUtils.isKeyValueSet(child.getString("s")) && BIJsonUtils.isArray(child.getJSONObject("s").getString("s"));
            if (hasSC || isArraySS) {
                JSONObject childS = child.getJSONObject("s");
                //交叉表，pValue来自于行列表头的结合
                createTableSumItems(childS.getString("c"), values);
                //显示列汇总 有指标
                if (showColTotal && targetIds.size() > 0) {
                    createTableSumItems(childS.getString("s"), values);
                }
            } else {
                JSONArray array = child.getJSONArray("s");
                for (int j = 0; j < array.length(); j++) {
                    String tId = targetIds.get(j);
                    BIBasicTableItem tarItem = new BIBasicTableItem();
                    tarItem.setValue(array.getString(j));
                    tarItem.setDId(tId);
                    tarItem.setStyles(item.getStyles());
                    values.add(tarItem);
                }
            }
            item.setValues(values);
        }
    }

    /**
     * 交叉表的(指标)汇总值
     *
     * @param s   json中的s节点数据
     * @param sum 汇总格子列表
     * @private
     */
    private void createTableSumItems(String s, List<ITableItem> sum) throws Exception {
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
                    createTableSumItems(child, sum);
                    if (showColTotal) {
                        createTableSumItems(sums, sum);
                    }
                } else if (null != sums) {
                    createTableSumItems(sums, sum);
                }
            } else {
                String tId;
                if (targetIds.size() == 0) {
                    tId = crossDimIds.get(i);
                } else {
                    tId = targetIds.get(i);
                }
                if (targetIds.size() == 0) {
                    tId = crossDimIds.get(i);
                }
                BIBasicTableItem tarItem = new BIBasicTableItem();
                tarItem.setDId(tId);
                tarItem.setValue(v);
                tarItem.setStyles(SummaryTableStyleHelper.getLastSummaryStyles(styleSetting.getThemeColor(), styleSetting.getTableStyleGroup()));
                sum.add(tarItem);
            }
        }
    }

    private boolean isOnlyCrossAndTarget() {
        return dimIds.size() == 0 && crossDimIds.size() > 0 && targetIds.size() > 0;
    }

    protected JSONArray getOneRowSummary(String sums) throws JSONException {
        JSONArray summary = new JSONArray();
        //对于交叉表的汇总 s: {c: [{s: [200, 300]}, {s: [0, 0]}], s: [100, 500]}
        if (BIJsonUtils.isArray(sums)) {
            JSONArray array = new JSONArray(sums);
            for (int i = 0; i < array.length(); i++) {
                String sum = array.getString(i);
                if (BIJsonUtils.isKeyValueSet(sum)) {
//                    summary.put(getOneRowSummary(sum));
                    summary = BIJsonUtils.arrayConcat(summary, getOneRowSummary(sum));
                } else {
                    summary.put(sum);
                }
            }
        } else if (BIJsonUtils.isKeyValueSet(sums)) {
            JSONObject jo = new JSONObject(sums);
            String c = jo.has("c") ? jo.getString("c") : null;
            String s = jo.has("s") ? jo.getString("s") : null;
            //是否显示列汇总 并且有指标
            if (null != c && null != s) {
                summary = BIJsonUtils.arrayConcat(summary, getOneRowSummary(c));
                if (showColTotal && targetIds.size() > 0) {
                    summary = BIJsonUtils.arrayConcat(summary, getOneRowSummary(s));
                }
            } else if (null != s) {
                summary = BIJsonUtils.arrayConcat(summary, getOneRowSummary(s));
            }
        }
        return summary;
    }

    protected void setOtherAttrs() {
    }

    @Override
    public void createTargetStyles() {

    }

    protected List<ITableItem> convertToItemList(JSONArray jsonArray) throws Exception {
        List<ITableItem> items = new ArrayList<ITableItem>();
        for (int i = 0; i < jsonArray.length(); i++) {
            BIBasicTableItem item = new BIBasicTableItem();
            if (BIJsonUtils.isKeyValueSet(jsonArray.getString(0))) {
                item.parseJSON(jsonArray.getJSONObject(i));
            }
            items.add(item);
        }
        return items;
    }
}
