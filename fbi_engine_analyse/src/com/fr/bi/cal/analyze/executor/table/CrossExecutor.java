package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCache;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCacheKey;
import com.fr.bi.cal.analyze.cal.result.BIXLeftNode;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.XNode;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;
import com.fr.bi.cal.analyze.cal.sssecret.PageIteratorGroup;
import com.fr.bi.cal.analyze.executor.iterator.StreamPagedIterator;
import com.fr.bi.cal.analyze.executor.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.cal.analyze.executor.utils.ExecutorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.style.DetailChartSetting;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.ExportConstants;
import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        DetailChartSetting setting = widget.getChartSetting();
        boolean isWholeCol = keys.length == 0 || !setting.showColTotal();
        boolean isWholeRow = keys.length == 0 || !setting.showRowTotal();
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
                    generateTitle(getCubeNode(), pagedIterator, rowIdx);
                    rowIdx.value++;
                    generateCells(getCubeNode(), iter, start, rowIdx);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    private void generateTitle(XNode root, StreamPagedIterator pagedIterator, FinalInt rowIdx) throws Exception {

        Node top = root.getTop();
        int columnTitleSpan = rowDimension.length;
        int columnEndIdx = top.getTotalLengthWithSummary() * usedSumTarget.length + columnTitleSpan;
        int colDimLen = 0;
        while (colDimLen < colDimension.length) {
            CBCell cell = ExecutorUtils.createCBCell(colDimension[colDimLen].getText(), rowIdx.value, 1, 0, rowDimension.length, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = columnTitleSpan;
            top = top.getFirstChild();
            getColDimensionsTitle(widget, usedSumTarget, pagedIterator, top, rowIdx.value, columnIdx);
            rowIdx.value++;
            colDimLen++;
        }

        for (int i = 0; i < columnTitleSpan; i++) {
            CBCell cell = ExecutorUtils.createCBCell(rowDimension[i].getText(), rowIdx.value, 1, i, 1, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
        }
        FinalInt targetsTitleColumnIdx = new FinalInt();
        targetsTitleColumnIdx.value = columnTitleSpan;
        getTargetsTitle(pagedIterator, top, rowIdx.value, targetsTitleColumnIdx, columnEndIdx);
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
            Object v = ExecutorUtils.formatDateGroup(dim.getGroup().getType(), dim.toString(dim.getValueByType(data)));
            int rowSpan = (rowIdx == dims.length - 1) ? (usedSumTarget.length == 1 ? 2 : 1) : 1;
            CBCell cell = ExecutorUtils.createCBCell(v, rowIdx, rowSpan, columnIdx.value, columnSpan, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
            columnIdx.value += columnSpan;
            if (widget.showColumnTotal()) {
                generateTitleSumCells(temp, pagedIterator, rowIdx, columnIdx);
            }
            temp = temp.getSibling();
        }
    }

    private void generateTitleSumCells(Node temp, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp) && temp.getParent().getChildLength() != 1) {
            if (rowIdx > 0) {
                CBCell cell = ExecutorUtils.createCBCell(Inter.getLocText("BI-Summary_Values"), rowIdx, temp.getDeep(), columnIdx.value, widget.getViewTargets().length, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
                pagedIterator.addCell(cell);
            } else {
                generateTargetTitleWithSum(Inter.getLocText("BI-Summary_Values") + ":", pagedIterator, rowIdx, columnIdx, temp.getDeep() + 1);
            }
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

    private void getTargetsTitle(StreamPagedIterator pagedIterator, Node top, int rowIdx, FinalInt columnIdx, int colEndIdx) {

        Node temp = top;
        int lengthWithSum = top.getTotalLength();
        while (temp != null && columnIdx.value < colEndIdx) {
            for (int i = 0; i < lengthWithSum; i++) {
                generateTargetTitleWithSum("", pagedIterator, rowIdx, columnIdx, 1);
            }
            if (widget.showColumnTotal()) {
                generateTargetTitleSum(temp, pagedIterator, rowIdx, columnIdx);
            }
            temp = temp.getSibling();
        }
    }

    private void generateTargetTitleSum(Node temp, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx) {

        if (checkIfGenerateTitleSumCells(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                generateTargetTitleWithSum(Inter.getLocText("BI-Summary_Values") + ":", pagedIterator, rowIdx, columnIdx, 1);
            }
            generateTargetTitleSum(temp.getParent(), pagedIterator, rowIdx, columnIdx);
        }
    }

    private void generateTargetTitleWithSum(String text, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx, int rowSpan) {

        for (BISummaryTarget anUsedSumTarget : usedSumTarget) {
            DetailChartSetting setting = widget.getChartSetting();
            int numLevel = setting.getNumberLevelByTargetId(anUsedSumTarget.getId());
            String unit = setting.getUnitByTargetId(anUsedSumTarget.getId());
            String levelAndUnit = ExecutorUtils.formatLevelAndUnit(numLevel, unit);
            String dimensionUnit = StringUtils.isEmpty(levelAndUnit) ? "" : "(" + levelAndUnit + ")";
            CBCell cell = ExecutorUtils.createCBCell(text + anUsedSumTarget.getText() + dimensionUnit, rowIdx, rowSpan, columnIdx.value++, 1, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
        }
    }

    private void generateCells(XNode root, TableCellIterator iter, FinalInt start, FinalInt rowIdx) throws Exception {
        //判断奇偶行需要用到标题的行数
        int rowTitleSpan = rowIdx.value;
        BIXLeftNode xLeftNode = createTempRoot(root);
        BIXLeftNode tempRoots = createTempRoot(root);
        int rowDimensionsLen = rowDimension.length;
        int[] oddEven = new int[rowDimensionsLen];
        int[] sumRowNum = new int[rowDimensionsLen];
        oddEven[0] = 0;
        Object[] dimensionNames = new Object[rowDimensionsLen];
        int newRow = rowIdx.value & ExportConstants.MAX_ROWS_2007 - 1;
        if (newRow == 0) {
            iter.getIteratorByPage(start.value).finish();
            start.value++;
        }
        StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
        while (xLeftNode != null) {
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = rowDimensionsLen;
            BIXLeftNode tempFirstNode = xLeftNode;
            //第一次出现表头时创建cell
            BIXLeftNode parent = xLeftNode;
            Style style = (rowIdx.value - rowTitleSpan + 1) % 2 == 1 ? widget.getTableStyle().getOddRowStyle(Style.getInstance()) : widget.getTableStyle().getEvenRowStyle(Style.getInstance());
            generateTopChildren(tempFirstNode, pagedIterator, rowIdx.value, rowDimensionsLen, style);
            generateDimensionName(parent, rowDimension, pagedIterator, dimensionNames, oddEven, sumRowNum, rowIdx, rowTitleSpan);
            xLeftNode = (BIXLeftNode) xLeftNode.getSibling();
            rowIdx.value++;
            if (widget.showRowToTal()) {
                generateSumRow(tempFirstNode, pagedIterator, rowIdx, sumRowNum, rowDimensionsLen, rowTitleSpan);
            }
        }
        if (widget.showRowToTal()) {
            generateLastSumRow(tempRoots, pagedIterator, rowIdx.value, rowDimensionsLen, rowTitleSpan);
        }
    }

    private static BIXLeftNode createTempRoot(XNode root) {
        BIXLeftNode node = (BIXLeftNode) root.getLeft();
        while (node.getChildLength() != 0) {
            node = (BIXLeftNode) node.getFirstChild();
        }
        return node;
    }

    private void generateDimensionName(BIXLeftNode parent, BIDimension[] rowDimension, StreamPagedIterator pagedIterator, Object[] dimensionNames, int[] oddEven, int[] sumRowNum, FinalInt rowIdx, int titleRowSpan) {

        int i = rowDimension.length;
        while (parent.getParent() != null) {
            int rowSpan = widget.showRowToTal() ? parent.getTotalLengthWithSummary() : parent.getTotalLength();
            Object data = parent.getData();
            BIDimension dim = rowDimension[--i];
            Object v = ExecutorUtils.formatDateGroup(dim.getGroup().getType(), dim.toString(data));
            if (v != dimensionNames[i] || (i == dimensionNames.length - 1) || parent.getParent().getTotalLength() == 1) {
                oddEven[i]++;
                Style style = (rowIdx.value - titleRowSpan + 1) % 2 == 1 ? widget.getTableStyle().getOddRowStyle(Style.getInstance()) : widget.getTableStyle().getEvenRowStyle(Style.getInstance());
                CBCell cell = ExecutorUtils.createCBCell(v, rowIdx.value, rowSpan, i, 1, style);
                pagedIterator.addCell(cell);
                sumRowNum[i] = rowIdx.value + parent.getTotalLengthWithSummary() - 1;
                dimensionNames[i] = v;
            }
            parent = (BIXLeftNode) parent.getParent();
        }
    }


    private void generateTopChildren(BIXLeftNode temp, StreamPagedIterator pagedIterator, int rowIdx, int columnIdx, Style style) {
        Number[][] values = temp.getXValue();
        for (int j = 0; j < values[0].length; j++) {
            for (TargetGettingKey key : widget.getTargetsKey()) {
                CBCell cell = formatTargetCell(values[key.getTargetIndex()][j], widget.getChartSetting(), key, rowIdx, columnIdx, style);
                pagedIterator.addCell(cell);
                columnIdx++;
            }
        }
    }

    private void generateSumRow(BIXLeftNode node, StreamPagedIterator pagedIterator, FinalInt rowIdx, int[] sumRowSum, int rowDimensionLen, int titleRowSpan) {

        for (int i = sumRowSum.length - 1; i >= 0; i--) {
            //最后一个行表头汇总行是本身
            if (i != sumRowSum.length - 1 && (widget.getViewTargets().length != 0) && checkIfGenerateRowSumCell(sumRowSum[i], rowIdx.value)) {
                CBCell cell = ExecutorUtils.createCBCell(Inter.getLocText("BI-Summary_Values"), rowIdx.value, 1, i + 1, rowDimensionLen - (i + 1), widget.getTableStyle().getSumRowStyle(Style.getInstance()));
                pagedIterator.addCell(cell);
                generateTopChildren(node, pagedIterator, rowIdx.value, rowDimensionLen, widget.getTableStyle().getSumRowStyle(Style.getInstance()));
                rowIdx.value++;
            }
            node = (BIXLeftNode) node.getParent();
        }
    }

    private void generateLastSumRow(BIXLeftNode xLeftNodes, StreamPagedIterator pagedIterator, int rowIdx, int rowDimensionLen, int titleRowSpan) {
        while (xLeftNodes.getParent() != null) {
            xLeftNodes = (BIXLeftNode) xLeftNodes.getParent();
        }
        CBCell cell = ExecutorUtils.createCBCell(Inter.getLocText("BI-Summary_Values"), rowIdx, 1, 0, rowDimensionLen, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
        pagedIterator.addCell(cell);
        generateTopChildren(xLeftNodes, pagedIterator, rowIdx, rowDimensionLen, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
    }

    private static boolean checkIfGenerateRowSumCell(int sumRowSum, int currentRow) {
        return sumRowSum == currentRow;
    }

    protected WidgetCacheKey createWidgetCacheKey() {
        PageIteratorGroup iteratorGroup = getPageIterator();
        Operator rowOp = PagingFactory.createRowOperator(paging.getOperator(), widget);
        Operator colOp = PagingFactory.createColumnOperator(paging.getOperator(), widget);
        int[] rowStartIndex = getStartIndex(rowOp, iteratorGroup == null ? null : iteratorGroup.getRowIterator(), rowDimension.length);
        int[] colStartIndex = getStartIndex(colOp, iteratorGroup == null ? null : iteratorGroup.getColumnIterator(), colDimension.length);
        return WidgetCacheKey.createKey(widget.fetchObjectCore(), expander.getYExpander(), expander.getXExpander(),
                rowOp, rowStartIndex, colOp, colStartIndex, widget.getAuthFilter(session.getUserId()));
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
        WidgetCacheKey key = createWidgetCacheKey();
        WidgetCache<JSONObject> widgetCache = getWidgetCache(key);
        if (widgetCache != null) {
            updateByCache(widgetCache);
            return widgetCache.getData();
        }
        JSONObject jo = getCubeNode().toJSONObject(rowDimension, colDimension, widget.getTargetsKey(), widget.showColumnTotal());
        if (isUseWidgetDataCache()){
            PageIteratorGroup pg = session.getPageIteratorGroup(true, widget.getWidgetId());
            NodeDimensionIterator rowIter = pg.getRowIterator().createClonedIterator();
            rowIter.setRoot(null);
            NodeDimensionIterator colIter = pg.getColumnIterator().createClonedIterator();
            colIter.setRoot(null);
            updateCache(key, new WidgetCache(jo, rowIter, colIter, widget.getPageSpinner()));
        }
        return jo;
    }

    private void updateByCache(WidgetCache widgetCache) {
        widget.setPageSpinner(widgetCache.getPageSpinner());
        PageIteratorGroup pg = session.getPageIteratorGroup(true, widget.getWidgetId());
        if (pg == null) {
            pg = new PageIteratorGroup();
            pg.setRowIterator(widgetCache.getRowIterator());
            pg.setColumnIterator(widgetCache.getColumnIterator());
            session.setPageIteratorGroup(true, widget.getWidgetId(), pg);
        } else {
            NodeDimensionIterator rowIterator = widgetCache.getRowIterator().createClonedIterator();
            rowIterator.setRoot(pg.getRowIterator().getRoot());
            pg.setRowIterator(rowIterator);
            NodeDimensionIterator colIterator = widgetCache.getColumnIterator().createClonedIterator();
            colIterator.setRoot(pg.getColumnIterator().getRoot());
            pg.setColumnIterator(colIterator);
        }
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