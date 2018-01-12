package com.fr.swift.adaptor.transformer;

import com.finebi.conf.internalimp.table.FineDBBusinessTable;
import com.finebi.conf.internalimp.update.TableUpdateInfo;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.data.impl.Connection;
import com.fr.swift.increase.Increment;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.db.ConnectionInfo;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.excel.ExcelDataSource;

import java.util.List;

/**
 * This class created on 2017-12-19 09:12:11
 *
 * @author Lucifer
 * @description FineCubeTable-->DataSource
 * @since Advanced FineBI Analysis 1.0
 */
public class DataSourceFactory {


    public static TableDBSource transformTableDBSource(String connectionName, String dbTableName, String schema, Connection connection) {
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, new SwiftConnectionInfo(schema, connection));
        TableDBSource tableDBSource = new TableDBSource(dbTableName, connectionName);
        return tableDBSource;
    }

    public static QueryDBSource transformQueryDBSource(String connectionName, String sql, String schema, Connection connection) {
        ConnectionManager.getInstance().registerConnectionInfo(connectionName, new SwiftConnectionInfo(schema, connection));
        QueryDBSource queryDBSource = new QueryDBSource(sql, connectionName);
        return queryDBSource;
    }

    public static ExcelDataSource transformExcelDataSource(String path, String[] names, int[] types, List<String> appendedFileNames) {
        ExcelDataSource excelDataSource = new ExcelDataSource(path, names, types, appendedFileNames);
        return excelDataSource;
    }
}

