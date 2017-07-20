package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.*;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.TargetCalculator;
import com.fr.bi.stable.constant.BIReportConstant;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;
import com.fr.general.GeneralUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;

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
                        if (rowDataIdx == 0) {
                            generateTitle(roots, pagedIterator, rowIdx);
//                            generateTitle(roots, widget, columnData.getDimensionArray(0),
//                                    rowData.getDimensionArray(0), usedSumTarget, pagedIterator, rowIdx);
                            rowIdx.value++;
                        }
                        generateCells(roots, widget, rowData.getDimensionArray(rowDataIdx),
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

    //根据列表头的第一个区域创建 列表头标题 columnDate.getDimensionArray(0)
    private void generateTitle(XNode[] roots, StreamPagedIterator pagedIterator, FinalInt rowIdx) throws Exception {

        int rootsLen = roots.length;
        Node[] tops = new Node[rootsLen];
        for (int i = 0; i < rootsLen; i++) {
            tops[i] = roots[i].getTop();
        }

        BIDimension[] colDimensions = columnData.getDimensionArray(0);

        int colDimIdx = 0;
        while (colDimIdx < columnData.getMaxArrayLength()) {
            if (colDimIdx < colDimensions.length) {
                int rowSpan = colDimIdx == colDimensions.length - 1 ? columnData.getMaxArrayLength() - colDimIdx : 1;
                CBCell cell = ExecutorUtils.createTitleCell(colDimensions[colDimIdx].getText(), rowIdx.value, rowSpan, 0, rowData.getMaxArrayLength());
                pagedIterator.addCell(cell);
            }
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = rowDimension.length;
            for (int i = 0; i < rootsLen; i++) {
                tops[i] = tops[i].getFirstChild();
                //列表头
                getColDimensionsTitle(widget, usedSumTarget, pagedIterator, tops[i], rowIdx.value, columnIdx);
            }
            rowIdx.value++;
            colDimIdx++;
        }

        for (int i = 0; i < rowDimension.length; i++) {
            CBCell cell = ExecutorUtils.createTitleCell(rowDimension[i].getText(), rowIdx.value, 1, i, 1);
            pagedIterator.addCell(cell);
        }
        if (widget.getViewTargets().length > 1) {
            FinalInt targetsTitleColumnIdx = new FinalInt();
            targetsTitleColumnIdx.value = rowDimension.length;
            for (int i = 0; i < rootsLen; i++) {
                getTargetsTitle(widget, usedSumTarget, pagedIterator, tops[i], rowIdx.value, targetsTitleColumnIdx);
            }
        }
    }

    private void getColDimensionsTitle(TableWidget widget, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator,
                                       Node top, int rowIdx, FinalInt columnIdx) {

        int targetNum = widget.getViewTargets().length;

        Node temp = top;
        while (temp != null) {
            int columnSpan = widget.showColumnTotal() ? temp.getTotalLengthWithSummary() * targetNum : temp.getTotalLength() * targetNum;
            Object data = temp.getData();
            BIDimension[] dims = widget.getViewTopDimensions();
            BIDimension dim = dims[rowIdx];
            String v = dim.toString(dim.getValueByType(data));
            if (dim.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(v) != null) {
                v = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(v).longValue()));
            }
            int rowSpan = (rowIdx == dims.length - 1) ? (usedSumTarget.length == 1 ? 2 : 1) : 1;
            CBCell cell = ExecutorUtils.createTitleCell(v, rowIdx, rowSpan, columnIdx.value, columnSpan);
            pagedIterator.addCell(cell);
            columnIdx.value += columnSpan;
            if (widget.showColumnTotal()) {
                generateTitleSumCells(temp, widget, pagedIterator, rowIdx, columnIdx);
            }
            temp = temp.getSibling();
        }
    }

    private void generateTitleSumCells(Node temp, TableWidget widget, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp) && temp.getParent().getChildLength() != 1) {
            CBCell cell = ExecutorUtils.createTitleCell(Inter.getLocText("BI-Summary_Values"), rowIdx, temp.getDeep(), columnIdx.value, widget.getViewTargets().length);
            pagedIterator.addCell(cell);
        }
        adjustColumnIdx(temp, widget, columnIdx);
    }

    private boolean checkIfGenerateTitleSumCells(Node header) {
        //到根节点停止
        boolean isNotRoot = header.getParent() != null;
        //isLastSum 是否是最后一行汇总行
        boolean isLastSum = header.getSibling() == null;
        //判断空值 比较当前节点和下一个兄弟节点是否有同一个父亲节点
        boolean needSumCell = isNotRoot && header.getSibling() != null && header.getSibling().getParent() != null && (header.getParent() != header.getSibling().getParent());
        return isNotRoot && (isLastSum || needSumCell);
    }

    private void adjustColumnIdx(Node temp, TableWidget widget, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                columnIdx.value += widget.getViewTargets().length;
            }
            if (temp.getParent() != null) {
                adjustColumnIdx(temp.getParent(), widget, columnIdx);
            }
        }
    }

    private void getTargetsTitle(TableWidget widget, BISummaryTarget[] usedSumTarget,
                                 StreamPagedIterator pagedIterator, Node top, int rowIdx, FinalInt columnIdx) {

        Node temp = top;
        int lengthWithSum = top.getTotalLength();
        while (temp != null) {
            for (int i = 0; i < lengthWithSum; i++) {
                generateTargetTitleWithSum(usedSumTarget, "", pagedIterator, rowIdx, columnIdx);
            }
            if (widget.showColumnTotal()) {
                generateTargetTitleSum(temp, usedSumTarget, pagedIterator, rowIdx, columnIdx);
            }
            temp = temp.getSibling();
        }
    }

    private void generateTargetTitleSum(Node temp, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                generateTargetTitleWithSum(usedSumTarget, Inter.getLocText("BI-Summary_Values") + ":", pagedIterator, rowIdx, columnIdx);
            }
            generateTargetTitleSum(temp.getParent(), usedSumTarget, pagedIterator, rowIdx, columnIdx);
        }
    }

    private void generateTargetTitleWithSum(BISummaryTarget[] usedSumTarget, String text, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        for (BISummaryTarget anUsedSumTarget : usedSumTarget) {
            CBCell cell = ExecutorUtils.createTitleCell(text + anUsedSumTarget.getText(), rowIdx, 1, columnIdx.value++, 1);
            pagedIterator.addCell(cell);
        }
    }

    private void generateCells(XNode[] roots, TableWidget widget, BIDimension[] rowDimensions, int maxDimLen,
                               TableCellIterator iter, FinalInt start, FinalInt rowIdx, int order) throws Exception {
        //判断奇偶行需要用到标题的行数
        int titleRowSpan = rowIdx.value;
        BIXLeftNode[] xLeftNode = createTempRoots(roots);
        BIXLeftNode[] tempRoots = createTempRoots(roots);
        int rowDimensionsLen = rowDimensions.length;
        int[] oddEven = new int[rowDimensionsLen];
        int[] sumRowNum = new int[rowDimensionsLen];
        oddEven[0] = order;
        Object[] dimensionNames = new Object[rowDimensionsLen];
        int newRow = rowIdx.value & ExportConstants.MAX_ROWS_2007 - 1;
        if (newRow == 0) {
            iter.getIteratorByPage(start.value).finish();
            start.value++;
        }
        StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
        while (xLeftNode[0] != null) {
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = rowDimensionsLen;
            BIXLeftNode[] tempFirstNode = new BIXLeftNode[xLeftNode.length];
            for (int i = 0, j = xLeftNode.length; i < j; i++) {
                BIXLeftNode temp = xLeftNode[i];
                //第一次出现表头时创建cell
                BIXLeftNode parent = xLeftNode[i];
                if (i == 0) {
                    generateDimensionName(widget, parent, rowDimensions, pagedIterator, dimensionNames, oddEven, sumRowNum, rowIdx, columnIdx, maxDimLen, titleRowSpan);
                }
                generateTopChildren(widget, temp, pagedIterator, rowIdx.value, columnIdx, titleRowSpan);
                if (i == 0) {
                    for (int k = 0; k < xLeftNode.length; k++) {
                        tempFirstNode[k] = xLeftNode[k];
                    }
                }
                xLeftNode[i] = (BIXLeftNode) xLeftNode[i].getSibling();
            }
            rowIdx.value++;
            if (widget.showRowToTal()) {
                generateSumRow(tempFirstNode, widget, pagedIterator, rowIdx, sumRowNum, maxDimLen);
            }
        }
        if (widget.showRowToTal()) {
            generateLastSumRow(widget, tempRoots, pagedIterator, rowIdx.value, rowDimensionsLen, maxDimLen);
        }
    }

    private BIXLeftNode[] createTempRoots(XNode[] roots) {
        BIXLeftNode[] xLeftNode = new BIXLeftNode[roots.length];
        for (int i = 0, j = roots.length; i < j; i++) {
            BIXLeftNode node = (BIXLeftNode) roots[i].getLeft();
            while (node.getChildLength() != 0) {
                node = (BIXLeftNode) node.getFirstChild();
            }
            xLeftNode[i] = node;
        }
        return xLeftNode;
    }

    private void generateDimensionName(TableWidget widget, BIXLeftNode parent, BIDimension[] rowDimension, StreamPagedIterator pagedIterator,
                                       Object[] dimensionNames, int[] oddEven, int[] sumRowNum, FinalInt rowIdx, FinalInt columnIdx, int maxDimLen, int titleRowSpan) {

        int i = rowDimension.length;
        while (parent.getParent() != null) {
            int rowSpan = widget.showRowToTal() ? parent.getTotalLengthWithSummary() : parent.getTotalLength();
            Object data = parent.getData();
            BIDimension dim = rowDimension[--i];
            Object v = dim.getValueByType(data);
            if (dim.getGroup().getType() == BIReportConstant.GROUP.YMD && GeneralUtils.string2Number(v.toString()) != null) {
                v = DateUtils.DATEFORMAT2.format(new Date(GeneralUtils.string2Number(v.toString()).longValue()));
            }
            if (v != dimensionNames[i] || (i == dimensionNames.length - 1)) {
                oddEven[i]++;
                CBCell cell = ExecutorUtils.createValueCell(v, rowIdx.value, rowSpan, i, 1, Style.getInstance(), (rowIdx.value - titleRowSpan + 1) % 2 == 1);
                pagedIterator.addCell(cell);
                sumRowNum[i] = rowIdx.value + parent.getTotalLengthWithSummary() - 1;
                //复杂表两个区域的维度的情况下 需要设置最后一个维度单元格columnSpan
                if (i == dimensionNames.length - 1) {
                    int diff = maxDimLen - rowDimension.length;
                    cell.setColumnSpan(diff + 1);
                    //后面指标的位置需要向右偏移
                    columnIdx.value += diff;
                }
                dimensionNames[i] = v;
            }
            parent = (BIXLeftNode) parent.getParent();
        }
    }


    private void generateTopChildren(TableWidget widget, BIXLeftNode temp, StreamPagedIterator pagedIterator,
                                     int rowIdx, FinalInt columnIdx, int titleRowSpan) {
        Number[][] values = temp.getXValue();
        for (int j = 0; j < values[0].length; j++) {
            for (int i = 0; i < widget.getUsedTargetID().length; i++) {
                CBCell cell = formatTargetCell(values[i][j], widget.getChartSetting(), widget.getTargetsKey()[i], rowIdx, columnIdx.value, (rowIdx - titleRowSpan + 1) % 2 == 1);
                pagedIterator.addCell(cell);
                columnIdx.value++;
            }
        }
    }

    private void generateSumRow(BIXLeftNode[] nodes, TableWidget widget, StreamPagedIterator pagedIterator, FinalInt rowIdx, int[] sumRowSum, int maxColumnDimensionsLength) {

        for (int i = sumRowSum.length - 1; i >= 0; i--) {
            //最后一个维度汇总行是本身
            if (i != sumRowSum.length - 1 && (widget.getViewTargets().length != 0) && checkIfGenerateRowSumCell(sumRowSum[i], rowIdx.value)) {
                CBCell cell = ExecutorUtils.createTitleCell(Inter.getLocText("BI-Summary_Values"), rowIdx.value, 1, i + 1, maxColumnDimensionsLength - (i + 1));
                pagedIterator.addCell(cell);
                FinalInt sumIdx = new FinalInt();
                sumIdx.value = maxColumnDimensionsLength;
                for (BIXLeftNode node : nodes) {
                    generateTopChildren(widget, node, pagedIterator, rowIdx.value, sumIdx, 1);
                }
                rowIdx.value++;
            }
            //开辟新内存，不对temp进行修改
            for (int j = 0; j < nodes.length; j++) {
                nodes[j] = (BIXLeftNode) nodes[j].getParent();
            }
        }
    }

    private void generateLastSumRow(TableWidget widget, BIXLeftNode[] xLeftNodes, StreamPagedIterator pagedIterator, int rowIdx, int rowDimensionLen, int maxColumnDimensionsLength) {
        for (int i = 0; i < xLeftNodes.length; i++) {
            while (xLeftNodes[i].getParent() != null) {
                xLeftNodes[i] = (BIXLeftNode) xLeftNodes[i].getParent();
            }
            CBCell cell = ExecutorUtils.createTitleCell(Inter.getLocText("BI-Summary_Values"), rowIdx, 1, 0, rowDimensionLen);
            pagedIterator.addCell(cell);
            FinalInt sumIdx = new FinalInt();
            sumIdx.value = maxColumnDimensionsLength;
            generateTopChildren(widget, xLeftNodes[i], pagedIterator, rowIdx, sumIdx, 1);
        }
    }

    private boolean checkIfGenerateRowSumCell(int sumRowSum, int currentRow) {
        return sumRowSum == currentRow;
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

    public GroupValueIndex getClickGvi(Map<String, JSONArray> clicked, BusinessTable targetKey) {

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