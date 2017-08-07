package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCache;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCacheKey;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
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
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.stable.gvi.GVIUtils;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class HorGroupExecutor extends AbstractTableWidgetExecutor<Node> {

    private Rectangle rectangle;

    private BIDimension[] colDimension;

    private BIDimension[] usedDimensions;

    private CrossExpander expander;

    public HorGroupExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {

        super(widget, paging, session);
        usedDimensions = widget.getViewTopDimensions();
        colDimension = usedDimensions;
        this.expander = expander;
    }

    public TableCellIterator createCellIterator4Excel() throws Exception {

        final Node node = getCubeNode();
        int rowLength = colDimension.length + usedSumTarget.length;
        int columnLength = node.getTotalLength() + widget.isOrder() + 1;
        //显示不显示汇总行
        int rowLen = widget.getChartSetting().showRowTotal() ? node.getTotalLengthWithSummary() : node.getTotalLength();
        rectangle = new Rectangle(rowLength + widget.isOrder(), 1, columnLength + widget.isOrder() - 1, rowLen);
        final TableCellIterator iter = new TableCellIterator(columnLength, rowLength);
        new Thread() {

            public void run() {

                try {
                    FinalInt start = new FinalInt();
                    StreamPagedIterator pagedIterator = iter.getIteratorByPage(start.value);
                    generateTitle(node, widget, colDimension, pagedIterator);
                    generateCells(node, widget, colDimension, usedSumTarget, pagedIterator);
                } catch (Exception e) {
                    BILoggerFactory.getLogger().error(e.getMessage(), e);
                } finally {
                    iter.finish();
                }
            }
        }.start();
        return iter;
    }

    private void generateTitle(Node node, TableWidget widget, BIDimension[] colDimension, StreamPagedIterator pagedIterator) {

        int rowIdx = 0;
        while (rowIdx < colDimension.length) {
            CBCell cell = ExecutorUtils.createCBCell(colDimension[rowIdx].getText(), rowIdx, 1, 0, 1, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
            node = node.getFirstChild();
            Node temp = node;
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = 1;
            BIDimension dim = colDimension[rowIdx];
            while (temp != null) {
                Object v = ExecutorUtils.formatDateGroup(dim.getGroup().getType(), dim.toString(temp.getData()));
                CBCell dimCell = ExecutorUtils.createCBCell(v, rowIdx, 1, columnIdx.value, widget.showColumnTotal() ? temp.getTotalLengthWithSummary() : temp.getTotalLength(), widget.getTableStyle().getHeaderStyle(Style.getInstance()));
                pagedIterator.addCell(dimCell);
                columnIdx.value += widget.showColumnTotal() ? temp.getTotalLengthWithSummary() : temp.getTotalLength();
                if (widget.showColumnTotal()) {
                    generateTitleSumCells(widget, temp, pagedIterator, rowIdx, columnIdx, colDimension.length);
                }
                temp = temp.getSibling();
            }
            rowIdx++;
        }
    }

    protected static void generateTitleSumCells(TableWidget widget, Node temp, StreamPagedIterator pagedIterator, int rowIdx, FinalInt columnIdx, int maxColumnDimLen) {

        if (checkIfGenerateSumCell(temp) && temp.getParent().getChildLength() != 1) {
            CBCell cell = ExecutorUtils.createCBCell(Inter.getLocText("BI-Summary_Values"), rowIdx, maxColumnDimLen - rowIdx, columnIdx.value, 1, widget.getTableStyle().getHeaderStyle(Style.getInstance()));
            pagedIterator.addCell(cell);
        }
        adjustColumnIdx(temp, columnIdx);
    }

    private static void adjustColumnIdx(Node temp, FinalInt columnIdx) {

        if (checkIfGenerateSumCell(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                columnIdx.value++;
            }
            if (temp.getParent() != null) {
                adjustColumnIdx(temp.getParent(), columnIdx);
            }
        }
    }

    private void generateCells(Node node, TableWidget widget, BIDimension[] colDimension, BISummaryTarget[] usedSumTarget, StreamPagedIterator pagedIterator) {

        int colDimensionLen = colDimension.length;
        TargetGettingKey[] keys = widget.getTargetsKey();
        while (node.getFirstChild() != null) {
            node = node.getFirstChild();
        }

        for (int i = 0; i < usedSumTarget.length; i++) {
            FinalInt columnIdx = new FinalInt();
            columnIdx.value = 1;
            Object targetName = usedSumTarget[i].getText();
            Style style = (i + 1) % 2 == 1 ? widget.getTableStyle().getOddRowStyle(Style.getInstance()) : widget.getTableStyle().getEvenRowStyle(Style.getInstance());
            CBCell targetNameCell = ExecutorUtils.createCBCell(targetName, colDimensionLen + i, 1, 0, 1, style);
            pagedIterator.addCell(targetNameCell);
            Node temp = node;
            while (temp != null) {
                Object data = temp.getSummaryValue(keys[i]);
                CBCell cell = formatTargetCell(data, widget.getChartSetting(), keys[i], colDimensionLen + i, columnIdx.value++, style);
                pagedIterator.addCell(cell);
                if (widget.showColumnTotal()) {
                    generateTargetSumCell(temp, widget, keys[i], pagedIterator, colDimensionLen, columnIdx, i);
                }
                temp = temp.getSibling();
            }
        }
    }

    public static void generateTargetSumCell(Node temp, TableWidget widget, TargetGettingKey key, StreamPagedIterator pagedIterator, int colDimensionLen, FinalInt columnIdx, int targetRowIdx) {

        if ((widget.getViewTargets().length != 0) && checkIfGenerateSumCell(temp)) {
            if (temp.getParent().getChildLength() != 1) {
                Object data = temp.getParent().getSummaryValue(key);
                Style style = (targetRowIdx + 1) % 2 == 1 ? widget.getTableStyle().getOddRowStyle(Style.getInstance()) : widget.getTableStyle().getEvenRowStyle(Style.getInstance());
                CBCell cell = formatTargetCell(data, widget.getChartSetting(), key, targetRowIdx + colDimensionLen, columnIdx.value++, style);
                pagedIterator.addCell(cell);
            }
            Node parent = temp.getParent();
            generateTargetSumCell(parent, widget, key, pagedIterator, colDimensionLen, columnIdx, targetRowIdx);
        }
    }


    protected WidgetCacheKey createWidgetCacheKey() {
        PageIteratorGroup iteratorGroup = getPageIterator();
        Operator colOp = PagingFactory.createColumnOperator(paging.getOperator(), widget);
        return WidgetCacheKey.createKey(widget.fetchObjectCore(), expander.getXExpander(), expander.getXExpander(),
                null, null, colOp, getStartIndex(colOp, iteratorGroup == null ? null : iteratorGroup.getColumnIterator(), usedDimensions.length),
                widget.getAuthFilter(session.getUserId()));
    }

    @Override
    public Node getCubeNode() throws Exception {

        if (session == null) {
            return null;
        }
        int rowLength = usedDimensions.length;
        int summaryLength = usedSumTarget.length;
        int columnLen = rowLength + summaryLength;
        if (columnLen == 0) {
            return null;
        }
        long start = System.currentTimeMillis();

        int calpage = paging.getOperator();
        CubeIndexLoader cubeIndexLoader = CubeIndexLoader.getInstance(session.getUserId());
        Node tree = cubeIndexLoader.loadPageGroup(true, widget, createTarget4Calculate(), usedDimensions, allDimensions, allSumTarget, calpage, widget.isRealData(), session, expander.getXExpander());
        if (tree == null) {
            tree = new Node(null, allSumTarget.length);
        }
        BILoggerFactory.getLogger().info(DateUtils.timeCostFrom(start) + ": cal time");
        return tree;
    }

    @Override
    public JSONObject createJSONObject() throws Exception {
        WidgetCacheKey key = createWidgetCacheKey();
        WidgetCache<JSONObject> widgetCache = getWidgetCache(key);
        if (widgetCache != null) {
            updateByCache(widgetCache);
            return widgetCache.getData();
        }
        JSONObject jo = getCubeNode().toJSONObject(usedDimensions, widget.getTargetsKey(), -1);
        if (isUseWidgetDataCache()){
            PageIteratorGroup pg = session.getPageIteratorGroup(true, widget.getWidgetId());
            NodeDimensionIterator colIter = pg.getColumnIterator().createClonedIterator();
            colIter.setRoot(null);
            updateCache(key, new WidgetCache(jo, null, colIter, widget.getPageSpinner()));
        }
        return jo;
    }

    private void updateByCache(WidgetCache widgetCache) {
        widget.setPageSpinner(widgetCache.getPageSpinner());
        PageIteratorGroup pg = session.getPageIteratorGroup(true, widget.getWidgetId());
        if (pg == null) {
            pg = new PageIteratorGroup();
            pg.setColumnIterator(widgetCache.getColumnIterator());
            session.setPageIteratorGroup(true, widget.getWidgetId(), pg);
        } else {
            NodeDimensionIterator colIterator = widgetCache.getColumnIterator().createClonedIterator();
            colIterator.setRoot(pg.getColumnIterator().getRoot());
            pg.setColumnIterator(colIterator);
        }
    }

    @Override
    public Rectangle getSouthEastRectangle() {

        return rectangle;
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
            if (!targetKey.equals(linkTargetTable)) {
                return null;
            }
            List<Object> col = getLinkRowData(clicked, target, true);
            Node linkNode = getStopOnRowNode(col.toArray(), widget.getViewTopDimensions());
            // 总汇总值
            if (col == null || col.size() == 0) {
                for (String key : clicked.keySet()) {
                    linkGvi = GVIUtils.AND(linkGvi, getTargetIndex(key, linkNode));
                }
                return linkGvi;
            }
            linkGvi = GVIUtils.AND(linkGvi, getLinkNodeFilter(linkNode, target, col));
        } catch (Exception e) {
            BILoggerFactory.getLogger(GroupExecutor.class).info("error in get link filter", e);
        }
        return linkGvi;
    }
}