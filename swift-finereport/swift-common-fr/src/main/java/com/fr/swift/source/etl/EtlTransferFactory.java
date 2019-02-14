package com.fr.swift.source.etl;

import com.fr.swift.SwiftContext;
import com.fr.swift.exception.SegmentAbsentException;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.EtlDataSource;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2018/1/8
 */
public class EtlTransferFactory {
    public static EtlTransfer createTransfer(EtlDataSource source) throws SegmentAbsentException {
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
            SwiftSegmentManager manager = SwiftContext.get().getBean("localSegmentProvider", SwiftSegmentManager.class);
            if (!manager.isSegmentsExist(dataSource.getSourceKey())) {
                throw new SegmentAbsentException(dataSource);
            }
            List<Segment> segments = manager.getSegment(dataSource.getSourceKey());
            basedSegments.add(segments.toArray(new Segment[0]));
        }
        return new EtlTransfer(transferOperator, metaData, basedMetas, basedSegments, source.getFieldsInfo());
    }
}
