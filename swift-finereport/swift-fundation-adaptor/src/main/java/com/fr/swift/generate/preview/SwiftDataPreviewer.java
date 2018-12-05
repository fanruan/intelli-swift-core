package com.fr.swift.generate.preview;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftSourceTransfer;
import com.fr.swift.source.db.ConnectionManager;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.source.db.ServerDBSource;
import com.fr.swift.source.db.TableDBSource;
import com.fr.swift.source.etl.ETLOperator;
import com.fr.swift.source.etl.ETLTransferOperator;
import com.fr.swift.source.etl.EtlTransfer;
import com.fr.swift.source.etl.EtlTransferOperatorFactory;
import com.fr.swift.source.excel.ExcelDataSource;
import com.fr.swift.source.excel.ExcelTransfer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/2/22
 */
public final class SwiftDataPreviewer {
    public static SwiftSourceTransfer createPreviewTransfer(DataSource dataSource, int rowCount) {
        SwiftSourceTransfer transfer = null;
        if (dataSource instanceof TableDBSource) {
            transfer = new TableDbSourcePreviewTransfer(ConnectionManager.getInstance().getConnectionInfo(((TableDBSource) dataSource).getConnectionName()),
                    ((TableDBSource) dataSource).getFieldColumnTypes(), dataSource.getMetadata(), rowCount, ((TableDBSource) dataSource).getDBTableName());
        } else if (dataSource instanceof QueryDBSource) {
            transfer = new QuerySourcePreviewTransfer(ConnectionManager.getInstance().getConnectionInfo(((QueryDBSource) dataSource).getConnectionName()),
                    ((QueryDBSource) dataSource).getFieldColumnTypes(), rowCount, ((QueryDBSource) dataSource).getQuery());
        } else if (dataSource instanceof ServerDBSource) {
        } else if (dataSource instanceof ExcelDataSource) {
            transfer = new ExcelTransfer(((ExcelDataSource) dataSource).getAllPaths(),dataSource.getMetadata(),((ExcelDataSource) dataSource).getOuterMetadata());
        } else if (dataSource instanceof EtlDataSource) {
            transfer = createMinorEtlTransfer((EtlDataSource) dataSource);
        }
        return transfer;
    }

    private static EtlTransfer createMinorEtlTransfer(EtlDataSource source) {
        SwiftMetaData metaData = source.getMetadata();
        ETLOperator operator = source.getOperator();
        ETLTransferOperator transferOperator = EtlTransferOperatorFactory.createTransferOperator(operator);
        List<SwiftMetaData> basedMetas = new ArrayList<SwiftMetaData>();
        for (DataSource basedSource : source.getBasedSources()) {
            basedMetas.add(basedSource.getMetadata());
        }
        List<DataSource> baseDataSourceList = source.getBasedSources();
        List<Segment[]> basedSegments = new ArrayList<Segment[]>();
        for (DataSource dataSource : baseDataSourceList) {
            List<Segment> segments = MinorSegmentManager.getInstance().getSegment(dataSource.getSourceKey());
            basedSegments.add(segments.toArray(new Segment[0]));
        }
        if (baseDataSourceList.isEmpty()) {
            basedSegments.add(new Segment[0]);
        }
        return new EtlTransfer(transferOperator, metaData, basedMetas, basedSegments, source.getFieldsInfo());
    }
}