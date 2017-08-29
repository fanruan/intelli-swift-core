package com.fr.bi.cal.analyze.executor.table;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.base.Style;
import com.fr.bi.base.FinalInt;
import com.fr.bi.cal.analyze.cal.index.loader.CubeIndexLoader;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCache;
import com.fr.bi.cal.analyze.cal.index.loader.cache.WidgetCacheKey;
import com.fr.bi.report.result.BIXLeftNode;
import com.fr.bi.cal.analyze.cal.result.CrossExpander;
import com.fr.bi.cal.analyze.cal.result.Node;
import com.fr.bi.cal.analyze.cal.result.XNode;
import com.fr.bi.cal.analyze.cal.result.operator.Operator;
import com.fr.bi.cal.analyze.cal.sssecret.NodeDimensionIterator;
import com.fr.bi.cal.analyze.cal.sssecret.PageIteratorGroup;
import com.fr.bi.export.iterator.StreamPagedIterator;
import com.fr.bi.export.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.export.utils.GeneratorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.conf.BIWidgetConf;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BICrossNode;
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

    protected WidgetCacheKey createWidgetCacheKey() {
        PageIteratorGroup iteratorGroup = getPageIterator();
        Operator rowOp = PagingFactory.createRowOperator(paging.getOperator(), widget);
        Operator colOp = PagingFactory.createColumnOperator(paging.getOperator(), widget);
        int[] rowStartIndex = getStartIndex(rowOp, iteratorGroup == null ? null : iteratorGroup.getRowIterator(), rowDimension.length);
        int[] colStartIndex = getStartIndex(colOp, iteratorGroup == null ? null : iteratorGroup.getColumnIterator(), colDimension.length);
        return WidgetCacheKey.createKey(widget.fetchObjectCore(), expander.getYExpander(), expander.getXExpander(), rowOp, rowStartIndex, colOp, colStartIndex, widget.getAuthFilter(session.getUserId()));
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
            BILoggerFactory.getLogger(GroupExecutor.class).info("data existed in caches,get data from caches");
            updateByCache(widgetCache);
            return widgetCache.getData();
        }
        JSONObject jo = getCubeNode().toJSONObject(rowDimension, colDimension, widget.getTargetsKey(), widget.showColumnTotal());
        if (isUseWidgetDataCache()) {
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
            if (pg.getRowIterator() != null) {
                rowIterator.setRoot(pg.getRowIterator().getRoot());
            }
            pg.setRowIterator(rowIterator);
            NodeDimensionIterator colIterator = widgetCache.getColumnIterator().createClonedIterator();
            if (pg.getColumnIterator() != null) {
                colIterator.setRoot(pg.getColumnIterator().getRoot());
            }
            pg.setColumnIterator(colIterator);
        }
    }

    public GroupValueIndex getClickGvi(Map<String, JSONArray> clicked, BusinessTable targetKey) {

        GroupValueIndex linkGvi = null;
        try {
            String target = getClickTarget(clicked);
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
            Node left = cubeIndexLoader.getStopWhenGetRowNode(row.toArray(), widget, createTarget4Calculate(), rowDimension, allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
            Node top = cubeIndexLoader.getStopWhenGetRowNode(col.toArray(), widget, createTarget4Calculate(), colDimension, allDimensions, allSumTarget, calPage, session, CrossExpander.ALL_EXPANDER.getYExpander());
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

    public BICrossNode getResult() throws Exception {

        return getCubeNode();
    }
}
