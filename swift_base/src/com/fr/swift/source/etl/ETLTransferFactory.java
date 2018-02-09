package com.fr.swift.source.etl;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.generate.minor.MinorSegmentManager;
import com.fr.swift.generate.minor.MinorUpdater;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pony on 2018/1/8.
 */
public class ETLTransferFactory {
    public static ETLTransfer createTransfer(ETLSource source) {
        SwiftMetaData metaData = source.getMetadata();
        ETLOperator operator = source.getOperator();
        ETLTransferOperator transferOperator = ETLTransferOperatorFactory.createTransferOperator(operator);
        List<SwiftMetaData> basedMetas = new ArrayList<SwiftMetaData>();
        for (DataSource basedSource : source.getBasedSources()) {
            basedMetas.add(basedSource.getMetadata());
        }
        List<DataSource> baseDataSourceList = source.getBasedSources();
        List<Segment[]> basedSegments = new ArrayList<Segment[]>();
        for (DataSource dataSource : baseDataSourceList) {
            List<Segment> segments = SwiftContext.getInstance().getSwiftSegmentProvider().getSegment(dataSource.getSourceKey());
            basedSegments.add(segments.toArray(new Segment[segments.size()]));
        }
        return new ETLTransfer(transferOperator, metaData, basedMetas, basedSegments);
    }

//    public static SwiftSourceTransfer createBaseTableMinorTransfer(ETLSource source) throws Exception{
//        SwiftContext.getInstance().getMinorSegmentManager().update(source);
//        return null;
//    }

    public static ETLTransfer createMinorTransfer(ETLSource source) throws Exception {
        SwiftMetaData metaData = source.getMetadata();
        ETLOperator operator = source.getOperator();
        ETLTransferOperator transferOperator = ETLTransferOperatorFactory.createTransferOperator(operator);
        List<SwiftMetaData> basedMetas = new ArrayList<SwiftMetaData>();
        for (DataSource basedSource : source.getBasedSources()) {
            basedMetas.add(basedSource.getMetadata());
        }
        List<DataSource> baseDataSourceList = source.getBasedSources();
        List<Segment[]> basedSegments = new ArrayList<Segment[]>();
        for (DataSource dataSource : baseDataSourceList) {
            if(dataSource != null) {
                if(!MinorSegmentManager.getInstance().isSegmentsExist(dataSource.getSourceKey())) {
                    MinorUpdater.update(dataSource);
                }
                List<Segment> segments = SwiftContext.getInstance().getMinorSegmentManager().getSegment(dataSource.getSourceKey());
                basedSegments.add(segments.toArray(new Segment[segments.size()]));
            }
        }
        if (baseDataSourceList.isEmpty()){
            basedSegments.add(new Segment[0]);
        }
        return new ETLTransfer(transferOperator, metaData, basedMetas, basedSegments);
    }
}
