package com.fr.bi.conf.report.map;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.general.GeneralContext;
import com.fr.json.JSONException;
import com.fr.stable.CodeUtils;
import com.fr.stable.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/5.
 */
public class BIMapInfoManager {
    private Map<String, String> innerMapPath;
    private Map<String, String> innerMapName;
    private Map<String, String> innerMapTypeName;
    private Map<String, Integer> innerMapLayer;
    private Map<String, List<String>> innerMapParentChildrenRelation;

    private Map<String, String> customMapPath;
    private Map<String, String> customMapName;
    private Map<String, String> customMapTypeName;
    private Map<String, Integer> customMapLayer;
    private Map<String, List<String>> customMapParentChildrenRelation;

    public static final String ACTION_PREFIX = "?op=fr_bi_base&cmd=get_map_json&file_path=";
    public static final String JSON_FOLDER = "geojson";

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
        try {
            innerMapPath = new HashMap<String, String>();
            innerMapName = new HashMap<String, String>();
            innerMapTypeName = new HashMap<String, String>();
            innerMapLayer = new HashMap<String, Integer>();
            innerMapParentChildrenRelation = new HashMap<String, List<String>>();
            customMapPath = new HashMap<String, String>();
            customMapName = new HashMap<String, String>();
            customMapTypeName = new HashMap<String, String>();
            customMapLayer = new HashMap<String, Integer>();
            customMapParentChildrenRelation = new HashMap<String, List<String>>();
            String innerMapPath = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.MAP_JSON.MAP_PATH + File.separator + "map").getAbsolutePath();
            String customMapPath = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.MAP_JSON.MAP_PATH + File.separator + "image").getAbsolutePath();
            editFileNames(innerMapPath, "map", "map", "MAP_", 0, true);
            editFileNames(customMapPath, "image", "image", "MAP_", 0, false);
        } catch (JSONException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    private void editFileNames(String path, String parentPath, String parentName, String prev, int layer, boolean isInner) throws JSONException {
        File file = new File(path);
        File[] array = file.listFiles();
        if (array == null) {
            return;
        }
        List<File> dirs = new ArrayList<File>();
        List<File> files = new ArrayList<File>();
        for (File f : array) {
            if (f.isFile()) {
                files.add(f);
            } else {
                if (f.isDirectory()) {
                    dirs.add(f);
                }
            }
        }
        List<String> children = new ArrayList<String>();
        for (File f : files) {
            String fileName = f.getName().substring(0, f.getName().lastIndexOf("."));
            String currentName = StringUtils.isEmpty(parentName) ? fileName : parentName + "/" + fileName;
            if (isInner) {
                innerMapName.put(fileName, prev + currentName);
                innerMapTypeName.put(prev + currentName, fileName);
                innerMapPath.put(prev + currentName, ACTION_PREFIX + CodeUtils.cjkEncode(currentName) + ".json");
                innerMapLayer.put(prev + currentName, layer);
            } else {
                customMapName.put(fileName, prev + currentName);
                customMapTypeName.put(prev + currentName, fileName);
                customMapPath.put(prev + currentName, ACTION_PREFIX + CodeUtils.cjkEncode(currentName) + ".json");
                customMapLayer.put(prev + currentName, layer);
            }
            children.add(prev + currentName);
        }
        if (isInner) {
            innerMapParentChildrenRelation.put(parentName, children);
        } else {
            customMapParentChildrenRelation.put(parentName, children);
        }
        for (File f : dirs) {
            String currentName = StringUtils.isEmpty(parentName) ? f.getName() : parentName + "/" + f.getName();
            String currentPath = StringUtils.isEmpty(parentPath) ? f.getName() : parentPath + "/" + f.getName();
            editFileNames(f.getAbsolutePath(), currentPath, currentName, prev, layer + 1, isInner);

        }
    }

    public Map<String, String> getinnerMapPath() {
        return innerMapPath;
    }

    public Map<String, String> getinnerMapName() {
        return innerMapName;
    }

    public Map<String, Integer> getinnerMapLayer() {
        return innerMapLayer;
    }

    public Map<String, List<String>> getinnerMapParentChildrenRelation() {
        return innerMapParentChildrenRelation;
    }

    public Map<String, String> getinnerMapTypeName() {
        return innerMapTypeName;
    }


    public Map<String, String> getCustomMapPath() {
        return customMapPath;
    }

    public Map<String, String> getCustomMapName() {
        return customMapName;
    }

    public Map<String, Integer> getCustomMapLayer() {
        return customMapLayer;
    }

    public Map<String, List<String>> getCustomMapParentChildrenRelation() {
        return customMapParentChildrenRelation;
    }

    public Map<String, String> getCustomMapTypeName() {
        return customMapTypeName;
    }
}
