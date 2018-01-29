package com.fr.swift.adaptor.executor;

import com.finebi.base.common.resource.FineResourceItem;
import com.finebi.base.constant.FineEngineType;
import com.finebi.conf.internalimp.basictable.table.FineDBBusinessTable;
import com.finebi.conf.structure.analysis.table.FineAnalysisTable;
import com.finebi.conf.structure.bean.connection.FineConnection;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.bean.table.FineBusinessTable;
import com.finebi.conf.structure.result.BIDetailCell;
import com.finebi.conf.structure.result.BIDetailTableResult;
import com.finebi.conf.utils.FineConnectionUtils;
import com.fr.swift.adaptor.struct.SwiftDetailCell;
import com.fr.swift.adaptor.struct.SwiftDetailTableResult;
import com.fr.swift.adaptor.struct.SwiftRealDetailResult;
import com.fr.swift.adaptor.transformer.DataSourceFactory;
import com.fr.swift.adaptor.transformer.FieldFactory;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DetailColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.SwiftSourceTransferFactory;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.SwiftConnectionInfo;
import com.fr.swift.source.db.TableDBSource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-2 11:22:19
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
//@Service("fineBDDataModel")
public class SwiftDBEngineExecutor extends AbstractSwiftTableEngineExecutor {

//


    //    public List<TableProcedure> getAllTables(Connection connection, String connectionName, String schema) {
//        TableProcedure[] tps = new TableProcedure[0];
//        TableProcedure[] views = new TableProcedure[0];
//        try {
//            if (schema != null && !schema.isEmpty()) {
//                String[] schemas = DataCoreUtils.getDatabaseSchema(connection);
//                if (StringUtils.isNotEmpty(schema) || schemas.length == 0) {
//                    TableProcedure[] sqlTables = DataCoreUtils.getTables(connection, TableProcedure.TABLE, schema, true);
//                    tps = ArrayUtils.addAll(tps, sqlTables);
//                    views = ArrayUtils.addAll(views, FRContext.getCurrentEnv().getTableProcedure(connection, TableProcedure.VIEW, schema));
//                }
//            } else {
//                tps = FRContext.getCurrentEnv().getTableProcedure(connection, TableProcedure.TABLE, null);
//                views = FRContext.getCurrentEnv().getTableProcedure(connection, TableProcedure.VIEW, null);
//            }
//            List<TableProcedure> result = duplicateRemove(tps, views);
//            return result;
//        } catch (Exception e) {
//            return new ArrayList<TableProcedure>();
//        }
//    }
//
//    private List<TableProcedure> duplicateRemove(TableProcedure[] tps, TableProcedure[] views) {
//        List<TableProcedure> procedureList = new ArrayList<TableProcedure>();
//        for (TableProcedure procedure : views) {
//            if (!existInTables(procedure, tps)) {
//                procedureList.add(procedure);
//            }
//        }
//        for (TableProcedure procedure : tps) {
//            procedureList.add(procedure);
//
//        }
//        return procedureList;
//    }
//
//    private boolean existInTables(TableProcedure procedure, TableProcedure[] sqlTables) {
//        for (TableProcedure table : sqlTables) {
//            if (ComparatorUtils.equals(procedure.getSchema(), table.getSchema()) && ComparatorUtils.equals(procedure.getName(), table.getName())) {
//                return true;
//            }
//        }
//        return false;
//    }
}
