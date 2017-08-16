package com.fr.fs.mapeditor.geojson;

import java.util.ArrayList;
import java.util.List;
import com.fr.fs.mapeditor.geojson.Geometry;

public class GeometryCollection extends Geometry {
    private Geometry[] geometries;

    public Geometry[] getGeometries() {
        return geometries;
    }

    public void setGeometries(Geometry[] geometries) {
        this.geometries = geometries;
    }

    @Override
    public List<List<double[]>> calculateOrderCoordinates() {
        List<List<double[]>> result = new ArrayList<List<double[]>>();
        for(Geometry geometry : geometries){
            result.addAll(geometry.calculateOrderCoordinates());
        }
        return result;
    }
}
