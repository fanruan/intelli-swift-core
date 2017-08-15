package com.fr.fs.mapeditor.geojson;

import java.util.List;

public class LineString extends Geometry {
    private double[][] coordinates;

    public double[][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][] coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * 获取多个有序的经纬度的list,代表多个封闭区域
     *
     * @return 有序经纬度的list
     */
    @Override
    public List<List<double[]>> calculateOrderCoordinates() {
        return calculateCoordinates(coordinates);
    }
}
