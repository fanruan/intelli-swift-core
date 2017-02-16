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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        List<ReportItem> items = createCommonTableItems(dataJSON.getJSONObject("data").getJSONArray("c"), 0, null, new ReportNodeTree());
        boolean isCross = dataJSON.getJSONObject("data").has("t");
        if (!isCross) {
            tableDataForExport = new TableDataForExport(items, headers, null, null);
        } else {
            List<ReportTableHeader> crossHeaders = buildCrossHeaders();
            items = createCommonTableItems();
            List<ReportItem> crossItems = createCrossItems();
            tableDataForExport = new TableDataForExport(items, headers, crossItems, crossHeaders);
        }
        return tableDataForExport;
    }


    private List<ReportTableHeader> buildHeaders() {
        List<ReportTableHeader> headers = new ArrayList<>();
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
        List<ReportTableHeader> headers = new ArrayList<>();
        return headers;
    }

    private List<ReportItem> createCommonTableItems() throws JSONException {
        List<ReportItem> items = createCommonTableItems(dataJSON.getJSONObject("data").getJSONArray("c"), 0, null, new ReportNodeTree());
        return items;
    }

    private List<ReportItem> createCommonTableItems(JSONArray cArray, int curentLayer, ReportNode parent, ReportNodeTree nodeTree) throws JSONException {
        BIDimension[] viewDimensions = widget.getViewDimensions();
        BISummaryTarget[] viewTargets = widget.getTargets();
        curentLayer++;
        List<ReportItem> items = new ArrayList<>();
        for (int i = 0; i < cArray.length(); i++) {
            ReportNode node = new ReportNode();
            JSONObject child = cArray.getJSONObject(i);
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
                json.put("value", itemNode.getName());
                json.put("dId", viewDimensions[tempLayer - 1]);
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
                List<ReportItem> values = new ArrayList<>();
                //todo
                boolean isCross = child.getJSONArray("s").length() == 0;
                isCross = false;
                if (isCross) {
                } else {
                    JSONArray childs = child.getJSONArray("s");
                    for (int j = 0; j < childs.length(); j++) {
                        ReportItem tartItem = new ReportItem();
                        tartItem.setText(childs.getString(j));
                        tartItem.setdId(viewTargets[j].getId());
                        tartItem.setClicked(pValues);
                        values.add(tartItem);
                    }
                }
                item.setValue(values);
            }
            items.add(item);
        }
        return items;

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
        JSONArray crossItems = new JSONArray();
        currentLayer++;
        for (int j = 0; j < c.length(); j++) {
            JSONObject child = c.getJSONObject(j);
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

}
