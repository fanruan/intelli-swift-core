package com.fr.swift.source;

import com.fr.swift.annotation.SwiftService;
import com.fr.swift.beans.annotation.SwiftBean;
import com.fr.swift.exception.SegmentAbsentException;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.QuerySourceTransfer;
import com.fr.swift.source.db.ServerDBSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.db.TableDBSourceTransfer;
import com.fr.swift.source.etl.EtlTransferFactory;
import com.fr.swift.source.excel.ExcelDataSource;
import com.fr.swift.source.excel.ExcelTransfer;

/**
 * @author pony
 * @date 2017/11/22
 */
@SwiftBean
public class SourceTransferProvider implements SwiftSourceTransferProvider{

    public  SwiftSourceTransfer createSourceTransfer(DataSource dataSource) throws SegmentAbsentException {
        SwiftSourceTransfer transfer = null;
        if (dataSource instanceof TableDBSource) {
            transfer = new TableDBSourceTransfer(ConnectionManager.getInstance().getConnectionInfo(((TableDBSource) dataSource).getConnectionName())
                    , dataSource.getMetadata(), ((TableDBSource) dataSource).getOuterMetadata(), ((TableDBSource) dataSource).getDBTableName());
        } else if (dataSource instanceof QueryDBSource) {
            transfer = new QuerySourceTransfer(ConnectionManager.getInstance().getConnectionInfo(((QueryDBSource) dataSource).getConnectionName())
                    , dataSource.getMetadata(), ((QueryDBSource) dataSource).getOuterMetadata(), ((QueryDBSource) dataSource).getQuery());
        } else if (dataSource instanceof ServerDBSource) {

        } else if (dataSource instanceof ExcelDataSource) {
            transfer = new ExcelTransfer(((ExcelDataSource) dataSource).getAllPaths(), dataSource.getMetadata(), ((ExcelDataSource) dataSource).getOuterMetadata());
        } else if (dataSource instanceof EtlDataSource) {
            transfer = EtlTransferFactory.createTransfer((EtlDataSource) dataSource);
        }
        return transfer;
    }

//    private static SwiftMetaData getOrCreateETLTable(SwiftMetaData metaData, EtlSource source) {
//        if (metaData == null) {
//            ETLOperator operator = source.getOperator();
//            List<DataSource> parentSource = source.getBasedSources();
//            List<SwiftMetaData> list = new ArrayList<SwiftMetaData>();
//            for (DataSource etlSource : parentSource) {
//                SwiftMetaData parentMetaData = getOrCreateETLTable(metaData, (EtlSource) etlSource);
//                if (parentMetaData == null) {
//                    throw new RuntimeException();
//                }
//                list.add(parentMetaData);
//            }
//            List<SwiftMetaDataColumn> columnList = operator.getColumns(list.toArray(new SwiftMetaData[list.size()]));
//            return new SwiftMetaDataBean(source.getSourceKey().getId(), columnList);
//        }
//        return metaData;
//    }
}
