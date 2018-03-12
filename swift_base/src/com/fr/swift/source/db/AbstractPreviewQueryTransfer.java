package com.fr.swift.source.db;

import com.fr.data.core.db.ColumnInformation;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.stable.StringUtils;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.SwiftMetaDataImpl;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.db.dbdealer.DBDealer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pony on 2017/12/5.
 */
public abstract class AbstractPreviewQueryTransfer extends AbstractQueryTransfer {
    protected Map<String, ColumnType> fieldClassTypes;
    private int row;

    public AbstractPreviewQueryTransfer(ConnectionInfo connectionInfo, int row) {
        super(connectionInfo);
        this.row = row;
    }

    public AbstractPreviewQueryTransfer(ConnectionInfo connectionInfo, Map<String, ColumnType> fieldClassTypes, int row) {
        super(connectionInfo);
        this.fieldClassTypes = fieldClassTypes;
        this.row = row;
    }

    @Override
    public Statement createStatement(Connection conn, Dialect dialect) throws SQLException {
        Statement statement = super.createStatement(conn, dialect);
        try {
            statement.setMaxRows(row);
        } catch (Exception e){
            LOGGER.error("not support setMaxRow");
        }
        return statement;
    }


    @Override
    public SwiftResultSet createIterator(final ResultSet rs, Dialect dialect, String sql, Statement stmt, Connection conn, boolean needCharSetConvert, String originalCharSetName, String newCharSetName) throws SQLException {
        ColumnInformation[] columns = dialect.getColumnInformation(conn, rs, sql, originalCharSetName, newCharSetName);
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        List<SwiftMetaDataColumn> outerColumnList = new ArrayList<SwiftMetaDataColumn>();
        for (int i = 0, cols = columns.length; i < cols; i++) {
            SwiftMetaDataColumn outerColumn = new MetaDataColumn(columns[i].getColumnName(), columns[i].getColumnType(), columns[i].getColumnSize(), columns[i].getScale());
            if (fieldClassTypes == null || fieldClassTypes.isEmpty()){
                columnList.add(outerColumn);
            } else if (fieldClassTypes.containsKey(outerColumn.getName())){
                SwiftMetaDataColumn column = outerColumn;
                ColumnType outerColumnType = ColumnTypeUtils.sqlTypeToColumnType(outerColumn.getType(), outerColumn.getPrecision(), outerColumn.getScale());
                ColumnType columnType = fieldClassTypes.get(outerColumn.getName());
                if (outerColumnType != columnType) {
                    column = ColumnTypeUtils.convertColumn(columnType, outerColumn);
                }
                columnList.add(column);
            }
            outerColumnList.add(outerColumn);
        }
        SwiftMetaData metaData = new SwiftMetaDataImpl(StringUtils.EMPTY, columnList);
        SwiftMetaData outerMeta = new SwiftMetaDataImpl(StringUtils.EMPTY, outerColumnList);
        DBDealer[] dealers = createDBDealer(needCharSetConvert, originalCharSetName, newCharSetName, metaData, outerMeta);
        return new JDBCRowLimitResultSet(rs, stmt, conn, metaData, dealers, row);
    }
}
