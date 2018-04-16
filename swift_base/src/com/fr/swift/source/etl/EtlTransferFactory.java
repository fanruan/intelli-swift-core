package com.fr.swift.source.etl;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.exception.SegmentAbsentException;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.DataSourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pony
 * @date 2018/1/8
 */
public class EtlTransferFactory {
    public static EtlTransfer createTransfer(EtlSource source) throws SegmentAbsentException {
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
            if (!SwiftContext.getInstance().getSegmentProvider().isSegmentsExist(dataSource.getSourceKey())) {
                throw new SegmentAbsentException(dataSource);
            }
            List<Segment> segments = SwiftContext.getInstance().getSegmentProvider().getSegment(DataSourceUtils.getSwiftSourceKey(dataSource));
            basedSegments.add(segments.toArray(new Segment[segments.size()]));
        }
        return new EtlTransfer(transferOperator, metaData, basedMetas, basedSegments, source.getFieldsInfo());
    }

//    public static SwiftSourceTransfer createBaseTableMinorTransfer(ETLSource source) throws Exception{
//        SwiftContext.getInstance().getMinorSegmentManager().update(source);
//        return null;
//    }

}
