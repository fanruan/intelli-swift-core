package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BICrossTableResult;
import com.fr.swift.adaptor.struct.node.BICrossNodeAdaptor;
import com.fr.swift.adaptor.struct.node.GroupNode2XLeftNodeAdaptor;
import com.fr.swift.adaptor.widget.expander.ExpanderFactory;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.cal.info.GroupQueryInfo;
import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.adapter.dimension.AllCursor;
import com.fr.swift.query.adapter.dimension.Dimension;
import com.fr.swift.query.adapter.dimension.DimensionInfo;
import com.fr.swift.query.adapter.dimension.DimensionInfoImpl;
import com.fr.swift.query.adapter.dimension.Expander;
import com.fr.swift.query.adapter.target.TargetInfo;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.result.node.xnode.XGroupNodeImpl;
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
        XNodeMergeResultSet resultSet = null;
        try {
            TargetInfo targetInfo = CalTargetParseUtils.parseCalTarget(widget);
            XGroupQueryInfo queryInfo = buildQueryInfo(widget, targetInfo);
            if (queryInfo.getColDimensionInfo().getDimensions().length == 0) {
                // 列表头为空
                GroupQueryInfo groupQueryInfo = new GroupQueryInfo(queryInfo.getQueryId(), queryInfo.getTable(),
                        queryInfo.getDimensionInfo(), queryInfo.getTargetInfo());
                NodeMergeResultSet result = (NodeMergeResultSet) QueryRunnerProvider.getInstance().executeQuery(groupQueryInfo);
                crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl(new GroupNode2XLeftNodeAdaptor((GroupNode) result.getNode()), new GroupNode()));
            } else if (queryInfo.getDimensionInfo().getDimensions().length == 0) {
                // 行表头为空
                GroupQueryInfo groupQueryInfo = new GroupQueryInfo(queryInfo.getQueryId(), queryInfo.getTable(),
                        queryInfo.getColDimensionInfo(), queryInfo.getTargetInfo());
                NodeMergeResultSet result = (NodeMergeResultSet) QueryRunnerProvider.getInstance().executeQuery(groupQueryInfo);
                crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl(new XLeftNode(), (GroupNode) result.getNode()));
            } else {
                // 行列表头都不为空
                resultSet = (XNodeMergeResultSet) QueryRunnerProvider.getInstance().executeQuery(queryInfo);
                crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl((XLeftNode) resultSet.getNode(), resultSet.getTopGroupNode()));
            }
        } catch (Exception e) {
            crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl(new XLeftNode(-1, null), new TopGroupNode(-1, null)));
            LOGGER.error(e);
        }
        return new CrossTableResult(crossNode, false, false, false, false);
    }

    private static XGroupQueryInfo buildQueryInfo(CrossTableWidget widget, TargetInfo targetInfo) throws Exception {
        String queryId = widget.getWidgetId();
        SourceKey sourceKey = getSourceKey(widget);
        List<Dimension> rowDimensions = TableWidgetAdaptor.getDimensions(sourceKey, widget.getDimensionList(), widget.getTargetList());
        List<Dimension> colDimensions = TableWidgetAdaptor.getDimensions(sourceKey, widget.getColDimensionList(), widget.getTargetList());
        Expander rowExpander = ExpanderFactory.create(true, widget.getDimensionList(),
                widget.getValue().getRowExpand(), null);
        Expander colExpander = ExpanderFactory.create(true, widget.getColDimensionList(),
                widget.getValue().getColExpand(), null);
        FilterInfo rowFilterInfo = TableWidgetAdaptor.getFilterInfo(widget, rowDimensions);
        FilterInfo colFilterInfo = TableWidgetAdaptor.getFilterInfo(widget, colDimensions);
        DimensionInfo rowDimensionInfo = new DimensionInfoImpl(new AllCursor(), rowFilterInfo, rowExpander, rowDimensions.toArray(new Dimension[rowDimensions.size()]));
        DimensionInfo colDimensionInfo = new DimensionInfoImpl(new AllCursor(), colFilterInfo, colExpander, colDimensions.toArray(new Dimension[colDimensions.size()]));
        return new XGroupQueryInfo(queryId, sourceKey, rowDimensionInfo, colDimensionInfo, targetInfo);
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
}