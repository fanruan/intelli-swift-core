package com.fr.swift.source.etl.datamining.kmeans;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.ETLTransferOperator;

import java.util.List;

/**
 * Created by Handsome on 2018/3/20 0020 18:06
 */
public class KmeansTransferOperator implements ETLTransferOperator {

    private int cluster;
    private int iterations;
    private boolean replaceMissing;
    private boolean distanceFunction;
    private String[] dimensions;

    public KmeansTransferOperator(int cluster, int iterations, boolean replaceMissing,
                                  boolean distanceFunction, String[] dimensions) {
        this.cluster = cluster;
        this.iterations = iterations;
        this.replaceMissing = replaceMissing;
        this.distanceFunction = distanceFunction;
        this.dimensions = dimensions;
    }

    @Override
    public SwiftResultSet createResultSet(SwiftMetaData metaData, List<SwiftMetaData> basedMetas, List<Segment[]> basedSegments) {
        return new KmeansResultSet(cluster, iterations, replaceMissing, distanceFunction,
                basedSegments.get(0), metaData, dimensions);
    }
}
