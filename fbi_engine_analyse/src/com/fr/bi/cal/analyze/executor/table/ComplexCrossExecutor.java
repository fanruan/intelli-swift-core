package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.executor.detail.DetailCellIterator;
import com.fr.bi.cal.analyze.executor.detail.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBBoxElement;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.style.TargetStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.conf.report.widget.field.target.BITarget;
import com.fr.bi.field.BITargetAndDimensionUtils;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.constant.CellConstant;
import com.fr.bi.stable.report.key.TargetGettingKey;
import com.fr.bi.stable.report.result.TargetCalculator;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.fr.general.ComparatorUtils;
import com.fr.general.DateUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;
import com.fr.stable.StringUtils;

import java.util.*;

/**
 * Created by sheldon on 14-9-2.
 */
public class ComplexCrossExecutor extends BIComplexExecutor<NewCrossRoot> {

    private BIComplexExecutData rowData;
    private BIComplexExecutData columnData;

    public ComplexCrossExecutor(TableWidget widget, Paging page,
                                ArrayList<ArrayList<String>> rowArray, ArrayList<ArrayList<String>> columnArray,
                                BISession session, ComplexExpander expander) {

        super(widget, page, session, expander);
        rowData = new BIComplexExecutData(rowArray, widget.getDimensions());
        columnData = new BIComplexExecutData(columnArray, widget.getDimensions());
    }

    @Override
    public DetailCellIterator createCellIterator4Excel() throws Exception {
        final Map<Integer, NewCrossRoot[]> nodesMap = getCubeCrossNodes();
        if (nodesMap.isEmpty() || nodesMap == null) {
            return new DetailCellIterator(0, 0);
        }
        int[] lens = calculateRowAndColumnLen(nodesMap);
        final DetailCellIterator iter = new DetailCellIterator(lens[0], lens[1]);
        new Thread() {
            public void run() {
                try {
                    FinalInt rowIdx = new FinalInt();
                    FinalInt start = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    int order = 0;
                    Iterator<Map.Entry<Integer, NewCrossRoot[]>> iterator = nodesMap.entrySet().iterator();
                    int rowDataIdx = 0;
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, NewCrossRoot[]> entry = iterator.next();
                        NewCrossRoot[] roots = entry.getValue();
                        //生成标题
                        if(rowDataIdx == 0) {
                            CrossExecutor.generateTitle(roots, widget, columnData.getDimensionArray(0),
                                    rowData.getDimensionArray(0), usedSumTarget, pagedIterator, rowIdx);
                            rowIdx.value++;
                        }
                        CrossExecutor.generateCells(roots, widget, rowData.getDimensionArray(rowDataIdx),
                                rowData.getMaxArrayLength(), iter, start, rowIdx, order);
                        if(rowDataIdx > 0) {
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

    private int[] calculateRowAndColumnLen(Map<Integer, NewCrossRoot[]> nodesMap) {
        int len = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[len];
        for (int i = 0; i < len; i++) {
            keys[i] = new TargetGettingKey(usedSumTarget[i].createSummaryCalculator().createTargetKey(), usedSumTarget[i].getValue());
        }
        boolean hasTarget = keys.length != 0;
        ArrayList<NewCrossRoot> nodes = new ArrayList<NewCrossRoot>();
        Iterator<Map.Entry<Integer, NewCrossRoot[]>> iterator = nodesMap.entrySet().iterator();
        ArrayList<Integer> integers = new ArrayList<Integer>();
        while (iterator.hasNext()) {
            Map.Entry<Integer, NewCrossRoot[]> entry = iterator.next();
            NewCrossRoot[] roots = entry.getValue();
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

    /**
     * 获取nodes 复杂列表获取的是一个nodes
     *
     * @return 获取的nodes
     */
    @Override
    public Map<Integer, NewCrossRoot> getCubeNodes() throws InterruptedException {
        return null;
    }

    /**
     * 获取nodes的个数
     *
     * @param nodes
     * @return
     */
    @Override
    public int getNodesTotalLength(Node[] nodes) {
        return 0;
    }

    /**
     * 获取node的个数
     *
     * @param nodes
     * @param complexExpander
     * @param ints
     */
    @Override
    public int getNodesTotalLength(Node[] nodes, ComplexExpander complexExpander, Integer[] ints) {
        return 0;
    }


    /**
     * 获取node
     *
     * @return 获取的node
     */
    @Override
    public NewCrossRoot getCubeNode() {
        return null;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        Iterator<Map.Entry<Integer, NewCrossRoot[]>> it = getCubeCrossNodes().entrySet().iterator();
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<Integer, NewCrossRoot[]> entry = it.next();
            JSONArray ja = new JSONArray();
            for (int i = 0; i < entry.getValue().length; i++) {
                ja.put(entry.getValue()[i].toJSONObject(rowData.getDimensionArray(entry.getKey()), columnData.getDimensionArray(i), widget.getTargetsKey()));
            }
            jo.put(String.valueOf(entry.getKey()), ja);
        }
        return jo;
    }

    /**
     * 注释
     *
     * @return 注释
     */
    @Override
    public CBCell[][] createCellElement() throws Exception {
        return new CBCell[0][0];
    }

    private int getTotalNodeRowLength(ArrayList<NewCrossRoot> roots, boolean hasTarget, ArrayList<Integer> integers) {
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

    private int getTotalNodeColumnLength(ArrayList<NewCrossRoot> roots, boolean hasTarget) {
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


    private BISummaryTarget[] createTarget4Calculate() {
        ArrayList<BITarget> list = new ArrayList<BITarget>();
        for (int i = 0; i < usedSumTarget.length; i++) {
            list.add(usedSumTarget[i]);
        }
        if (widget.getTargetSort() != null) {
            String name = widget.getTargetSort().getName();
            boolean inUsedSumTarget = false;
            for (int i = 0; i < usedSumTarget.length; i++) {
                if (ComparatorUtils.equals(usedSumTarget[i].getValue(), name)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (int i = 0; i < allSumTarget.length; i++) {
                    if (ComparatorUtils.equals(allSumTarget[i].getValue(), name)) {
                        list.add(allSumTarget[i]);
                    }
                }
            }
        }
        Iterator<String> it1 = widget.getTargetFilterMap().keySet().iterator();
        while (it1.hasNext()) {
            String key = it1.next();
            boolean inUsedSumTarget = false;
            for (int i = 0; i < usedSumTarget.length; i++) {
                if (ComparatorUtils.equals(usedSumTarget[i].getValue(), key)) {
                    inUsedSumTarget = true;
                }
            }
            if (!inUsedSumTarget) {
                for (int i = 0; i < allSumTarget.length; i++) {
                    if (ComparatorUtils.equals(allSumTarget[i].getValue(), key)) {
                        list.add(allSumTarget[i]);
                    }
                }
            }

        }
        return list.toArray(new BISummaryTarget[list.size()]);
    }

    /* (non-Javadoc)
     * @see com.fr.bi.cube.engine.report.summary.BIEngineExecutor#getCubeNode()
     */
    private Map<Integer, NewCrossRoot[]> getCubeCrossNodes() throws Exception {
        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int usedSumLen = usedSumTarget.length;
        Map<String, TargetCalculator> targetsMap = new HashMap<String, TargetCalculator>();
        for (int i = 0; i < usedSumLen; i++) {
            targetsMap.put(usedSumTarget[i].getValue(), usedSumTarget[i].createSummaryCalculator());
        }

        Map<Integer, NewCrossRoot[]> nodeMap = getCubeCrossNodesFromProvide(targetsMap);

        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return nodeMap;
    }

    //通过交叉表的provide，创建nodeMap
    private Map<Integer, NewCrossRoot[]> getCubeCrossNodesFromProvide(Map<String, TargetCalculator> targetsMap) throws Exception {
        BISummaryTarget[] usedTarget = createTarget4Calculate();
        Map<Integer, NewCrossRoot[]> nodeMap = new HashMap<Integer, NewCrossRoot[]>();
        int columnRegionLen = columnData.getDimensionArrayLength();

        for (int i = 0; i < rowData.getRegionIndex(); i++) {

            BIDimension[] rowDimension = rowData.getDimensionArray(i);
            NewCrossRoot[] nodes = new NewCrossRoot[columnRegionLen];

            for (int j = 0; j < columnRegionLen; j++) {

                CrossExpander expander = complexExpander.createCrossNode(i, j);
                BIDimension[] colDimension = columnData.getDimensionArray(j);
                NewCrossRoot node = null;
                node = CubeIndexLoader.getInstance(session.getUserId()).loadPageCrossGroup(usedTarget, rowDimension, colDimension, allSumTarget, -1, widget.useRealData(), session, expander, widget);
                node = node.createResultFilterNodeWithTopValue(rowDimension, colDimension, targetsMap);
                nodes[j] = node;
            }
            nodeMap.put(i, nodes);
        }
        return nodeMap;
    }
}