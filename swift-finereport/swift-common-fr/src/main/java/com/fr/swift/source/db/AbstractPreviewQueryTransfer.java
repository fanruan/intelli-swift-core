package com.fr.swift.source.db;

import com.fr.data.core.db.ColumnInformation;
import com.fr.data.core.db.dialect.Dialect;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.ColumnTypeConstants.ColumnType;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.db.dbdealer.DBDealer;
import com.fr.swift.source.resultset.JdbcResultSet;
import com.fr.swift.source.resultset.LimitedResultSet;
import com.fr.swift.util.Strings;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pony
 * @date 2017/12/5
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
        } catch (Exception e) {
            LOGGER.error("not support setMaxRow");
        }
        return statement;
    }


    @Override
    public SwiftResultSet createIterator(final ResultSet rs, Dialect dialect, String sql, Statement stmt, Connection conn, boolean needCharSetConvert, String originalCharSetName, String newCharSetName) throws SQLException {
        ColumnInformation[] columns = dialect.getColumnInformation(conn, rs, sql, originalCharSetName, newCharSetName);
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        List<SwiftMetaDataColumn> outerColumnList = new ArrayList<SwiftMetaDataColumn>();
        for (ColumnInformation columnInfo : columns) {
            SwiftMetaDataColumn outerColumn = new MetaDataColumnBean(columnInfo.getColumnName(), columnInfo.getColumnType(), columnInfo.getColumnSize(), columnInfo.getScale());
            if (fieldClassTypes == null || fieldClassTypes.isEmpty()) {
                columnList.add(outerColumn);
            } else if (fieldClassTypes.containsKey(outerColumn.getName())) {
                SwiftMetaDataColumn column = outerColumn;
                ColumnType outerColumnType = ColumnTypeUtils.getColumnType(outerColumn);
                ColumnType columnType = fieldClassTypes.get(outerColumn.getName());
                if (outerColumnType != columnType) {
                    column = ColumnTypeUtils.convertColumn(columnType, outerColumn);
                }
                columnList.add(column);
            }
            outerColumnList.add(outerColumn);
        }
        SwiftMetaData metaData = new SwiftMetaDataBean(Strings.EMPTY, columnList);
        SwiftMetaData outerMeta = new SwiftMetaDataBean(Strings.EMPTY, outerColumnList);
        DBDealer[] dealers = createDBDealer(needCharSetConvert, originalCharSetName, newCharSetName, metaData, outerMeta);
        return new LimitedResultSet(new JdbcResultSet(rs, stmt, conn, metaData, dealers), row);
    }
}
