package com.fr.swift.adaptor.widget;

import com.finebi.conf.algorithm.common.DMUtils;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.structure.result.table.BICrossTableResult;
import com.fr.swift.adaptor.struct.node.BICrossNodeAdaptor;
import com.fr.swift.adaptor.struct.node.GroupNode2XLeftNodeAdaptor;
import com.fr.swift.adaptor.widget.datamining.CrossTableToDMResultVisitor;
import com.fr.swift.adaptor.widget.datamining.DMErrorWrap;
import com.fr.swift.adaptor.widget.expander.ExpanderFactory;
import com.fr.swift.adaptor.widget.target.TargetInfoUtils;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.query.aggregator.AggregatorValue;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.group.info.cursor.AllCursor;
import com.fr.swift.query.group.info.cursor.Expander;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.info.element.dimension.DimensionInfo;
import com.fr.swift.query.info.element.dimension.DimensionInfoImpl;
import com.fr.swift.query.info.element.target.TargetInfo;
import com.fr.swift.query.info.group.GroupQueryInfo;
import com.fr.swift.query.info.group.XGroupQueryInfo;
import com.fr.swift.query.query.QueryRunnerProvider;
import com.fr.swift.result.GroupNode;
import com.fr.swift.result.NodeMergeResultSet;
import com.fr.swift.result.NodeResultSet;
import com.fr.swift.result.TopGroupNode;
import com.fr.swift.result.XLeftNode;
import com.fr.swift.result.XNodeMergeResultSet;
import com.fr.swift.result.node.iterator.PostOrderNodeIterator;
import com.fr.swift.result.node.xnode.XGroupNodeImpl;
import com.fr.swift.result.node.xnode.XNodeUtils;
import com.fr.swift.source.SourceKey;
import com.fr.swift.structure.iterator.IteratorUtils;
import com.fr.swift.structure.iterator.MapperIterator;
import com.fr.swift.util.function.Function;

import java.util.Iterator;
import java.util.List;

/**
 * @author anchore
 * @date 2018/3/6
 * 交叉表
 */
public class CrossTableWidgetAdaptor extends AbstractTableWidgetAdaptor {


    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(CrossTableWidgetAdaptor.class);

    public static BICrossTableResult calculate(CrossTableWidget widget) {
        BICrossNode crossNode = null;
        XNodeMergeResultSet resultSet = null;
        DMErrorWrap dmErrorWrap = new DMErrorWrap();
        try {
            TargetInfo targetInfo = TargetInfoUtils.parse(widget);
            XGroupQueryInfo queryInfo = buildQueryInfo(widget, targetInfo);
//            if (queryInfo.getColDimensionInfo().getDimensions().length == 0) {
                // 列表头为空
//                GroupQueryInfoImpl groupQueryInfo = new GroupQueryInfoImpl(queryInfo.getQueryId(), queryInfo.getTable(),
//                        queryInfo.getDimensionInfo(), queryInfo.getTargetInfo());
            GroupQueryInfo groupQueryInfo = null;
                NodeMergeResultSet result = (NodeMergeResultSet) QueryRunnerProvider.getInstance().executeQuery(groupQueryInfo);

                // 添加挖掘相关
                result = processDataMining(result, widget, queryInfo, dmErrorWrap);

                crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl(new GroupNode2XLeftNodeAdaptor((GroupNode) result.getNode()), new GroupNode()));
//            } else if (queryInfo.getDimensionInfo().getDimensions().length == 0) {
//                // 行表头为空
//                GroupQueryInfoImpl groupQueryInfo = new GroupQueryInfoImpl(queryInfo.getQueryId(), queryInfo.getTable(),
//                        queryInfo.getColDimensionInfo(), queryInfo.getTargetInfo());
//                NodeMergeResultSet result = (NodeMergeResultSet) QueryRunnerProvider.getInstance().executeQuery(groupQueryInfo);
//
//                // 添加挖掘相关
//                result = processDataMining(result, widget, queryInfo, dmErrorWrap);
//
//                GroupNode groupNode = (GroupNode) result.getNode();
//                XLeftNode xLeftNode = topGroupNode2XLeftNode(queryInfo.getColDimensionInfo().isShowSum(),
//                        groupQueryInfo.getDimensionInfo().getDimensions().length, groupNode);
//                crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl(xLeftNode, groupNode));
//            } else {
                // 行列表头都不为空
//                resultSet = (XNodeMergeResultSet) QueryRunnerProvider.getInstance().executeQuery(queryInfo);
//
//                // 添加挖掘相关
//                resultSet = processDataMining(resultSet, widget, queryInfo, dmErrorWrap);
//
//                crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl((XLeftNode) resultSet.getNode(), resultSet.getTopGroupNode()));
//            }
        } catch (Exception e) {
            crossNode = new BICrossNodeAdaptor(new XGroupNodeImpl(new XLeftNode(-1, null), new TopGroupNode(-1, null)));
            LOGGER.error(e);
        }
        return new CrossTableResult(crossNode, false, false, false, false, dmErrorWrap);
    }

    private static <T extends NodeResultSet> T processDataMining(T result, CrossTableWidget widget, XGroupQueryInfo info, DMErrorWrap dmErrorWrap) throws Exception {
        // 挖掘模块处理
        AlgorithmBean dmBean = widget.getValue().getDataMining();
        T resultSet = result;
        if (!DMUtils.isEmptyAlgorithm(dmBean)) {
            CrossTableToDMResultVisitor crossVisitor = new CrossTableToDMResultVisitor(result, widget, info, dmErrorWrap);
            resultSet = (T) dmBean.accept(crossVisitor);
        }
        return resultSet;
    }

    private static XLeftNode topGroupNode2XLeftNode(boolean isShowColSum, int dimensionSize, GroupNode node) {
        Iterator<GroupNode> iterator = new PostOrderNodeIterator<GroupNode>(dimensionSize, node);
        if (isShowColSum) {
            iterator = XNodeUtils.excludeNoShowSummaryRow(iterator);
        } else {
            iterator = XNodeUtils.excludeAllSummaryRow(iterator);
        }
        List<AggregatorValue[]> values = IteratorUtils.iterator2List(new MapperIterator<GroupNode, AggregatorValue[]>(iterator, new Function<GroupNode, AggregatorValue[]>() {
            @Override
            public AggregatorValue[] apply(GroupNode p) {
                return p.getAggregatorValue();
            }
        }));
        XLeftNode xLeftNode = new XLeftNode();
        xLeftNode.setXValues(values);
        return xLeftNode;
    }

    private static XGroupQueryInfo buildQueryInfo(CrossTableWidget widget, TargetInfo targetInfo) throws Exception {
        String queryId = widget.getWidgetId();
        SourceKey sourceKey = getSourceKey(widget);
        List<Dimension> rowDimensions = TableWidgetAdaptor.getDimensions(sourceKey, widget.getDimensionList(),
                TableWidgetAdaptor.getTargetIndexPair(widget.getTargetList(), targetInfo.getTargetsForShowList()));
        List<Dimension> colDimensions = TableWidgetAdaptor.getDimensions(sourceKey, widget.getColDimensionList(),
                TableWidgetAdaptor.getTargetIndexPair(widget.getTargetList(), targetInfo.getTargetsForShowList()));
        Expander rowExpander = ExpanderFactory.createRowExpander(widget.getValue(), widget.getDimensionList());
        Expander colExpander = ExpanderFactory.createColExpander(widget.getValue(), widget.getColDimensionList());
        FilterInfo rowFilterInfo = TableWidgetAdaptor.getFilterInfo(widget, rowDimensions);
        FilterInfo colFilterInfo = TableWidgetAdaptor.getFilterInfo(widget, colDimensions);
        DimensionInfo rowDimensionInfo = new DimensionInfoImpl(new AllCursor(), rowFilterInfo, rowExpander, rowDimensions.toArray(new Dimension[rowDimensions.size()]));
        DimensionInfo colDimensionInfo = new DimensionInfoImpl(widget.isShowColSum(), new AllCursor(), colFilterInfo, colExpander, colDimensions.toArray(new Dimension[colDimensions.size()]));
        return new XGroupQueryInfo(queryId, sourceKey, rowDimensionInfo, colDimensionInfo, targetInfo);
    }

    private static class CrossTableResult implements BICrossTableResult {
        private BICrossNode node;
        private boolean hasHorizontalNextPage;
        private boolean hasHorizontalPreviousPage;
        private boolean hasVerticalNextPage;
        private boolean hasVerticalPreviousPage;
        private DMErrorWrap errorWrap;

        public CrossTableResult(BICrossNode node, boolean hasHorizontalNextPage, boolean hasHorizontalPreviousPage, boolean hasVerticalNextPage, boolean hasVerticalPreviousPage, DMErrorWrap errorWrap) {
            this.node = node;
            this.hasHorizontalNextPage = hasHorizontalNextPage;
            this.hasHorizontalPreviousPage = hasHorizontalPreviousPage;
            this.hasVerticalNextPage = hasVerticalNextPage;
            this.hasVerticalPreviousPage = hasVerticalPreviousPage;
            this.errorWrap = errorWrap;
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

        @Override
        public String getDataMiningError() {
            return null;
        }
    }
}