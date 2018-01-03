package com.fr.swift.adaptor.model;

import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.field.FineBusinessFieldImp;
import com.finebi.conf.internalimp.table.dataModel.FineDataModel;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.fr.data.impl.Connection;
import com.fr.json.JSONObject;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.SwiftConnectionInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-2 11:22:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftDataModel implements FineDataModel {

    @Override
    public BIDetailTableResult getPreviewData(String connectionName, String tableName, int rowCount, String schema, Connection connection) throws Exception {
        ConnectionManager.getInstance().registerConnectionInfo(connectionName,
                new SwiftConnectionInfo(schema, connection));
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createSourcePreviewTransfer(connectionName, tableName, rowCount);
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
        List<FineBusinessField> fineBusinessFieldList = new ArrayList<FineBusinessField>();
        for (int i = 1; i <= swiftMetaData.getColumnCount(); i++) {
            String columnName = swiftMetaData.getColumnName(i);
            int columnType = swiftMetaData.getColumnType(i);
            String columnRemark = swiftMetaData.getColumnRemark(i);
            int precision = swiftMetaData.getPrecision(i);
            int scale = swiftMetaData.getScale(i);
            FineBusinessField fineBusinessField = new FineBusinessFieldImp(columnName, ColumnTypeUtils.sqlTypeToClassType(columnType, precision, scale), precision, columnRemark, FineEngineType.Cube.getEngineType());
            fineBusinessFieldList.add(fineBusinessField);
        }
        return fineBusinessFieldList;
    }

    @Override
    public boolean ischanged() {
        return false;
    }

    @Override
    public void remove(JSONObject dataJson, boolean isUsed) {

    }

    @Override
    public int getEngineType() {
        return FineEngineType.Cube.getEngineType();
    }
}
