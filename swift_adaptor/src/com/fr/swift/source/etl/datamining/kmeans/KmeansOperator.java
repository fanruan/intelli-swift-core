package com.fr.swift.source.etl.datamining.kmeans;

import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/19 0020 15:22
 */
public class KmeansOperator extends AbstractOperator {

    private int cluster;
    private int iterations;
    private boolean replaceMissing;
    private boolean distanceFunction;
    private String[] dimensions;

    public int getCluster() {
        return cluster;
    }

    public int getIterations() {
        return iterations;
    }

    public boolean isReplaceMissing() {
        return replaceMissing;
    }

    public boolean isDistanceFunction() {
        return distanceFunction;
    }

    public String[] getDimensions() {
        return dimensions;
    }

    public KmeansOperator(int cluster, int iterations, boolean replaceMissing,
                          boolean distanceFunction, String[] dimensions) {
        this.cluster = cluster;
        this.iterations = iterations;
        this.replaceMissing = replaceMissing;
        this.distanceFunction = distanceFunction;
        this.dimensions = dimensions;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        for(int i = 0; i < dimensions.length; i ++) {
            columnList.add(new MetaDataColumn(dimensions[i], dimensions[i],
                    Types.DOUBLE, MD5Utils.getMD5String(new String[]{(dimensions[i])})));
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return null;
    }

    @Override
    public String getNewAddedName() {
        return null;
    }
}
