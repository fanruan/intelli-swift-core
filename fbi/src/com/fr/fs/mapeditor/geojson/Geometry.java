package com.fr.fs.mapeditor.geojson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Geometry extends GeoJSON {
    public Geometry() {
        super();
    }

    /**
     * 获取多个有序的经纬度的list,代表多个封闭区域
     * @return 有序经纬度的list
     */
    public abstract List<List<double[]>> calculateOrderCoordinates();

    //点
    protected List<List<double[]>> calculateCoordinates(double[] coordinates){
        List<List<double[]>> result = new ArrayList<List<double[]>>();

        List<double[]> oneArea = new ArrayList<double[]>();
        oneArea.add(coordinates);
        
        result.add(oneArea);
        return result;
    }

    //多点/线
    protected List<List<double[]>> calculateCoordinates(double[][] coordinates){
        List<List<double[]>> result = new ArrayList<List<double[]>>();
        result.add(Arrays.asList(coordinates));
        return result;
    }

    //多线/面
    protected List<List<double[]>> calculateCoordinates(double[][][] coordinates) {
        List<List<double[]>> result = new ArrayList<List<double[]>>();

        List<double[]> oneArea = new ArrayList<double[]>();
        for(double[][] cc : coordinates){
            for(double[] c : cc){
                oneArea.add(c);
            }
        }
        result.add(oneArea);

        return result;
    }

    //多面,即多个封闭区域
    protected List<List<double[]>> calculateCoordinates(double[][][][] coordinates) {
        List<List<double[]>> result = new ArrayList<List<double[]>>();

        for(double[][][] ccc : coordinates){
            List<double[]> oneArea = new ArrayList<double[]>();
            for(double[][] cc : ccc){
                for(double[] c : cc){
                    oneArea.add(c);
                }
            }
            result.add(oneArea);
        }

        return result;
    }

}
