package com.fr.fs.mapeditor.server;

import com.fr.base.FRContext;
import com.fr.file.XMLFileManager;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.general.GeneralContext;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StringUtils;
import com.fr.stable.file.RemoteXMLFileManagerProvider;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by eason on 2017/7/19.
 */
public class MapLayerConfigManager extends XMLFileManager implements RemoteXMLFileManagerProvider {

    public static String XML_TAG = "MapLayerConfigManager";

    private static MapLayerConfigManager manager = null;

    private static HashMap<String, MapTileLayer> layerMap = new HashMap<String, MapTileLayer>();

    public synchronized static MapLayerConfigManager getInstance() {
        if (manager == null) {
            manager = new MapLayerConfigManager();
            manager.readXMLFile();
        }

        return manager;
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            public void envChanged() {
                manager = null;
                layerMap.clear();
            }
        });
    }

    public JSONArray createMapLayerFolderEntries(){

        JSONArray entries = JSONArray.create();

        try {
            Iterator iter = layerMap.entrySet().iterator();
            while(iter.hasNext()){

                Map.Entry<String, MapTileLayer> entry = (Map.Entry<String, MapTileLayer>)iter.next();

                MapTileLayer value = entry.getValue();

                entries.put(value.getEntryConfig(entry.getKey()));
            }

        }catch (Exception e){
            FRLogger.getLogger().error(e.getMessage());
        }


        return entries;
    }

    public JSONObject addLayerEntry(String name){

        MapTileLayer layer = new MapTileLayer(MapTileLayer.TILE_LAYER);

        layerMap.put(name, layer);

        this.saveCurrentConfig();

        return layer.getEntryConfig(name);
    }

    public void removeLayerEntry(String name){
        layerMap.remove(name);

        this.saveCurrentConfig();
    }

    public void editLayerEntry(String oldName, String newName){
        if(layerMap.containsKey(oldName)){

            layerMap.put(newName, layerMap.get(oldName));

            this.saveCurrentConfig();
        }
    }

    public MapTileLayer getMapTileLayer(String name){
        return layerMap.get(name);
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            if (ComparatorUtils.equals(reader.getTagName(), "gis")) {
                String name = reader.getAttrAsString("name", StringUtils.EMPTY);
                String type = reader.getAttrAsString("type", MapTileLayer.TILE_LAYER);
                layerMap.put(name, (MapTileLayer)reader.readXMLObject(new MapTileLayer(type)));
            }
        }
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {

        writer.startTAG(MapLayerConfigManager.XML_TAG);

        Iterator iter = layerMap.entrySet().iterator();
        while(iter.hasNext()){

            Map.Entry<String, MapTileLayer> entry = (Map.Entry<String, MapTileLayer>)iter.next();

            MapTileLayer value = entry.getValue();

            writer.startTAG(value.XML_TAG).attr("name", entry.getKey()).attr("type", value.getType());

            value.writeXML(writer);

            writer.end();
        }

        writer.end();

        writer.close();
    }

    @Override
    public String fileName() {
        return "mapLayer.xml";
    }

    @Override
    public boolean writeResource() throws Exception {
        return FRContext.getCurrentEnv().writeResource(MapLayerConfigManager.getInstance());
    }

    private void saveCurrentConfig(){
        try {
            this.writeResource();
        }catch (Exception ex){
            FRLogger.getLogger().error(ex.getMessage());
        }
    }
}
