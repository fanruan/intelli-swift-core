package com.fr.swift.source.etl.datamining.kmeans;

import com.finebi.conf.internalimp.analysis.bean.operator.datamining.AlgorithmBean;
import com.finebi.conf.internalimp.analysis.bean.operator.datamining.kmeans.KmeansBean;
import com.fr.swift.source.ColumnTypeUtils;
import com.fr.swift.source.MetaDataColumn;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.core.MD5Utils;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.ColumnTypeConstants.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Handsome on 2018/3/19 0020 15:22
 */
public class KmeansOperator extends AbstractOperator {
    @CoreField
    private int cluster;
    @CoreField
    private int iterations;
    @CoreField
    private boolean replaceMissing;
    @CoreField
    private boolean distanceFunction;
    @CoreField
    private String[] dimensions;
    @CoreField
    private AlgorithmBean algorithmBean;
    @CoreField
    private String clusterName;

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

    public KmeansOperator(AlgorithmBean algorithmBean) {
        this.algorithmBean = algorithmBean;
        init();
    }

    private void init() {
        KmeansBean bean = (KmeansBean) algorithmBean;
        this.cluster = bean.getCluster();
        this.iterations = bean.getIterations();
        this.replaceMissing = bean.isReplaceMissing();
        this.distanceFunction = bean.isDistanceFunction();
        this.dimensions = bean.getDimensions();
        this.clusterName = bean.getClusterName();
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(clusterName, clusterName, ColumnTypeUtils.columnTypeToSqlType(ColumnType.NUMBER),
                MD5Utils.getMD5String(new String[]{(clusterName)})));
        for(int i = 0; i < dimensions.length; i ++) {
            columnList.add(new MetaDataColumn(dimensions[i], dimensions[i],
                    Types.DOUBLE, MD5Utils.getMD5String(new String[]{(dimensions[i])})));
        }
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.EXTRA_TRUE;
    }

    @Override
    public List<String> getNewAddedName() {
        List<String> columnList = Arrays.asList(dimensions);
        columnList.add(clusterName);
        return columnList;
    }
}
