package com.fr.fs.mapeditor.geojson;

import com.fr.stable.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Feature extends GeoJSON {
    private  Geometry geometry;
    private  Map<String, Object> properties;

    public Feature(Geometry geometry, Map<String,Object> properties){
        super();
        this.geometry = geometry;
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public String calculateName(){
        if(properties == null){
            return StringUtils.EMPTY;
        }
        Object o = properties.get("name");
        return o == null ? StringUtils.EMPTY : o.toString();
    }

    public Object calculateCenter() {
        return properties == null ? null : properties.get("center");
    }

    public List<List<double[]>> calculateOrderCoordinates() {
        return geometry == null ? new ArrayList<List<double[]>>() : geometry.calculateOrderCoordinates();
    }
}
