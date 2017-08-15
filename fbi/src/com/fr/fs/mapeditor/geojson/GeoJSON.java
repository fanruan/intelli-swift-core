package com.fr.fs.mapeditor.geojson;

public abstract class GeoJSON implements Cloneable{
    private String type;
    
    public GeoJSON() {
        setType(getClass().getSimpleName());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
