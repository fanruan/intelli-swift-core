package com.fr.swift.adaptor.widget;

import com.finebi.conf.internalimp.dashboard.widget.table.CrossTableWidget;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.table.BICrossNode;
import com.finebi.conf.utils.FineTableUtils;
import com.fr.swift.adaptor.struct.node.BICrossNodeAdaptor;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FilterInfoFactory;
import com.fr.swift.adaptor.widget.target.CalTargetParseUtils;
import com.fr.swift.cal.QueryInfo;
import com.fr.swift.cal.info.Expander;
import com.fr.swift.cal.info.TableGroupQueryInfo;
import com.fr.swift.cal.info.XGroupQueryInfo;
import com.fr.swift.cal.info.XTableGroupQueryInfo;
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
import com.fr.swift.source.DataSource;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/6
 * 交叉表
 */
public class CrossTableWidgetAdaptor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(CrossTableWidgetAdaptor.class);

    public static BICrossNode calculate(CrossTableWidget widget) {
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
        return crossNode;
    }

    static QueryInfo buildQueryInfo(CrossTableWidget widget, List<Metric> metrics) throws Exception {
        Cursor cursor = null;
        String queryId = widget.getWidgetId();
        FilterInfo filterInfo = FilterInfoFactory.transformFineFilter(widget.getFilters());

        List<Dimension> rowDimensions = TableWidgetAdaptor.getDimensions(widget.getDimensionList());
        List<Dimension> colDimensions = TableWidgetAdaptor.getDimensions(widget.getColDimensionList());

        GroupTarget[] targets = TableWidgetAdaptor.getTargets(widget);
        Expander expander = null;
        String fieldId = widget.getDimensionList().isEmpty() ? null : widget.getDimensionList().get(0).getFieldId();
        fieldId = fieldId == null ?
                widget.getTargetList().isEmpty() ? null : widget.getTargetList().get(0).getFieldId()
                : fieldId;
        FineBusinessTable fineBusinessTable = FineTableUtils.getTableByFieldId(fieldId);
        DataSource baseDataSource = DataSourceFactory.transformDataSource(fineBusinessTable);
        return new XGroupQueryInfo(cursor, queryId, baseDataSource.getSourceKey(), filterInfo,
                rowDimensions.toArray(new Dimension[rowDimensions.size()]),
                colDimensions.toArray(new Dimension[colDimensions.size()]),
                metrics.toArray(new Metric[metrics.size()]),
                targets, expander);
    }
}