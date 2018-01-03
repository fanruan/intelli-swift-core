package com.fr.swift.adaptor.model;

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

import java.util.List;

/**
 * This class created on 2018-1-3 10:00:54
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftSQLDataModel {

    public BIDetailTableResult getPreviewData(String connectionName, String sql, int rowCount, String schema, Connection connection) throws Exception {
        ConnectionManager.getInstance().registerConnectionInfo(connectionName,
                new SwiftConnectionInfo(schema, connection));
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSQLSourcePreviewTransfer(connectionName, rowCount, sql);
        SwiftResultSet swiftResultSet = transfer.createResultSet();
        BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
        return detailTableResult;
    }

    public List<FineBusinessField> getFieldList(String connectionName, String sql, String schema, Connection connection) throws Exception {
        DataSource dataSource = DataSourceFactory.transformQueryDBSource(connectionName, sql, schema, connection);
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        return FieldFactory.transformColumns2Fields(swiftMetaData);
    }

}
