package com.fr.swift.adaptor.executor;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.service.engine.table.FineTableEngineExecutor;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftDetailCell;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftEmptyResult;
import com.fr.swift.adaptor.struct.SwiftRealDetailResult;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.adaptor.transformer.IndexingDataSourceFactory;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-26 14:16:39
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftTableEngineExecutor implements FineTableEngineExecutor {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(SwiftTableEngineExecutor.class);

    @Override
    public BIDetailTableResult getPreviewData(FineBusinessTable table, int rowCount) throws Exception {
        DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        if (dataSource != null) {
            SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourcePreviewTransfer(dataSource, rowCount);
            SwiftResultSet swiftResultSet = transfer.createResultSet();
            BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
            return detailTableResult;
        }
        return new SwiftDetailTableResult(new SwiftEmptyResult());
    }

    @Override
    public BIDetailTableResult getRealData(FineBusinessTable table) throws Exception {
        return this.getRealData(table, 100);
    }

    @Override
    public BIDetailTableResult getRealData(FineBusinessTable table, int rowCount) throws Exception {
        DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        List<List<BIDetailCell>> dataList = new ArrayList<List<BIDetailCell>>();
        for (Segment segment : segments) {
            List<DetailColumn> columnList = new ArrayList<DetailColumn>();
            int count = segment.getRowCount();
            for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
                String columnName = swiftMetaData.getColumnName(i);
                ColumnKey columnKey = new ColumnKey(columnName);
                columnList.add(segment.getColumn(columnKey).getPrimitiveDetailColumn());
            }
            for (int i = 0; i < count; i++) {
                List<BIDetailCell> cellList = new ArrayList<BIDetailCell>();
                for (int j = 0; j < swiftMetaData.getColumnCount(); j++) {
                    BIDetailCell cell = new SwiftDetailCell(columnList.get(j).get(i));
                    cellList.add(cell);
                }
                dataList.add(cellList);
            }
        }
        BIDetailTableResult realDetailResult = new SwiftRealDetailResult(dataList.iterator(), dataList.size(), swiftMetaData.getColumnCount());
        return realDetailResult;
    }

    @Override
    public List<FineBusinessField> getFieldList(FineBusinessTable table) throws Exception {
        DataSource dataSource = IndexingDataSourceFactory.transformDataSource(table);
        if (dataSource == null) {
            return new ArrayList<FineBusinessField>();
        }
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        return FieldFactory.transformColumns2Fields(swiftMetaData);
    }

    @Override
    public FineAnalysisTable getBasicOperator(FineAnalysisTable table) throws Exception {
        return null;
    }

    @Override
    public boolean isAvailable(FineResourceItem item) {
        return false;
    }

    @Override

    public String getName(FineResourceItem item) {
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    @Override
    public List<List<String>> getFloors(FineAnalysisTable table) throws Exception {
        return null;
    }
}
