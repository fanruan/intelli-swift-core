package com.fr.swift.adaptor.transformer;

import com.finebi.base.constant.BaseConstant;
import com.finebi.conf.internalimp.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.table.FineExcelBusinessTable;
import com.finebi.conf.internalimp.table.FineSQLBusinessTable;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.container.SourceContainer;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.excel.ExcelDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2018-1-12 15:49:48
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class IndexingDataSourceFactory {


    public static void transformDataSources(Map<FineBusinessTable, TableUpdateInfo> infoMap, List<String> updateTableSourceKeys, SourceContainer updateSourceContainer) throws Exception {
        if (infoMap != null) {
            for (Map.Entry<FineBusinessTable, TableUpdateInfo> infoEntry : infoMap.entrySet()) {
                DataSource updateDataSource = transformDataSource(infoEntry.getKey());
                if (updateDataSource != null) {
                    updateTableSourceKeys.add(updateDataSource.getSourceKey().getId());
                    updateSourceContainer.getDataSourceContainer().addSource(updateDataSource);
                }
            }
        }
    }

    public static List<DataSource> transformDataSources(List<FineBusinessTable> tables) throws Exception {
        List<DataSource> dataSourceList = new ArrayList<DataSource>();
        if (tables != null) {
            for (FineBusinessTable table : tables) {
                DataSource updateDataSource = transformDataSource(table);
                if (updateDataSource != null) {
                    dataSourceList.add(updateDataSource);
                }
            }
        }
        return dataSourceList;
    }

    public static DataSource transformDataSource(FineBusinessTable table) throws Exception {
        DataSource dataSource = null;
        switch (table.getType()) {
            case BaseConstant.TABLETYPE.DB:
                dataSource = transformTableDBSource((FineDBBusinessTable) table);
                break;
            case BaseConstant.TABLETYPE.SQL:
                dataSource = transformQueryDBSource((FineSQLBusinessTable) table);
                break;
            case BaseConstant.TABLETYPE.SERVER:
                break;
            case BaseConstant.TABLETYPE.EXCEL:
                dataSource = transformExcelDataSource((FineExcelBusinessTable) table);
                break;
            case BaseConstant.TABLETYPE.ETL:
                break;
            default:
        }
        return dataSource;
    }


    private static TableDBSource transformTableDBSource(FineDBBusinessTable table) throws Exception {
        String connectionName = table.getConnName();
        FineConnection fineConnection = FineConnectionUtils.getConnectionByName(connectionName);
        ConnectionInfo connectionInfo = new SwiftConnectionInfo(fineConnection.getSchema(), fineConnection.getConnection());
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, connectionInfo);
        TableDBSource tableDBSource = new TableDBSource(table.getTableName(), connectionName);
        return tableDBSource;
    }

    private static QueryDBSource transformQueryDBSource(FineSQLBusinessTable table) throws Exception {

        String connectionName = table.getConnectionName();
        FineConnection fineConnection = FineConnectionUtils.getConnectionByName(connectionName);
        ConnectionInfo connectionInfo = new SwiftConnectionInfo(fineConnection.getSchema(), fineConnection.getConnection());
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, connectionInfo);

        QueryDBSource queryDBSource = new QueryDBSource(table.getSql(), table.getConnectionName());
        return queryDBSource;
    }


    private static ExcelDataSource transformExcelDataSource(FineExcelBusinessTable table) {
        return null;
    }


//    public static Increment transformIncrement(TableUpdateInfo tableUpdateInfo, SourceKey sourceKey, String connectionName) {
//        Increment increment = new Increment(tableUpdateInfo.getAddSql(), tableUpdateInfo.getDeleteSql(), tableUpdateInfo.getModifySql(), sourceKey, connectionName);
//        return increment;
//    }
}
