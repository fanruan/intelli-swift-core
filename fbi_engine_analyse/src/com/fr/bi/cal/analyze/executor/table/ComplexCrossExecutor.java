package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexCrossExecutor extends AbstractTableWidgetExecutor<XNode> {

    private BIComplexExecutData rowData;

    private BIComplexExecutData columnData;

    private ComplexExpander complexExpander;

    public ComplexCrossExecutor(TableWidget widget, Paging page,
                                ArrayList<ArrayList<String>> rowArray, ArrayList<ArrayList<String>> columnArray,
                                BISession session, ComplexExpander expander) {

        super(widget, page, session);
        rowData = new BIComplexExecutData(rowArray, widget.getDimensions());
        columnData = new BIComplexExecutData(columnArray, widget.getDimensions());
        this.complexExpander = expander;
    }

    @Override
    public TableCellIterator createCellIterator4Excel() throws Exception {

        final Map<Integer, XNode[]> nodesMap = getCubeCrossNodes();
        if (nodesMap.isEmpty() || nodesMap == null) {
            return new TableCellIterator(0, 0);
        }
        int[] lens = calculateRowAndColumnLen(nodesMap);
        final TableCellIterator iter = new TableCellIterator(lens[0], lens[1]);
        new Thread() {

            public void run() {

                try {
                    FinalInt rowIdx = new FinalInt();
                    FinalInt start = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    int order = 0;
                    Iterator<Map.Entry<Integer, XNode[]>> iterator = nodesMap.entrySet().iterator();
                    int rowDataIdx = 0;
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, XNode[]> entry = iterator.next();
                        XNode[] roots = entry.getValue();
                        //生成标题
                        if (rowDataIdx == 0) {
                            CrossExecutor.generateTitle(roots, widget, columnData.getDimensionArray(0),
                                    rowData.getDimensionArray(0), usedSumTarget, pagedIterator, rowIdx);
                            rowIdx.value++;
                        }
                        CrossExecutor.generateCells(roots, widget, rowData.getDimensionArray(rowDataIdx),
                                rowData.getMaxArrayLength(), iter, start, rowIdx, order);
                        if (rowDataIdx > 0) {
                            order += nodesMap.get(rowDataIdx - 1)[0].getLeft().getChildLength();
                        }
                        rowDataIdx++;
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

    private int[] calculateRowAndColumnLen(Map<Integer, XNode[]> nodesMap) {

        int len = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[len];
        for (int i = 0; i < len; i++) {
            keys[i] = usedSumTarget[i].createTargetGettingKey();
        }
        boolean hasTarget = keys.length != 0;
        ArrayList<XNode> nodes = new ArrayList<XNode>();
        Iterator<Map.Entry<Integer, XNode[]>> iterator = nodesMap.entrySet().iterator();
        ArrayList<Integer> integers = new ArrayList<Integer>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, XNode[]> entry = iterator.next();
            XNode[] roots = entry.getValue();
            integers.add(entry.getKey());
            for (int i = 0; i < roots.length; i++) {
                nodes.add(roots[i]);
            }
        }
        int[] lens = new int[2];

        lens[0] = getTotalNodeColumnLength(nodes, hasTarget) * (Math.max(1, keys.length)) + rowData.getMaxArrayLength();
        lens[1] = getTotalNodeRowLength(nodes, hasTarget, integers) + columnData.getMaxArrayLength() + 1;
        return lens;
    }

    private int getTotalNodeRowLength(ArrayList<XNode> roots, boolean hasTarget, ArrayList<Integer> integers) {

        int count = 0;
        int columnRegionLength = this.columnData.getDimensionArrayLength();

        for (int i = 0; i < integers.size(); i++) {
            if (hasTarget) {

                count += roots.get(i * columnRegionLength).getLeft().getTotalLengthWithSummary();
            } else {

                count += roots.get(i * columnRegionLength).getLeft().getTotalLength();
            }
        }

        return count;

    }

    private int getTotalNodeColumnLength(ArrayList<XNode> roots, boolean hasTarget) {

        int count = 0;
        int columnRegionLength = this.columnData.getDimensionArrayLength();

        for (int i = 0; i < columnRegionLength; i++) {
            if (hasTarget) {

                count += roots.get(i).getTop().getTotalLengthWithSummary();
            } else {

                count += roots.get(i).getTop().getTotalLength();
            }
        }

        return count;

    }

    @Override
    public XNode getCubeNode() throws Exception {

        return null;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {

        Iterator<Map.Entry<Integer, XNode[]>> it = getCubeCrossNodes().entrySet().iterator();
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<Integer, XNode[]> entry = it.next();
            JSONArray ja = new JSONArray();
            for (int i = 0; i < entry.getValue().length; i++) {
                ja.put(entry.getValue()[i].toJSONObject(rowData.getDimensionArray(entry.getKey()), columnData.getDimensionArray(i), widget.getTargetsKey(), widget.showColumnTotal()));
            }
            jo.put(String.valueOf(entry.getKey()), ja);
        }
        return jo;
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    private Map<Integer, XNode[]> getCubeCrossNodes() throws Exception {

        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int usedSumLen = usedSumTarget.length;
        Map<String, TargetCalculator> targetsMap = new HashMap<String, TargetCalculator>();
        for (int i = 0; i < usedSumLen; i++) {
            targetsMap.put(usedSumTarget[i].getValue(), usedSumTarget[i].createSummaryCalculator());
        }

        Map<Integer, XNode[]> nodeMap = getCubeCrossNodesFromProvide(targetsMap);

        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
    }

    //通过交叉表的provide，创建nodeMap
    private Map<Integer, XNode[]> getCubeCrossNodesFromProvide(Map<String, TargetCalculator> targetsMap) throws Exception {

        BISummaryTarget[] usedTarget = createTarget4Calculate();
        Map<Integer, XNode[]> nodeMap = new HashMap<Integer, XNode[]>();
        int columnRegionLen = columnData.getDimensionArrayLength();

        for (int i = 0; i < rowData.getRegionIndex(); i++) {

            BIDimension[] rowDimension = rowData.getDimensionArray(i);
            XNode[] nodes = new XNode[columnRegionLen];

            for (int j = 0; j < columnRegionLen; j++) {

                CrossExpander expander = complexExpander.createCrossNode(i, j);
                BIDimension[] colDimension = columnData.getDimensionArray(j);
                XNode node = CubeIndexLoader.getInstance(session.getUserId()).loadPageCrossGroup(usedTarget, rowDimension, colDimension, allSumTarget, -1, widget.useRealData(), session, expander, widget);
                nodes[j] = node;
            }
            nodeMap.put(i, nodes);
        }
        return nodeMap;
    }

    public GroupValueIndex getClieckGvi(Map<String, JSONArray> clicked, BusinessTable targetKey) {

        GroupValueIndex linkGvi = null;
        try {
            String target = getClieckTarget(clicked);
            // 连联动计算指标都没有就没有所谓的联动了,直接返回
            if (target == null) {
                return null;
            }
            BISummaryTarget summaryTarget = widget.getBITargetByID(target);
            BusinessTable linkTargetTable = summaryTarget.createTableKey();
            // 基础表相同才进行比较
            if (!targetKey.equals(linkTargetTable)) {
                return null;
            }
            // 区分哪些是行数据,哪些是列数据
            List<Object> row = new ArrayList<Object>();
            List<Object> col = new ArrayList<Object>();
            List<String> rowsId = new ArrayList<String>();
            List<String> colsId = new ArrayList<String>();
            getLinkRowAndColData(clicked, target, row, col, rowsId, colsId);
            int columnRegionLen = columnData.getDimensionArrayLength();
            for (int i = 0; i < rowData.getRegionIndex(); i++) {
                BIDimension[] rowDimension = rowData.getDimensionArray(i);
                for (int j = 0; j < columnRegionLen; j++) {
                    BIDimension[] colDimension = columnData.getDimensionArray(j);
                    if (isClieckRegin(rowsId, rowDimension, colsId, colDimension)) {
                        CubeIndexLoader cubeIndexLoader = CubeIndexLoader.getInstance(session.getUserId());
                        int calPage = paging.getOperator();
                        Node l = cubeIndexLoader.getStopWhenGetRowNode(row.toArray(), widget, createTarget4Calculate(), rowDimension,
                                allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
                        Node t = cubeIndexLoader.getStopWhenGetRowNode(col.toArray(), widget, createTarget4Calculate(), colDimension,
                                allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
                        linkGvi = GVIUtils.AND(getLinkNodeFilter(l, target, row), linkGvi);
                        linkGvi = GVIUtils.AND(getLinkNodeFilter(t, target, col), linkGvi);
                        return linkGvi;
                    }
                }
            }
            return linkGvi;
        } catch (Exception e) {
            BILoggerFactory.getLogger(ComplexCrossExecutor.class).info("error in get link filter", e);
        }
        return linkGvi;
    }
}