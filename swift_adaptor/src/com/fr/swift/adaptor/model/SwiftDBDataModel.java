package com.fr.swift.adaptor.model;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.table.dataModel.FineDBEngineExecutor;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.data.impl.Connection;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.third.springframework.stereotype.Service;

import java.util.List;

/**
 * This class created on 2018-1-2 11:22:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@Service("fineDataModel")
public class SwiftDBDataModel implements FineDBEngineExecutor {

    @Override
    public BIDetailTableResult getPreviewData(String connectionName, String tableName, int rowCount, String schema, Connection connection) throws Exception {
        ConnectionManager.getInstance().registerConnectionInfo(connectionName,
                new SwiftConnectionInfo(schema, connection));
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createDBSourcePreviewTransfer(connectionName, tableName, rowCount);
        SwiftResultSet swiftResultSet = transfer.createResultSet();
        BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
        return detailTableResult;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public List<FineBusinessField> getFieldList(String connectionName, String dbTableName, String schema, Connection connection) throws Exception {
        DataSource dataSource = DataSourceFactory.transformTableDBSource(connectionName, dbTableName, schema, connection);
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        return FieldFactory.transformColumns2Fields(swiftMetaData);
    }

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }
}
