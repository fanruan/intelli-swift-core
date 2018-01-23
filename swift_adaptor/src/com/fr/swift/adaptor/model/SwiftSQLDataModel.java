package com.fr.swift.adaptor.model;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.basictable.table.FineSQLBusinessTable;
import com.finebi.conf.internalimp.service.engine.table.FineTableEngineExecutor;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.utils.FineConnectionUtils;
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
import com.fr.third.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * This class created on 2018-1-2 11:22:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
@Qualifier()
//@Service("fineSQLDataModel")
public class SwiftSQLDataModel implements FineTableEngineExecutor {

    @Override
    public FineEngineType getEngineType() {
        return FineEngineType.Cube;
    }

    @Override
    public BIDetailTableResult getPreviewData(FineBusinessTable table, int rowCount) throws Exception {
        FineSQLBusinessTable sqlTable = (FineSQLBusinessTable) table;
        FineConnection connection = FineConnectionUtils.getConnectionByName(sqlTable.getConnectionName());
        ConnectionManager.getInstance().registerConnectionInfo(sqlTable.getConnectionName(),
                new SwiftConnectionInfo(connection.getSchema(), connection.getConnection()));
        SwiftSourceTransfer transfer = SwiftSourceTransferFactory.createDBSourcePreviewTransfer(sqlTable.getConnectionName(), sqlTable.getSql(), rowCount);
        SwiftResultSet swiftResultSet = transfer.createResultSet();
        BIDetailTableResult detailTableResult = new SwiftDetailTableResult(swiftResultSet);
        return detailTableResult;
    }

    @Override
    public BIDetailTableResult getRealData(FineBusinessTable table, int rowCount) throws Exception {
        return null;
    }

    @Override
    public List<FineBusinessField> getFieldList(FineBusinessTable table) throws Exception {
        FineSQLBusinessTable sqlTable = (FineSQLBusinessTable) table;
        FineConnection connection = FineConnectionUtils.getConnectionByName(sqlTable.getConnectionName());
        DataSource dataSource = DataSourceFactory.transformTableDBSource(sqlTable.getConnectionName(), sqlTable.getSql(), connection.getSchema(), connection.getConnection());
        SwiftMetaData swiftMetaData = dataSource.getMetadata();
        return FieldFactory.transformColumns2Fields(swiftMetaData);
    }

    @Override
    public boolean isAvailable(FineResourceItem item) {
        return false;
    }

    @Override
    public String getName(FineResourceItem item) {
        return null;
    }
}
