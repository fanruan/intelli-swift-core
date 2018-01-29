package com.fr.swift.adaptor.executor;

import com.fr.third.springframework.beans.factory.annotation.Qualifier;

/**
 * This class created on 2018-1-2 11:22:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@Qualifier()
//@Service("fineDataModel")
public class SwiftSQLEngineExecutor extends AbstractSwiftTableEngineExecutor {

//    @Override
//    public BIDetailTableResult getPreviewData(FineBusinessTable table, int rowCount) throws Exception {
//        FineSQLBusinessTable sqlTable = (FineSQLBusinessTable) table;
//        FineConnection connection = FineConnectionUtils.getConnectionByName(sqlTable.getConnectionName());
//        ConnectionManager.getInstance().registerConnectionInfo(sqlTable.getConnectionName(),
//                new SwiftConnectionInfo(connection.getSchema(), connection.getConnection()));
//        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createDBSourcePreviewTransfer(sqlTable.getConnectionName(), sqlTable.getSql(), rowCount);
//        SwiftResultSet swiftResultSet = transfer.createResultSet();
//        BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
//        return detailTableResult;
//    }
//
//
//    @Override
//    public List<FineBusinessField> getFieldList(FineBusinessTable table) throws Exception {
//        FineSQLBusinessTable sqlTable = (FineSQLBusinessTable) table;
//        FineConnection connection = FineConnectionUtils.getConnectionByName(sqlTable.getConnectionName());
//        DataSource dataSource = DataSourceFactory.transformTableDBSource(sqlTable.getConnectionName(), sqlTable.getSql(), connection.getSchema(), connection.getConnection());
//        SwiftMetaData swiftMetaData = dataSource.getMetadata();
//        return FieldFactory.transformColumns2Fields(swiftMetaData);
//    }

}
