package com.fr.fs.mapeditor.geojson;

import java.util.*;

public class FeatureCollection extends GeoJSON {
    private Feature[] features;

    public FeatureCollection(Feature[] features) {
        super();
        this.features = features;
    }

    public Feature[] getFeatures() {
        return features;
    }

    public void setFeatures(Feature[] features) {
        this.features = features;
    }

    /**
     * 获取所有区域的所有坐标
     * 改正:一个区域名对应的可能是多个封闭多边形
     * 改正:json含有同名区域,要显示出来.一个区域名对应多个边界,每个边界对应多个封闭图形.
     * @return 所有区域名及其对应的有序坐标
     */
    public Map<String, List<List<List<double[]>>>> calculateOrderCoordinates() {
        Map<String, List<List<List<double[]>>>> result = new HashMap<String, List<List<List<double[]>>>>();
        for(Feature feature : features){
            String areaName = feature.calculateName();
            List<List<List<double[]>>> areaList = result.get(areaName);
            if(areaList == null){
                areaList = new ArrayList<List<List<double[]>>>();
                result.put(areaName, areaList);
            }
            areaList.add(feature.calculateOrderCoordinates());
        }
        return result;
    }

    public Set<String> calculateAreaNames() {
        Set<String> areaNameList = new HashSet<String>();
        for(Feature feature : features){
            areaNameList.add(feature.calculateName());
        }
        return areaNameList;
    }

    public Map<String, List<Object>> calculateCenter() {
        Map<String, List<Object>> map = new HashMap<String, List<Object>>();
        for(Feature feature : features) {
            String areaName = feature.calculateName();
            Object center = feature.calculateCenter();
            List<Object> centerList = map.get(areaName);
            if(centerList == null){
                centerList = new ArrayList<Object>();
                map.put(areaName, centerList);
            }
            centerList.add(center);
        }
        return map;
    }
}
