package com.fr.swift.source.etl.datamining.kmeans;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftResultSet;
import weka.clusterers.SimpleKMeans;
import weka.core.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/3/19 0020 15:41
 */
public class KmeansResultSet implements SwiftResultSet {

    private int cluster;
    private int iterations;
    private boolean replaceMissing;
    private boolean distanceFunction;
    private Segment[] segments;
    private SwiftMetaData metaData;
    private String[] dimensions;
    private double[][] data;
    private int totalRowCount;
    private int rowCursor;
    private TempValue tempValue;

    public KmeansResultSet(int cluster, int iterations, boolean replaceMissing,
                           boolean distanceFunction, Segment[] segments,
                           SwiftMetaData metaData, String[] dimensions) {
        this.cluster = cluster;
        this.iterations = iterations;
        this.replaceMissing = replaceMissing;
        this.distanceFunction = distanceFunction;
        this.segments = segments;
        this.metaData = metaData;
        this.dimensions = dimensions;
        init();
    }

    private void init(){
        tempValue = new TempValue();
        data = new double[cluster][dimensions.length];
        rowCursor = 0;
        Instances instances = processData();
        SimpleKMeans kmeans = new SimpleKMeans();
        try {
            kmeans.setNumClusters(cluster);
            if(iterations > 0) {
                kmeans.setMaxIterations(iterations);
            }
            if(distanceFunction) {
                kmeans.setDistanceFunction(new EuclideanDistance());
            } else {
                kmeans.setDistanceFunction(new ManhattanDistance());
            }
            kmeans.setDontReplaceMissingValues(replaceMissing);
            kmeans.buildClusterer(instances);
            createData(kmeans.getClusterCentroids());
        } catch(Exception e) {
            throw new RuntimeException("Number of clusters must be > 0");
        }
    }

    private void createData(Instances instances) {
        for(int i = 0; i < cluster; i++) {
            Instance instance = instances.get(i);
            for(int j = 0; j < dimensions.length; j++) {
                data[i][j] = instance.value(j);
            }
        }
    }

    private Instances processData() {
        int colCount = dimensions.length;
        ArrayList<Attribute> attributes = new ArrayList<Attribute>();
        for(int i = 0; i < colCount; i ++) {
            attributes.add(new Attribute(dimensions[i]));
        }
        for(int i = 0; i < segments.length; i ++) {
            totalRowCount += segments[i].getRowCount();
        }
        Instances wine = new Instances("dataSet", attributes, totalRowCount);
        for(int i = 0; i < segments.length; i++) {
            for(int j = 0; j < segments[i].getRowCount(); j++) {
                DenseInstance dataInstance = new DenseInstance(colCount);
                for(int k = 0; k < dimensions.length; k++) {
                    DictionaryEncodedColumn getter = segments[i].getColumn(new ColumnKey(dimensions[k])).getDictionaryEncodedColumn();
                    Object value = getter.getValue(getter.getIndexByRow(j));
                    double v = Double.parseDouble(value.toString());
                    dataInstance.setValue(k, v);
                }
                wine.add(dataInstance);
            }
        }
        return wine;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public boolean next() throws SQLException {
        if(rowCursor < totalRowCount) {
            List dataList = new ArrayList();
            if(rowCursor < cluster) {
                for(int i = 0; i < dimensions.length; i++) {
                    dataList.add(data[rowCursor][i]);
                }
            } else {
                for(int i = 0; i < dimensions.length; i++) {
                    dataList.add(null);
                }
            }
            tempValue.setRow(new ListBasedRow(dataList));
            rowCursor ++;
            return true;
        }
        return false;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public Row getRowData() throws SQLException {
        return tempValue.getRow();
    }

    private class TempValue {
        public ListBasedRow getRow() {
            return row;
        }

        public void setRow(ListBasedRow row) {
            this.row = row;
        }

        ListBasedRow row;

    }
}
