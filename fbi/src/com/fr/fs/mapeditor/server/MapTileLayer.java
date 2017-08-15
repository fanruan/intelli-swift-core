package com.fr.fs.mapeditor.server;

import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLable;
import com.fr.stable.xml.XMLableReader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by eason on 2017/7/20.
 */
public class MapTileLayer implements XMLable {

    public static String XML_TAG = "gis";

    public static String TILE_LAYER = "tileLayer";
    public static String WMS_LAYER = "wmsLayer";

    private String type = TILE_LAYER;
    private String url = StringUtils.EMPTY;
    private String attribution = StringUtils.EMPTY;
    private HashMap<String, Boolean> wmsLayers = new HashMap<String, Boolean>();

    public MapTileLayer(String type){
        this.type = type;
    }

    public void readXML(XMLableReader reader){

        if (reader.isChildNode()) {
            if(ComparatorUtils.equals(reader.getTagName(), "url")) {
                this.url = reader.getElementValue();
            }

            if(ComparatorUtils.equals(reader.getTagName(), "attribution")) {
                this.attribution = reader.getElementValue();
            }

            if(ComparatorUtils.equals(reader.getTagName(), "layer")){
                wmsLayers.put(
                    reader.getAttrAsString("name", StringUtils.EMPTY), reader.getAttrAsBoolean("used", false)
                );
            }
        }

    }

    public String getType(){
        return this.type;
    }

    public String getUrl(){
        return this.url;
    }

    public boolean isTileLayer(){
        return ComparatorUtils.equals(this.type, TILE_LAYER);
    }

    public JSONObject getWmsLayers() throws JSONException{
        JSONObject layers = JSONObject.create();

        Iterator iter = wmsLayers.entrySet().iterator();

        while(iter.hasNext()){
            Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>)iter.next();

            layers.put(entry.getKey(), entry.getValue());
        }

        return layers;
    }

    public JSONArray getWmsLayerArray(){

        JSONArray wms = JSONArray.create();

        Iterator iter = wmsLayers.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>)iter.next();
            if(entry.getValue().booleanValue()){
                wms.put(entry.getKey());
            }
        }

        return wms;
    }

    public void updateTileLayer(String url, String attribution, HashMap<String, Boolean> layers, boolean isTileLayer){
        this.url = url;
        this.attribution = attribution;
        this.wmsLayers = layers;
        this.type = isTileLayer ? TILE_LAYER : WMS_LAYER;
    }

    public void updateWmsLayers(String layerURL, String[] layers){
        this.type = WMS_LAYER;
        this.url = layerURL;
        wmsLayers.clear();
        for(int i = 0, len = layers.length; i < len; i++){
            wmsLayers.put(layers[i], true);
        }
    }

    public JSONObject getEntryConfig(String name){
        JSONObject data = JSONObject.create();

        try {
            data.put("name", name).put("isTileLayer", isTileLayer())
                    .put("attribution", this.attribution)
                    .put("layerURL", this.url).put("layers", this.getWmsLayers());
        }catch (Exception e){
            FRLogger.getLogger().error(e.getMessage());
        }

        return data;
    }


    public void writeXML(XMLPrintWriter writer){

        writer.startTAG("url");
        writer.textNode(this.url);
        writer.end();

        writer.startTAG("attribution");
        writer.textNode(this.attribution);
        writer.end();

        Iterator iter = wmsLayers.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, Boolean> entry = (Map.Entry<String, Boolean>)iter.next();
            writer.startTAG("layer")
                    .attr("name", entry.getKey())
                    .attr("used", entry.getValue())
                    .end();
        }

    }

    public Object clone() throws CloneNotSupportedException{

        return new MapTileLayer(TILE_LAYER);

    }

    public String getAttribution() {
        return attribution;
    }
}
