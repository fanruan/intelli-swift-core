package com.fr.swift.adaptor.model;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.table.dataModel.FineSQLEngineExecutor;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.data.impl.Connection;
import com.fr.swift.adaptor.struct.SwiftDetailCell;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftRealDetailResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.segment.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.PrimitiveDetailColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;
import com.fr.third.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-3 10:00:54
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@Service("fineSQLaModel")
public class SwiftSQLEngineExecutor implements FineSQLEngineExecutor {

    public BIDetailTableResult getPreviewData(String connectionName, String sql, int rowCount, String schema, Connection connection) throws Exception {
        ConnectionManager.getInstance().registerConnectionInfo(connectionName,
                new SwiftConnectionInfo(schema, connection));
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSQLSourcePreviewTransfer(connectionName, rowCount, sql);
        SwiftResultSet swiftResultSet = transfer.createResultSet();
        BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
        return detailTableResult;
    }

    public BIDetailTableResult getPreviewRealData(String connectionName, String sql) throws Exception {
        DataSource dataSource = new QueryDBSource(sql, connectionName);
        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        List<List<BIDetailCell>> dataList = new ArrayList<List<BIDetailCell>>();
        for (Segment segment : segments) {
            List<PrimitiveDetailColumn> columnList = new ArrayList<>();
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

    public List<FineBusinessField> getFieldList(String connectionName, String sql, String schema, Connection connection) throws Exception {
        DataSource dataSource = DataSourceFactory.transformQueryDBSource(connectionName, sql, schema, connection);
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        return FieldFactory.transformColumns2Fields(swiftMetaData);
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
