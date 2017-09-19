package com.fr.bi.conf.report.map;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.base.FRContext;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.json.JSONException;
//import com.fr.plugin.bi.chart.map.server.GEOJSONHelper;
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

    public static final String PRE = "MAP_";
    public static final String GEOGRAPHIC = "geographic";
    public static final String IMAGE = "image";

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
            String innerMapPath = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.MAP_JSON.MAP_PATH + File.separator + "geographic").getAbsolutePath();
            String customMapPath = new File(FRContext.getCurrentEnv().getPath() + BIBaseConstant.MAP_JSON.MAP_PATH + File.separator + "image").getAbsolutePath();
            editFileNames(innerMapPath, GEOGRAPHIC, GEOGRAPHIC, 0, true);
            editFileNames(customMapPath, IMAGE, IMAGE, 0, false);
        } catch (JSONException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    private void editFileNames(String path, String parentPath, String parentName, int layer, boolean isInner) throws JSONException {
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
            if(StringUtils.isEmpty(fileName)){
                continue;
            }
//            fileName = fileName.replace(GEOJSONHelper.POINT, StringUtils.EMPTY).replace(GEOJSONHelper.AREA, StringUtils.EMPTY);
            String currentName = StringUtils.isEmpty(parentName) ? fileName : parentName + "/" + fileName;
            if (isInner) {
                if(innerMapName.containsKey(fileName)){
                    continue;
                }
                innerMapName.put(fileName, PRE + currentName);
                innerMapTypeName.put(PRE + currentName, fileName);
                innerMapPath.put(PRE + currentName, ACTION_PREFIX + CodeUtils.cjkEncode(currentName) + ".json");
                innerMapLayer.put(PRE + currentName, layer);
            } else {
                customMapName.put(fileName, PRE + currentName);
                customMapTypeName.put(PRE + currentName, fileName);
                customMapPath.put(PRE + currentName, ACTION_PREFIX + CodeUtils.cjkEncode(currentName) + ".json");
                customMapLayer.put(PRE + currentName, layer);
            }
            children.add(PRE + currentName);
        }
        if (isInner) {
            innerMapParentChildrenRelation.put(parentName, children);
        } else {
            customMapParentChildrenRelation.put(parentName, children);
        }
        for (File f : dirs) {
            String currentName = StringUtils.isEmpty(parentName) ? f.getName() : parentName + "/" + f.getName();
            String currentPath = StringUtils.isEmpty(parentPath) ? f.getName() : parentPath + "/" + f.getName();
            editFileNames(f.getAbsolutePath(), currentPath, currentName, layer + 1, isInner);

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
