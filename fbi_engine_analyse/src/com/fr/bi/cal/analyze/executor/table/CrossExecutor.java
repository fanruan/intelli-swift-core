package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.result.BIXLeftNode;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.BIXLeftNode;
import com.fr.bi.cal.analyze.cal.result.XNode;
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
import com.fr.stable.ExportConstants;

import java.util.*;

public class CrossExecutor extends AbstractTableWidgetExecutor<XNode> {

    private BIDimension[] rowDimension;

    private BIDimension[] colDimension;

    private CrossExpander expander;

    public CrossExecutor(TableWidget widget, BIDimension[] usedRows,
                         BIDimension[] usedColumn,
                         Paging paging, BISession session, CrossExpander expander) {

        super(widget, paging, session);
        this.rowDimension = usedRows;
        this.colDimension = usedColumn;
        this.expander = expander;
    }

    @Override
    public TableCellIterator createCellIterator4Excel() throws Exception {

        XNode node = getCubeNode();
        if (node == null) {
            return new TableCellIterator(0, 0);
        }

        int len = usedSumTarget.length;
        TargetGettingKey[] keys = new TargetGettingKey[len];
        boolean isWholeCol = keys.length == 0 || !widget.getChartSetting().showColTotal();
        boolean isWholeRow = keys.length == 0 || !widget.getChartSetting().showRowTotal();
        int columnLen = (isWholeCol ? node.getTop().getTotalLength() :
                node.getTop().getTotalLengthWithSummary()) * Math.max(1, keys.length) + rowDimension.length + widget.isOrder();
        int rowLen = (isWholeRow ? node.getLeft().getTotalLength() :
                node.getLeft().getTotalLengthWithSummary()) + colDimension.length + 1;

        final TableCellIterator iter = new TableCellIterator(columnLen, rowLen);
        new Thread() {

            public void run() {

                try {
                    FinalInt start = new FinalInt();
                    FinalInt rowIdx = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    XNode[] xNodes = new XNode[1];
                    xNodes[0] = getCubeNode();
                    generateTitle(xNodes, widget, colDimension, rowDimension, usedSumTarget, pagedIterator, rowIdx);
                    rowIdx.value++;
                    generateCells(xNodes, widget, rowDimension, rowDimension.length, iter, start, rowIdx, 0);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    /**
     * @param roots         ComplexCrossExecutor复用此方法时需要的参数
     * @param widget        ComplexCrossExecutor复用此方法时需要的参数
     * @param colDimension  ComplexCrossExecutor复用此方法时需要的参数
     * @param rowDimension  ComplexCrossExecutor复用此方法时需要的参数
     * @param usedSumTarget ComplexCrossExecutor复用此方法时需要的参数
     * @param pagedIterator
     * @param rowIdx
     * @throws Exception
     */
    public static void generateTitle(XNode[] roots, TableWidget widget, BIDimension[] colDimension, BIDimension[] rowDimension,
                                     BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator, FinalInt rowIdx) throws Exception {

        int rootsLen = roots.length;
        Node[] tops = new Node[rootsLen];
        for (int i = 0; i < rootsLen; i++) {
            tops[i] = roots[i].getTop();
        }

        int colDimLen = 0;
        while (colDimLen < colDimension.length) {
            CBCell cell = ExecutorUtils.createTitleCell(colDimension[colDimLen].getText(), rowIdx.value, 1, 0, rowDimension.length);
            pagedIterator.addCell(cell);
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = rowDimension.length;
            for (int i = 0; i < rootsLen; i++) {
                tops[i] = tops[i].getFirstChild();
                //列表头
                getColDimensionsTitle(widget, usedSumTarget, pagedIterator, tops[i], rowIdx.value, columnIdx);
            }
            rowIdx.value++;
            colDimLen++;
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

    private static void getColDimensionsTitle(TableWidget widget, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator,
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

    private static void generateTitleSumCells(Node temp, TableWidget widget, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp) && temp.getParent().getChildLength() != 1) {
            CBCell cell = ExecutorUtils.createTitleCell(Inter.getLocText("BI-Summary_Values"), rowIdx, temp.getDeep(), columnIdx.value, widget.getViewTargets().length);
            pagedIterator.addCell(cell);
        }
        adjustColumnIdx(temp, widget, columnIdx);
    }

    private static boolean checkIfGenerateTitleSumCells(Node header) {
        //到根节点停止
        boolean isNotRoot = header.getParent() != null;
        //isLastSum 是否是最后一行汇总行
        boolean isLastSum = header.getSibling() == null;
        //判断空值 比较当前节点和下一个兄弟节点是否有同一个父亲节点
        boolean needSumCell = isNotRoot && header.getSibling() != null && header.getSibling().getParent() != null && (header.getParent() != header.getSibling().getParent());
        return isNotRoot && (isLastSum || needSumCell);
    }

    private static void adjustColumnIdx(Node temp, TableWidget widget, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                columnIdx.value += widget.getViewTargets().length;
            }
            if (temp.getParent() != null) {
                adjustColumnIdx(temp.getParent(), widget, columnIdx);
            }
        }
    }

    private static void getTargetsTitle(TableWidget widget, BISummaryTarget[] usedSumTarget,
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

    private static void generateTargetTitleSum(Node temp, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                generateTargetTitleWithSum(usedSumTarget, Inter.getLocText("BI-Summary_Values") + ":", pagedIterator, rowIdx, columnIdx);
            }
            generateTargetTitleSum(temp.getParent(), usedSumTarget, pagedIterator, rowIdx, columnIdx);
        }
    }

    private static void generateTargetTitleWithSum(BISummaryTarget[] usedSumTarget, String text, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        for (BISummaryTarget anUsedSumTarget : usedSumTarget) {
            CBCell cell = ExecutorUtils.createTitleCell(text + anUsedSumTarget.getText(), rowIdx, 1, columnIdx.value++, 1);
            pagedIterator.addCell(cell);
        }
    }

    /**
     * @param roots         ComplexCrossExecutor复用此方法时需要的参数
     * @param widget        ComplexCrossExecutor复用此方法时需要的参数
     * @param rowDimensions ComplexCrossExecutor复用此方法时需要的参数
     * @param maxDimLen     ComplexCrossExecutor复用此方法时需要的参数 列表头中维度最多区域中维度的长度
     * @param iter
     * @param start
     * @param rowIdx        ComplexCrossExecutor复用此方法时需要的参数 记录行数
     * @param order         ComplexCrossExecutor复用此方法时需要的参数 记录序号
     * @throws Exception
     */
    public static void generateCells(XNode[] roots, TableWidget widget, BIDimension[] rowDimensions, int maxDimLen,
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
                    generateDimensionName(widget, parent, rowDimensions, pagedIterator, dimensionNames, oddEven, sumRowNum, rowIdx, columnIdx, maxDimLen);
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
        if(widget.showRowToTal()) {
            generateLastSumRow(widget, tempRoots, pagedIterator, rowIdx.value, rowDimensionsLen, maxDimLen);
        }
    }

    private static BIXLeftNode[] createTempRoots (XNode[] roots) {
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

    private static void generateDimensionName(TableWidget widget, BIXLeftNode parent, BIDimension[] rowDimension, StreamPagedIterator pagedIterator,
                                              Object[] dimensionNames, int[] oddEven, int[] sumRowNum, FinalInt rowIdx, FinalInt columnIdx, int maxDimLen) {

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
                CBCell cell = ExecutorUtils.createValueCell(v, rowIdx.value, rowSpan, i, 1, Style.getInstance(), (rowIdx.value - maxDimLen) % 2 == 1);
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


    private static void generateTopChildren(TableWidget widget, BIXLeftNode temp, StreamPagedIterator pagedIterator,
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

    private static void generateSumRow(BIXLeftNode[] nodes, TableWidget widget, StreamPagedIterator pagedIterator, FinalInt rowIdx, int[] sumRowSum, int maxColumnDimensionsLength) {

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

    private static void generateLastSumRow (TableWidget widget, BIXLeftNode[] xLeftNodes, StreamPagedIterator pagedIterator, int rowIdx, int rowDimensionLen, int maxColumnDimensionsLength) {
        for(int i = 0; i < xLeftNodes.length; i++) {
            while(xLeftNodes[i].getParent() != null) {
                xLeftNodes[i] = (BIXLeftNode) xLeftNodes[i].getParent();
            }
            CBCell cell = ExecutorUtils.createTitleCell(Inter.getLocText("BI-Summary_Values"), rowIdx, 1, 0, rowDimensionLen);
            pagedIterator.addCell(cell);
            FinalInt sumIdx = new FinalInt();
            sumIdx.value = maxColumnDimensionsLength;
            generateTopChildren(widget, xLeftNodes[i], pagedIterator, rowIdx, sumIdx, 1);
        }
    }

    private static boolean checkIfGenerateRowSumCell(int sumRowSum, int currentRow) {
        return sumRowSum == currentRow;
    }

    @Override
    public XNode getCubeNode() throws Exception {

        long start = System.currentTimeMillis();
        if (getSession() == null) {
            return null;
        }
        int len = usedSumTarget.length;
        Map<String, TargetGettingKey> targetsMap = new HashMap<String, TargetGettingKey>();
        TargetGettingKey[] keys = new TargetGettingKey[len];
        for (int i = 0; i < len; i++) {
            keys[i] = usedSumTarget[i].createTargetGettingKey();
            targetsMap.put(usedSumTarget[i].getValue(), keys[i]);
        }
        int calpage = paging.getOperator();

        XNode node = CubeIndexLoader.getInstance(session.getUserId()).loadPageCrossGroup(createTarget4Calculate(), rowDimension, colDimension, allSumTarget, calpage, widget.useRealData(), session, expander, widget);

        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return node;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {

        return getCubeNode().toJSONObject(rowDimension, colDimension, widget.getTargetsKey(), widget.showColumnTotal());
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
            getLinkRowAndColData(clicked, target, row, col, null, null);
            CubeIndexLoader cubeIndexLoader = CubeIndexLoader.getInstance(session.getUserId());
            int calPage = paging.getOperator();
            Node left = cubeIndexLoader.getStopWhenGetRowNode(row.toArray(), widget, createTarget4Calculate(), rowDimension,
                    allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
            Node top = cubeIndexLoader.getStopWhenGetRowNode(col.toArray(), widget, createTarget4Calculate(), colDimension,
                    allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
            if (row.size() == 0 && col.size() == 0) {
                // 总汇总值得时候
                linkGvi = GVIUtils.AND(linkGvi, getTargetIndex(target, left));
                linkGvi = GVIUtils.AND(linkGvi, getTargetIndex(target, top));
                return linkGvi;
            }
            linkGvi = GVIUtils.AND(getLinkNodeFilter(left, target, row), linkGvi);
            linkGvi = GVIUtils.AND(getLinkNodeFilter(top, target, col), linkGvi);
            return linkGvi;
        } catch (Exception e) {
            BILoggerFactory.getLogger(CrossExecutor.class).info("error in get link filter", e);
        }
        return linkGvi;
    }
}