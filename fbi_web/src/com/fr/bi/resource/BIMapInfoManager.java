package com.fr.bi.resource;

import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.CodeUtils;
import com.fr.stable.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/5.
 */
public class BIMapInfoManager {
    private Map<String, String> mapPath;
    private Map<String, String> mapName;
    private Map<String, String> mapTypeName;
    private Map<String, Integer> mapLayer;
    private Map<String, List<String>> mapParentChildrenRelation;

    private static BIMapInfoManager manager;

    public static BIMapInfoManager getInstance() {
        synchronized (BIWMSManager.class) {
            if (manager == null) {
                manager = new BIMapInfoManager();
                manager.readFiles();
            }
            return manager;
        }
    }

    private void readFiles() {
//        Map<String, JSONObject> map = new HashMap<String, JSONObject>();
//        innerMapInfo = new JSONObject();
//        customMapInfo = new JSONObject();
//        try {
//            customMapInfo.put("MAP_NAME", new JSONObject());
//            customMapInfo.put("MAP_TYPE_NAME", new JSONObject());
//            customMapInfo.put("MAP_PATH", new JSONObject());
//            customMapInfo.put("MAP_LAYER", new JSONObject());
//            customMapInfo.put("MAP_PARENT_CHILDREN", new JSONObject());
//            innerMapInfo.put("MAP_NAME", new JSONObject());
//            innerMapInfo.put("MAP_TYPE_NAME", new JSONObject());
//            innerMapInfo.put("MAP_PATH", new JSONObject());
//            innerMapInfo.put("MAP_LAYER", new JSONObject());
//            innerMapInfo.put("MAP_PARENT_CHILDREN", new JSONObject());
//            String innerMapPath = new File(GeneralContext.getEnvProvider().getPath(), "resources/geojson/map").getAbsolutePath();
//            String customMapPath = new File(GeneralContext.getEnvProvider().getPath(), "resources/geojson/image").getAbsolutePath();
//            editFileNames(innerMapPath, "map", "map", innerMapInfo, "MAP_", 0);
//            editFileNames(customMapPath, "image", "image", customMapInfo, "MAP_", 0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    private void editFileNames(String path, String parentPath, String parentName, JSONObject obj, String prev, int layer) throws JSONException {
        File file = new File(path);
        File[] array = file.listFiles();
        if(array == null){
            return;
        }
        List<File> dirs = new ArrayList<File>();
        List<File> files = new ArrayList<File>();
        for(File f : array){
            if(f.isFile()){
                files.add(f);
            }else{
                if (f.isDirectory()){
                    dirs.add(f);
                }
            }
        }
        List<String> children = new ArrayList<String>();
        for(File f : files){
            String fileName = f.getName().substring(0, f.getName().lastIndexOf("."));
            String currentName = (StringUtils.isEmpty(parentName) ? fileName : parentName + File.separator + fileName);
            mapName.put(fileName, prev + currentName);
            mapTypeName.put(prev + currentName, fileName);
            mapPath.put(prev + currentName, "?op=fr_bi&cmd=get_map_json&file_path=" + CodeUtils.cjkEncode(currentName) + ".json");
            mapLayer.put(prev + currentName, layer);
            children.add(prev + currentName);
        }
        mapParentChildrenRelation.put(parentName, children);
        for(File f : dirs){
            String currentName = (StringUtils.isEmpty(parentName) ? f.getName() : parentName + File.separator + f.getName());
            String currentPath = StringUtils.isEmpty(parentPath) ? f.getName() : parentPath + File.separator + f.getName();
            editFileNames(f.getAbsolutePath(), currentPath, currentName, obj, prev, layer + 1);

        }
    }

    public Map<String, String> getMapPath(){
        return mapPath;
    }

    public Map<String, String> getMapName(){
        return mapName;
    }

    public Map<String, Integer> getMapLayer(){
        return mapLayer;
    }

    public Map<String, List<String>> getMapParentChildrenRelation(){
        return getMapParentChildrenRelation();
    }

    public Map<String, String> getMapTypeName(){
        return mapTypeName;
    }
}
