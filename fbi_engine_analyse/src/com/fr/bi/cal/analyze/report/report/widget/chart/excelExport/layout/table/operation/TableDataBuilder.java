package com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.operation;

import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.nodeTree.ReportNode;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.nodeTree.ReportNodeTree;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic.ReportItem;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic.ReportTableHeader;
import com.fr.bi.cal.analyze.report.report.widget.chart.excelExport.layout.table.basic.TableDataForExport;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by Kary on 2017/2/16.
 */
public class TableDataBuilder {
    private TableWidget widget;
    private JSONObject dataJSON;
    private ReportNodeTree itemTree;
    private ReportNodeTree crossItemTree;
    //fixme
    private JSONArray crossSum = new JSONArray();

    public TableDataBuilder(TableWidget widget, JSONObject dataJSON) {
        this.widget = widget;
        this.dataJSON = dataJSON;
    }

    public TableDataForExport buildTableData() throws JSONException {
        TableDataForExport tableDataForExport;
        List<ReportTableHeader> headers = buildHeaders();
        boolean isCross = dataJSON.getJSONObject("data").has("t");
        if (!isCross) {
            Map items = createCommonTableItems();
            tableDataForExport = new TableDataForExport(items, headers, null, null);
        } else {
            List<ReportItem> items = createCommonTableItems(dataJSON.getJSONObject("data").getJSONArray("c"), 0, null, new ReportNodeTree());
            List<ReportTableHeader> crossHeaders = buildCrossHeaders();
            List<ReportItem> crossItems = createCrossItems();
            tableDataForExport = new TableDataForExport(null, headers, null, crossHeaders);
        }
        return tableDataForExport;
    }


    private List<ReportTableHeader> buildHeaders() {
        List<ReportTableHeader> headers = new ArrayList<ReportTableHeader>();
        for (BIDimension dimension : widget.getViewDimensions()) {
            ReportTableHeader header = new ReportTableHeader(dimension.getValue(), dimension.getText(), dimension.getText());
            headers.add(header);
        }
        for (BISummaryTarget biSummaryTarget : widget.getViewTargets()) {
            ReportTableHeader header = new ReportTableHeader(biSummaryTarget.getValue(), biSummaryTarget.getText(), biSummaryTarget.getName());
            headers.add(header);
        }
        return headers;
    }

    private List<ReportTableHeader> buildCrossHeaders() {
        List<ReportTableHeader> headers = new ArrayList<ReportTableHeader>();
        return headers;
    }

    private Map createCommonTableItems() throws JSONException {
        List<ReportItem> items = createCommonTableItems(dataJSON.getJSONObject("data").getJSONArray("c"), 0, null, new ReportNodeTree());
        Map item = new HashMap<String, ReportTableHeader>();
        item.put("children", items);
        Map value = createItemValue(item, dataJSON);
        return value;
    }

    private Map createItemValue(Map itemMap, JSONObject dataJSON) throws JSONException {
        List<ReportItem> outerValues = new ArrayList();
        //汇总
        if (dataJSON.getJSONObject("data").has("s")) {
            if (widget.getViewDimensions().length > 0) {
                JSONArray array = dataJSON.getJSONObject("data").getJSONArray("s");
                for (int i = 0; i < array.length(); i++) {
                    String tId = widget.getViewTargets()[i].getId();
                    ReportItem itemTmp = new ReportItem();
                    itemTmp.setdId(tId);
                    itemTmp.setText(array.getString(i));
                    itemTmp.setStyle("BI.SummaryTableHelper.getLastSummaryStyles(self.themeColor, self.tableStyle)");
                    outerValues.add(itemTmp);
                }
            }
            itemMap.put("values", outerValues);
        } else {
            //使用第一个值作为一个维度
            if (dataJSON.getJSONArray("s").length() == 0) {
                return itemMap;
            }
            if (widget.getViewDimensions().length > 0) {
                JSONArray array = dataJSON.getJSONObject("data").getJSONArray("s");
                for (int i = 0; i < array.length(); i++) {
                    String tId = widget.getViewTargets()[i].getId();
                    ReportItem itemTmp = new ReportItem();
                    itemTmp.setdId(tId);
                    itemTmp.setText(array.getString(i));
                    itemTmp.setStyle("BI.SummaryTableHelper.getSummaryStyles(self.themeColor, self.tableStyle)");
                    outerValues.add(itemTmp);
                }
                ReportItem itemSum = new ReportItem();
                itemSum.setType("bi.target_body_normal_cell");
                itemSum.setType(dataJSON.getJSONObject("data").getJSONArray("s").getString(0));
                itemSum.setSum(true);
                itemSum.setValue(outerValues);
                itemSum.setStyle("BI.SummaryTableHelper.getSummaryStyles(self.themeColor, self.tableStyle)");
                ((List<ReportItem>) itemMap.get("children")).add(itemSum);
                itemMap.put("values", itemMap);
            }
        }
        return itemMap;
    }

    private List<ReportItem> createCommonTableItems(JSONArray cArray, int curentLayer, ReportNode parent, ReportNodeTree nodeTree) throws JSONException {
        BIDimension[] viewDimensions = widget.getViewDimensions();
        BISummaryTarget[] viewTargets = widget.getTargets();
        curentLayer++;
        List<ReportItem> items = new ArrayList<ReportItem>();
        for (int i = 0; i < cArray.length(); i++) {
            getItems(cArray, curentLayer, parent, nodeTree, viewDimensions, viewTargets, items, i);
        }
        return items;

    }

    private void getItems(JSONArray cArray, int curentLayer, ReportNode parent, ReportNodeTree nodeTree, BIDimension[] viewDimensions, BISummaryTarget[] viewTargets, List<ReportItem> items, int i) throws JSONException {
        JSONObject child = cArray.getJSONObject(i);
        ReportNode node = new ReportNode();
        String cId = BIStringUtils.isEmptyString(child.getString("n")) ? UUID.randomUUID().toString() : child.getString("n");
        String nodeId = null != parent ? parent.getId() + cId : cId;
        node.setId(nodeId);
        String currDid = viewDimensions[curentLayer - 1].getId();
        String currentValue = child.getString("n");
        node.setName(currentValue);
        node.setdId(currDid);
        nodeTree.addNode(parent, node);
        List<String> pValues = new ArrayList();
        int tempLayer = curentLayer;
        String tempNodeId = node.getId();
        while (tempLayer > 0) {
            ReportNode itemNode = nodeTree.getNode(tempNodeId);
            JSONObject json = new JSONObject();
            json.put("value", itemNode.getName()).put("dId", viewDimensions[tempLayer - 1]);
            pValues.add(json.toString());
            tempNodeId = itemNode.getParent().getId();
            tempLayer--;
        }
        ReportItem item = new ReportItem();
        item.setdId(currDid);
        item.setText(currentValue);
        item.setNeedExpand(curentLayer < viewDimensions.length);
        item.setStyle("");
        item.setType("bi.normal_expander_cell");
        if (child.has("c")) {
            List<ReportItem> c = createCommonTableItems(child.getJSONArray("c"), curentLayer, node, nodeTree);
            item.setChildren(c);
        }
        if (child.has("s")) {
            List<ReportItem> values = new ArrayList<ReportItem>();
            //todo
//            boolean isCross = child.getJSONArray("s").length() == 0;
//            isCross = false;
//            if (isCross) {
//            } else {
                JSONArray childs = child.getJSONArray("s");
                for (int j = 0; j < childs.length(); j++) {
                    addTarItem(viewTargets[j], pValues, values, childs.getString(j));
                }
//            }
            item.setValue(values);
        }
        items.add(item);
    }

    private void addTarItem(BISummaryTarget viewTarget, List<String> pValues, List<ReportItem> values, String string) throws JSONException {
        ReportItem tartItem = new ReportItem();
        tartItem.setText(string);
        tartItem.setdId(viewTarget.getId());
        tartItem.setClicked(pValues);
        values.add(tartItem);
    }

    //todo
    private List<ReportItem> createCrossItems() throws JSONException {
        JSONObject top = dataJSON.getJSONObject("t");
        JSONObject left = dataJSON.getJSONObject("l");
//        if (left.has("s")){
//
//        }
        crossSum.put(0, new JSONArray());
        if (left.has("s")) {
            crossSum.put(0, crossSum.getJSONArray(0).put(true));
        }
        initCrossItemSum(0, left.getJSONArray("c"));
        JSONObject crossItem = new JSONObject();
        List children = createCrossPartItems(top.getJSONArray("c"), 0, new ReportNode(), new ReportNodeTree());
        return children;
    }


    private void initCrossItemSum(int currentLayer, JSONArray crossSum) throws JSONException {
        for (int i = 0; i < crossSum.length(); i++) {
            JSONArray array = crossSum.getJSONArray(i);
        }
    }

    /**
     * 交叉表——crossItems
     */
    private List<ReportItem> createCrossPartItems(JSONArray c, int currentLayer, ReportNode parent, ReportNodeTree crossTree) throws JSONException {
        List items = new ArrayList();
//        JSONArray crossItems = new JSONArray();
        currentLayer++;
        for (int j = 0; j < c.length(); j++) {
            JSONObject child = c.getJSONObject(j);
            if (returnNullItems(child)) {
                return items;
            }
            String cId = child.has("n") ? UUID.randomUUID().toString() : child.getString("n");
            String currDid = widget.getDimensions()[currentLayer - 1].getText();
            String currValue = child.getString("n");
            String nodeId = null != parent ? parent.getId() + cId : cId;
            ReportNode node = new ReportNode(nodeId);
            node.setName(child.getString("n"));
            node.setdId(currDid);
            crossTree.addNode(parent, node);
            List pValues = new ArrayList();
            int tempLayer = currentLayer;
            String tempNodeId = nodeId;
            while (tempLayer > 0) {
                String dId = widget.getDimensions()[tempLayer - 1].getId();
                ReportNode itemNode = crossTree.getNode(tempNodeId);
                JSONObject json = new JSONObject();
                json.put("value", itemNode.getName() + dId);
                json.put("dId", widget.getDimensions()[tempLayer - 1]);
                pValues.add(json.toString());
                tempNodeId = itemNode.getParent().getId();
                tempLayer--;
            }
            ReportItem item = new ReportItem();
            item.setText("bi.normal_expander_cell");
            item.setText(currValue);
            item.setdId(currDid);
            item.setCross(true);
            if (currentLayer < widget.getDimensions().length) {
                item.setNeedExpand(true);
                item.setExpanded(false);
            }
            if (child.has("c")) {
                List children = createCrossPartItems(child.getJSONArray("c"), currentLayer, node, crossTree);
                if (children.size() > 0) {
                    item.setChildren(children);
                    item.setExpanded(true);
                }
                boolean hasSum = false;
            }
        }
        return items;
    }

    private boolean returnNullItems(JSONObject child) throws JSONException {
        boolean flag = false;
        for (BIDimension biDimension : widget.getViewDimensions()) {
            if (biDimension.getValue().equals(child.getString("n"))) {
                flag = true;
                break;
            }
        }
        for (BISummaryTarget target : widget.getViewTargets()) {
            if (target.getValue().equals(child.getString("n"))) {
                flag = true;
                break;
            }
        }
        if (child.has("c") && flag) {
            return true;
        }
        return false;
    }

}
