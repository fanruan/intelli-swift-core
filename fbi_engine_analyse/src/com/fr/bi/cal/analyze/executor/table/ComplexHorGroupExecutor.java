package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.ComplexGroupResult;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BIComplexGroupResult;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexHorGroupExecutor extends AbstractTableWidgetExecutor {

    protected BIComplexExecutData rowData;

    protected ComplexExpander complexExpander;

    public ComplexHorGroupExecutor(TableWidget widget, Paging page,
                                   ArrayList<ArrayList<String>> columnArray,
                                   BISession session, ComplexExpander expander) {

        super(widget, page, session);
        //这里的rowData实际上是列表头
        rowData = new BIComplexExecutData(columnArray, widget.getDimensions());
        this.complexExpander = expander;
    }

    @Override
    public Object getCubeNode() throws Exception {

        return null;
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    public Map<Integer, Node> getCubeNodes() throws Exception {

        if (getSession() == null) {
            return null;
        }
        if (rowData.getDimensionArrayLength() == 0) {
            return null;
        }
        long start = System.currentTimeMillis();
        Map<String, TargetGettingKey> targetsMap = new HashMap<String, TargetGettingKey>();
        int summaryLength = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[summaryLength];
        for (int s = 0; s < summaryLength; s++) {
            keys[s] = usedSumTarget[s].createTargetGettingKey();
        }
        Map<Integer, Node> nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(true, widget, createTarget4Calculate(), rowData, allDimensions, allSumTarget, keys, paging.getOperator(), widget.useRealData(), session, complexExpander, false);


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
                        BILoggerFactory.getLogger().error(e.getMessage(), e);
                    }
                }
                if (biSummaryTarget == null || !biSummaryTarget.createTableKey().equals(targetKey)) {
                    return null;
                }
                JSONArray c = ((Map<String, JSONArray>) clicked).get(target);
                if (c == null) {
                    return null;
                }
                java.util.List<String> dids = new ArrayList<String>();
                java.util.List<Object> cs = new ArrayList<Object>();
                for (int i = 0; i < c.length(); i++) {
                    JSONObject object = c.getJSONObject(i);
                    cs.add(object.getJSONArray("value").getString(0));
                    dids.add(object.getString("dId"));
                }
                for (int i = 0; i < rowData.getRegionIndex(); i++) {
                    BIDimension[] dimensions = rowData.getDimensionArray(i);
                    if (isClickRegion(dids, dimensions)) {
                        Node n = getStopOnRowNode(cs.toArray(), dimensions);
                        filterGvi = GVIUtils.AND(filterGvi, getLinkNodeFilter(n, target, cs));
                        return filterGvi;
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(ComplexHorGroupExecutor.class).info("error in get link filter", e);
        }
        return filterGvi;
    }

    public BIComplexGroupResult getResult() throws Exception {

        return new ComplexGroupResult(getCubeNodes());
    }
}