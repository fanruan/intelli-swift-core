package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BICrossTableResult;
import com.fr.swift.adaptor.struct.node.BICrossNodeAdaptor;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.cal.result.group.Cursor;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.metric.Metric;
import com.fr.swift.query.adapter.target.GroupTarget;
import com.fr.swift.query.adapter.target.cal.TargetInfo;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.XGroupByResultSet;
import com.fr.swift.result.node.xnode.TopGroupNode;
import com.fr.swift.result.node.xnode.XGroupNode;
import com.fr.swift.result.node.xnode.XGroupNodeFactory;
import com.fr.swift.result.node.xnode.XGroupNodeImpl;
import com.fr.swift.result.node.xnode.XLeftNode;
import com.fr.swift.service.QueryRunnerProvider;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/6
 * 交叉表
 */
public class CrossTableWidgetAdaptor extends AbstractTableWidgetAdaptor{


    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(CrossTableWidgetAdaptor.class);

    public static BICrossTableResult calculate(CrossTableWidget widget) {
        BICrossNode crossNode = null;
        XGroupByResultSet resultSet = null;
        try {
            TargetInfo targetInfo = CalTargetParseUtils.parseCalTarget(widget);
            QueryInfo queryInfo = buildQueryInfo(widget, targetInfo.getMetrics());
            resultSet = (XGroupByResultSet) QueryRunnerProvider.getInstance().executeQuery(queryInfo);
            // 同时处理交叉表的计算指标
            XGroupNode xGroupNode = XGroupNodeFactory.createXGroupNode(resultSet, targetInfo);
            crossNode = new BICrossNodeAdaptor(xGroupNode);
        } catch (Exception e) {
            crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl(new XLeftNode(-1, null), new TopGroupNode(-1, null)));
            LOGGER.error(e);
        }
        return new CrossTableResult(crossNode, false, false, false, false);
    }

    private static class CrossTableResult implements BICrossTableResult {
        private BICrossNode node;
        private boolean hasHorizontalNextPage;
        private boolean hasHorizontalPreviousPage;
        private boolean hasVerticalNextPage;
        private boolean hasVerticalPreviousPage;
        public CrossTableResult(BICrossNode node, boolean hasHorizontalNextPage, boolean hasHorizontalPreviousPage, boolean hasVerticalNextPage, boolean hasVerticalPreviousPage) {
            this.node = node;
            this.hasHorizontalNextPage = hasHorizontalNextPage;
            this.hasHorizontalPreviousPage = hasHorizontalPreviousPage;
            this.hasVerticalNextPage = hasVerticalNextPage;
            this.hasVerticalPreviousPage = hasVerticalPreviousPage;
        }

        @Override
        public BICrossNode getNode() {
            return node;
        }

        @Override
        public boolean hasHorizontalNextPage() {
            return hasHorizontalNextPage;
        }

        @Override
        public boolean hasHorizontalPreviousPage() {
            return hasHorizontalPreviousPage;
        }

        @Override
        public boolean hasVerticalNextPage() {
            return hasVerticalNextPage;
        }

        @Override
        public boolean hasVerticalPreviousPage() {
            return hasVerticalPreviousPage;
        }

        @Override
        public ResultType getResultType() {
            return ResultType.BICROSS;
        }
    }


    private static QueryInfo buildQueryInfo(CrossTableWidget widget, List<Metric> metrics) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());
        SourceKey sourceKey = getSourceKey(widget);
        List<Dimension> rowDimensions = TableWidgetAdaptor.getDimensions(sourceKey, widget.getDimensionList(), widget.getTargetList());
        List<Dimension> colDimensions = TableWidgetAdaptor.getDimensions(sourceKey, widget.getColDimensionList(), widget.getTargetList());

        GroupTarget[] targets = TableWidgetAdaptor.getTargets(widget);
        Expander expander = null;
        return new XGroupQueryInfo(cursor, queryId, sourceKey, filterInfo,
                rowDimensions.toArray(new Dimension[rowDimensions.size()]),
                colDimensions.toArray(new Dimension[colDimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander);
    }
}