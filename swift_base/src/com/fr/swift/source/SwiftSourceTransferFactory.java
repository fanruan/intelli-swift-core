package com.fr.swift.source;

import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.QuerySourceTransfer;
import com.fr.swift.source.db.ServerDBSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TableDBSourceTransfer;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLSource;
import com.fr.swift.source.etl.ETLTransferFactory;
import com.fr.swift.source.excel.ExcelDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2017/11/22.
 */
public class SwiftSourceTransferFactory {
    public static SwiftSourceTransfer createSourceTransfer(DataSource dataSource) {
        SwiftSourceTransfer transfer = null;
        if (dataSource instanceof TableDBSource) {
            transfer = new TableDBSourceTransfer(ConnectionManager.getInstance().getConnectionInfo(((TableDBSource) dataSource).getConnectionName())
                    , dataSource.getMetadata(), ((TableDBSource) dataSource).getOuterMetadata(), ((TableDBSource) dataSource).getDBTableName());
        } else if (dataSource instanceof QueryDBSource) {
            transfer = new QuerySourceTransfer(ConnectionManager.getInstance().getConnectionInfo(((QueryDBSource) dataSource).getConnectionName())
                    , dataSource.getMetadata(), ((QueryDBSource) dataSource).getOuterMetadata(), ((QueryDBSource) dataSource).getQuery());
        } else if (dataSource instanceof ServerDBSource) {

        } else if (dataSource instanceof ExcelDataSource) {

        } else if (dataSource instanceof ETLSource) {
            transfer = ETLTransferFactory.createTransfer((ETLSource) dataSource);
        }
        return transfer;
    }

//    public static SwiftSourceTransfer createDBSourcePreviewTransfer(String connectionName, String tableName, int rowCount) {
//        ConnectionInfo connectionInfo = ConnectionManager.getInstance().getConnectionInfo(connectionName);
//        SwiftSourceTransfer transfer = new TableDBSourcePreviewTransfer(connectionInfo, rowCount, tableName);
//        return transfer;
//    }
//
//    public static SwiftSourceTransfer createSQLSourcePreviewTransfer(String connectionName, int rowCount, String sql) {
//        ConnectionInfo connectionInfo = ConnectionManager.getInstance().getConnectionInfo(connectionName);
//        SwiftSourceTransfer transfer = new QuerySourcePreviewTransfer(connectionInfo, rowCount, sql);
//        return transfer;
//    }
//
//    public static SwiftSourceTransfer createEXCELSourcePreviewTransfer(List<String> fileNames, SwiftMetaData metaData, SwiftMetaData outerMetaData) {
//        SwiftSourceTransfer transfer = new ExcelTransfer(fileNames, metaData, outerMetaData);
//        return transfer;
//    }

    private static SwiftMetaData getOrCreateETLTable(SwiftMetaData metaData, ETLSource source) {
        if (metaData == null) {
            ETLOperator operator = source.getOperator();
            List<DataSource> parentSource = source.getBasedSources();
            List<SwiftMetaData> list = new ArrayList<SwiftMetaData>();
            for (DataSource etlSource : parentSource) {
                SwiftMetaData parentMetaData = getOrCreateETLTable(metaData, (ETLSource) etlSource);
                if (parentMetaData == null) {
                    throw new RuntimeException();
                }
                list.add(parentMetaData);
            }
            List<SwiftMetaDataColumn> columnList = operator.getColumns(list.toArray(new SwiftMetaData[list.size()]));
            return new SwiftMetaDataImpl(source.getSourceKey().getId(), columnList);
        }
        return metaData;
    }
}
