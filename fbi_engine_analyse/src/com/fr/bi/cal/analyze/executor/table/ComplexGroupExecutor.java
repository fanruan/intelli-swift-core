package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BIComplexGroupResult;
import com.fr.bi.cal.analyze.cal.result.ComplexGroupResult;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexGroupExecutor extends AbstractTableWidgetExecutor {

    protected BIComplexExecutData rowData;

    protected ComplexExpander complexExpander;

    public ComplexGroupExecutor(TableWidget widget, Paging page,
                                ArrayList<ArrayList<String>> rowArray,
                                BISession session, ComplexExpander expander) {

        super(widget, page, session);
        rowData = new BIComplexExecutData(rowArray, widget.getDimensions());
        this.complexExpander = expander;
    }

    @Override
    public TableCellIterator createCellIterator4Excel() throws Exception {

        Map<Integer, Node> nodeMap = getCubeNodes();
        if (nodeMap == null) {
            return new TableCellIterator(0, 0);
        }

        int collen = rowData.getMaxArrayLength();
        int columnLen = collen + usedSumTarget.length;

        Iterator<Map.Entry<Integer, Node>> iterator = nodeMap.entrySet().iterator();
        final Node[] nodes = new Node[nodeMap.size()];
        Integer[] integers = new Integer[nodeMap.size()];
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Node> entry = iterator.next();
            nodes[i] = entry.getValue();
            integers[i] = entry.getKey();
            i++;
        }

        int rowLen = getNodesTotalLength(nodes);
        final TableCellIterator iter = new TableCellIterator(columnLen, rowLen);

        new Thread() {

            public void run() {

                try {
                    FinalInt start = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    GroupExecutor.generateHeader(widget, rowData.getDimensionArray(0), usedSumTarget, pagedIterator, rowData.getMaxArrayLength());
                    FinalInt rowIdx = new FinalInt();
                    for (int i = 0, j = nodes.length; i < j; i++) {
                        GroupExecutor.generateCells(nodes[i], widget, rowData.getDimensionArray(i), iter, start, rowIdx, rowData.getMaxArrayLength());
                    }
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();

        return iter;
    }

    @Override
    public Object getCubeNode() throws Exception {

        return null;
    }

    /**
     * 获取nodes的个数
     *
     * @param nodes
     * @return
     */
    public int getNodesTotalLength(Node[] nodes) {

        int count = 0;

        for (int i = 0; i < nodes.length; i++) {
            count += nodes[i].getTotalLengthWithSummary();
        }

        return count;
    }

    /**
     * 获取某个nodes
     *
     * @return
     */
    public Map<Integer, Node> getCubeNodes() throws Exception {

        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int i = 0; i < summaryLength; i++) {
            keys[i] = usedSumTarget[i].createTargetGettingKey();
        }
        Map<Integer, Node> nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(false, widget, createTarget4Calculate(), rowData, allDimensions,
                                                                                                           allSumTarget, keys, paging.getOperator(), widget.useRealData(), session, complexExpander, true);
        if (nodeMap.isEmpty()) {
            return null;
        }

        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {

        Iterator<Map.Entry<Integer, Node>> it = getCubeNodes().entrySet().iterator();
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<Integer, Node> entry = it.next();
            jo.put(String.valueOf(entry.getKey()), entry.getValue().toJSONObject(rowData.getDimensionArray(entry.getKey()), widget.getTargetsKey(), -1));
        }
        return jo;
    }

    @Override
    public GroupValueIndex getClickGvi(Map clicked, BusinessTable targetKey) {

        GroupValueIndex filterGvi = null;
        try {
            if (clicked != null) {
                String target = null;
                BISummaryTarget biSummaryTarget = null;
                for (String t : ((Map<String, JSONArray>) clicked).keySet()) {
                    try {
                        biSummaryTarget = widget.getBITargetByID(t);
                        target = t;
                        break;
                    } catch (Exception e) {

                    }
                }
                if (biSummaryTarget == null || !biSummaryTarget.createTableKey().equals(targetKey)) {
                    return null;
                }
                JSONArray c = ((Map<String, JSONArray>) clicked).get(target);
                if (c == null) {
                    return null;
                }
                List<String> dids = new ArrayList<String>();
                List<Object> cs = new ArrayList<Object>();
                for (int i = c.length() - 1; i >= 0; i--) {
                    JSONObject object = c.getJSONObject(i);
                    Object clickValue = object.getJSONArray("value").getString(0);
                    String did = object.getString("dId");
                    cs.add(clickValue);
                    dids.add(did);
                }
                for (int i = 0; i < rowData.getRegionIndex(); i++) {
                    BIDimension[] dimensions = rowData.getDimensionArray(i);
                    if (isClickRegion(dids, dimensions)) {
                        Node n = getStopOnRowNode(cs.toArray(), dimensions);
                        if (n != null) {
                            filterGvi = GVIUtils.AND(filterGvi, getLinkNodeFilter(n, target, cs));
                        }
                        return filterGvi;
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(ComplexGroupExecutor.class).info("error in get link filter", e);
        }
        return filterGvi;
    }

    public BIComplexGroupResult getResult() throws Exception {

        return new ComplexGroupResult(getCubeNodes());
    }
}