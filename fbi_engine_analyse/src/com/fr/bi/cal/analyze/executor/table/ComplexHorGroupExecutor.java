package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIComplexExecutData;
import com.fr.bi.cal.analyze.cal.result.ComplexExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.BITableStyle;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
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
    public TableCellIterator createCellIterator4Excel() throws Exception {

        Map<Integer, Node> nodeMap = getCubeNodes();
        if (nodeMap == null || nodeMap.isEmpty()) {
            return new TableCellIterator(0, 0);
        }

        Iterator<Map.Entry<Integer, Node>> iterator = nodeMap.entrySet().iterator();
        final Node[] trees = new Node[nodeMap.size()];
        int count = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Node> entry = iterator.next();
            trees[count++] = entry.getValue();
        }
        int rowLen = usedSumTarget.length + rowData.getMaxArrayLength();
        int columnLen = getNodesTotalLength(trees);

        final TableCellIterator iter = new TableCellIterator(rowLen, columnLen);
        new Thread() {

            public void run() {

                try {
                    //水平分组表行数随指标的行数增加而增加，故用第一页就行了
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(0);
                    generateTitle(trees, widget, rowData, pagedIterator);
                    generateCells(trees, widget, rowData, usedSumTarget, pagedIterator);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    private void generateTitle(Node[] nodes, TableWidget widget, BIComplexExecutData rowData, StreamPagedIterator pagedIterator) {

        int maxRowDimLen = rowData.getMaxArrayLength();
//        if (widget.isOrder() == 1) {
//            CBCell cell = ExecutorUtils.createTitleCell(Inter.getLocText("BI-Number_Index"), 0, maxRowDimLen, 0, 1);
//            pagedIterator.addCell(cell);
//        }
        int colDimIdx = 0;
        int firstColumnDimLen = rowData.getDimensionArray(0).length;
        //一行一行的生成单元格，按列表头区域维度最多的个数来确定行数
        while (colDimIdx < maxRowDimLen) {
            FinalInt columnIdx = new FinalInt();
//            columnIdx.value += widget.isOrder();
            //列表头中第一个维度的维度名显示在行的开头
            if (firstColumnDimLen > colDimIdx) {
                int rowSpan = firstColumnDimLen - 1 == colDimIdx ? maxRowDimLen - colDimIdx : 1;
                CBCell cell = ExecutorUtils.createTitleCell(rowData.getDimensionArray(0)[colDimIdx].getText(), colDimIdx,
                                                       rowSpan, columnIdx.value, 1);
                pagedIterator.addCell(cell);
            }
            columnIdx.value++;
            generateDimCell(nodes, pagedIterator, colDimIdx, columnIdx, maxRowDimLen);
            colDimIdx++;
        }
    }

    private void generateDimCell(Node[] nodes, StreamPagedIterator pagedIterator, int colDimIdx, FinalInt columnIdx, int maxRowDimLen) {

        for (int i = 0, j = rowData.getDimensionArrayLength(); i < j; i++) {
            BIDimension[] dimensions = rowData.getDimensionArray(i);
            //列表头不同区域中的维度个数可能不同
            if (dimensions.length > colDimIdx) {
                nodes[i] = nodes[i].getFirstChild();
                Node temp = nodes[i];
                BIDimension dim = dimensions[colDimIdx];
                while (temp != null) {
                    Object data = temp.getData();
                    Object v = dim.getValueByType(data);
                    if (dim.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(v.toString()) != null) {
                        v = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(v.toString()).longValue()));
                    }
                    int rowSpan = (dimensions.length == colDimIdx + 1) ? (maxRowDimLen - colDimIdx) : temp.getDeep();
                    CBCell dimCell = ExecutorUtils.createTitleCell(v, colDimIdx, rowSpan, columnIdx.value, temp.getTotalLengthWithSummary());
                    pagedIterator.addCell(dimCell);
                    columnIdx.value += temp.getTotalLengthWithSummary();
                    HorGroupExecutor.generateTitleSumCells(temp, pagedIterator, colDimIdx, columnIdx, rowSpan);
                    temp = temp.getSibling();
                }
            } else {
                columnIdx.value += nodes[i].getParent().getTotalLengthWithSummary();
            }
        }
    }

    private void generateCells(Node[] nodes, TableWidget widget, BIComplexExecutData rowData, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator) {

        int rowIdx = rowData.getMaxArrayLength();
        TargetGettingKey[] keys = widget.getTargetsKey();
        for (int i = 0, j = nodes.length; i < j; i++) {
            while (nodes[i].getFirstChild() != null) {
                nodes[i] = nodes[i].getFirstChild();
            }
        }

        for (int i = 0; i < usedSumTarget.length; i++) {
            FinalInt columnIdx = new FinalInt();
//            columnIdx.value += widget.isOrder() + 1;
            columnIdx.value ++;
            Style headStyle = BITableStyle.getInstance().getDimensionCellStyle(false, (i + 1) % 2 == 1);
//            if (widget.isOrder() == 1) {
//                CBCell orderCell = ExecutorUtils.createValueCell(i + 1, rowIdx + i, 1, 0, 1, headStyle, rowIdx + i % 2 == 1);
//                pagedIterator.addCell(orderCell);
//            }
            Object targetName = usedSumTarget[i].getText();
            CBCell targetNameCell = ExecutorUtils.createValueCell(targetName, rowIdx + i, 1, 0, 1, headStyle, rowIdx + i % 2 == 1);
            pagedIterator.addCell(targetNameCell);
            for (int k = 0, j = nodes.length; k < j; k++) {
                Node temp = nodes[k];
                while (temp != null) {
                    Object data = temp.getSummaryValue(keys[i]);
                    boolean isPercent = widget.getChartSetting().getNumberLevelByTargetId(keys[i].getTargetName()) == BIReportConstant.TARGET_STYLE.NUM_LEVEL.PERCENT;
                    Style style = BITableStyle.getInstance().getNumberCellStyle(data, (i + 1) % 2 == 1, isPercent);
                    CBCell cell = ExecutorUtils.createValueCell(data, rowIdx + i, 1, columnIdx.value++, 1, style, rowIdx + i % 2 == 1);
                    pagedIterator.addCell(cell);
                    HorGroupExecutor.generateTargetSumCell(temp, widget, keys[i], pagedIterator, rowIdx, columnIdx, i);
                    temp = temp.getSibling();
                }
            }
        }
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

    @Override
    public Rectangle getSouthEastRectangle() {

        return super.getSouthEastRectangle();
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
        Map<Integer, Node> nodeMap = CubeIndexLoader.getInstance(session.getUserId()).loadComplexPageGroup(true, widget, createTarget4Calculate(), rowData, allDimensions,
                                                                                                           allSumTarget, keys, paging.getOperator(), widget.useRealData(), session, complexExpander, false);


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
                java.util.List<String> dids = new ArrayList<String>();
                java.util.List<Object> cs = new ArrayList<Object>();
                for (int i = 0; i < c.length(); i++) {
                    JSONObject object = c.getJSONObject(i);
                    cs.add(object.getJSONArray("value").getString(0));
                    dids.add(object.getString("dId"));
                }
                for (int i = 0; i < rowData.getRegionIndex(); i++) {
                    BIDimension[] dimensions = rowData.getDimensionArray(i);
                    if (isClieckRegion(dids, dimensions)) {
                        Node n = getStopOnRowNode(cs.toArray(), dimensions);
                        filterGvi = GVIUtils.AND(filterGvi, getLinkNodeFilter(n, target, cs));
                        return filterGvi;
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger(ComplexHorGroupExecutor.class).info("error in get link filter",e);
        }
        return filterGvi;
    }
}