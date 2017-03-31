package com.fr.bi.conf.report.map;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.util.ArrayList;

/**
 * Created by eason on 2017/3/31.
 */
public class MapInfo {

    private String type = StringUtils.EMPTY;
    private ArrayList<String> wmsLayers = new ArrayList<String>();
    private String url = StringUtils.EMPTY;


    public void setUrl(String url){
        this.url = url;
    }

    public void addWmsLayer(String layer){
        this.wmsLayers.add(layer);
    }


    public void setType(String type){
        this.type = type;
    }

    public JSONObject toJSON(){
        JSONObject json = JSONObject.create();
        try {
            JSONArray wms = JSONArray.create();
            for(int i = 0, len = wmsLayers.size(); i < len; i++){
                wms.put(wmsLayers.get(i));
            }
            json = JSONObject.create().put("type", type).put("url", url).put("wmsLayer", wms);
        }catch (JSONException e){
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }

        return json;
    }
}
