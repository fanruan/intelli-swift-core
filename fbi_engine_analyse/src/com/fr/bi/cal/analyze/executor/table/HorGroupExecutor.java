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
import com.fr.bi.export.iterator.StreamPagedIterator;
import com.fr.bi.export.iterator.TableCellIterator;
import com.fr.bi.cal.analyze.executor.paging.Paging;
import com.fr.bi.cal.analyze.executor.paging.PagingFactory;
import com.fr.bi.export.utils.GeneratorUtils;
import com.fr.bi.cal.analyze.report.report.widget.TableWidget;
import com.fr.bi.cal.analyze.session.BISession;
import com.fr.bi.cal.report.engine.CBCell;
import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.bi.field.target.target.BISummaryTarget;
import com.fr.bi.report.key.TargetGettingKey;
import com.fr.bi.report.result.BIGroupNode;
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

    private BIDimension[] colDimension;

    private BIDimension[] usedDimensions;

    private CrossExpander expander;

    public HorGroupExecutor(TableWidget widget, Paging paging, BISession session, CrossExpander expander) {

        super(widget, paging, session);
        usedDimensions = widget.getViewTopDimensions();
        colDimension = usedDimensions;
        this.expander = expander;
    }

    protected WidgetCacheKey createWidgetCacheKey() {
        PageIteratorGroup iteratorGroup = getPageIterator();
        Operator colOp = PagingFactory.createColumnOperator(paging.getOperator(), widget);
        return WidgetCacheKey.createKey(widget.fetchObjectCore(), expander.getYExpander(), expander.getXExpander(),
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
            BILoggerFactory.getLogger(GroupExecutor.class).info("data existed in caches,get data from caches");
            return widgetCache.getData();
        }
        JSONObject jo = getCubeNode().toJSONObject(usedDimensions, widget.getTargetsKey(), -1);
        if (isUseWidgetDataCache()) {
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
            if (!targetKey.equals(linkTargetTable)) {
                return null;
            }
            List<Object> col = getLinkRowData(clicked, target, true);
            BIDimension[] dimensions = getUserDimension(clicked, target);
            Node linkNode = getStopOnRowNode(col.toArray(), dimensions);
            // 总汇总值
            if (col.isEmpty()) {
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

    public BIGroupNode getResult() throws Exception {

        return getCubeNode();
    }
}
