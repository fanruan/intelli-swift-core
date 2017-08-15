package com.fr.fs.mapeditor.server;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import com.fr.base.FRContext;
import com.fr.file.filetree.FileNode;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.general.GeneralContext;
import com.fr.general.IOUtils;
import com.fr.general.Inter;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.fs.mapeditor.geojson.Feature;
import com.fr.fs.mapeditor.geojson.FeatureCollection;
import com.fr.fs.mapeditor.geojson.GeoJSONFactory;
import com.fr.fs.mapeditor.geojson.JSONMapper;
import com.fr.stable.ArrayUtils;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.CoreConstants;
import com.fr.stable.project.ProjectConstants;
import com.fr.stable.EncodeConstants;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GEOJSONHelper {
    //区域和点分开存
    public static final String GEO_JSON_DIR_NAME = "map";

    public static final String GEOGRAPHIC_JSON = "geographic";
    public static final String IMAGE_JSON = "image";

    public static final String POINT = "-point";
    public static final String AREA = "-area";

    public static final String GEO_JSON_SUFFIX = ".json";
    public static final String ROOT_MARK = "rootgeojsonroot";

    private static GEOJSONHelper jsonHelper = null;

    //url和实际文件的对应关系
    public static HashMap<String, JSONMapper> URL_JSON_MAP = new HashMap<String, JSONMapper>();

    //所有的json文件和路径的对应
    private static HashMap<String, String> DIR_URL_MAP = new HashMap<String, String>();

    public static HashMap<String, Boolean> DIR_MAP = new HashMap<String, Boolean>();

    private static final Map<String, String> ROOT_NAME_MAP = new HashMap<String, String>();

    static {
        ROOT_NAME_MAP.put(MapDataType.GEOGRAPHIC.getStringType(), "BI-FS-Module_Map_Editor_Geographic");
        ROOT_NAME_MAP.put(MapDataType.IMAGE.getStringType(), "BI-FS-Module_Map_Editor_Map_Image_RootName");
        ROOT_NAME_MAP.put(MapDataType.PARAM.getStringType(), "BI-FS-Module_Map_Editor_Map_Param_RootName");

        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            public void envChanged() {
                GEOJSONHelper.reset();
            }
        });
    }

    public static void reset(){
        jsonHelper = null;
        URL_JSON_MAP.clear();
        DIR_URL_MAP.clear();
        DIR_MAP.clear();
    }

    public static GEOJSONHelper getInstance(){
        if(jsonHelper == null) {
            jsonHelper = new GEOJSONHelper();
            try {

                resetDirRelatedMap();

                readJSON(FRContext.getCurrentEnv().listFile(StableUtils.pathJoin(new String[]{ProjectConstants.ASSETS_NAME, GEO_JSON_DIR_NAME, GEOGRAPHIC_JSON})));


                readJSON(FRContext.getCurrentEnv().listFile(StableUtils.pathJoin(new String[]{ProjectConstants.ASSETS_NAME, GEO_JSON_DIR_NAME, IMAGE_JSON})));

            } catch (Exception e) {
                FRLogger.getLogger().error(e.getMessage());
            }
        }

        return jsonHelper;
    }

    private static void readJSON(FileNode[] nodes) throws Exception{
        for(FileNode node : nodes){
            String nodePath = node.getEnvPath();

            if(nodePath.endsWith(GEOJSONHelper.GEO_JSON_SUFFIX)){
                InputStream inputStream = FRContext.getCurrentEnv().readBean(nodePath, StringUtils.EMPTY);
                String string = IOUtils.inputStream2String(inputStream).replace("\uFEFF", StringUtils.EMPTY);

                //空json
                if(StringUtils.isNotBlank(string)){
                    JSONMapper jsonMapper = GeoJSONFactory.create(string, nodePath);
                    URL_JSON_MAP.put(nodePath, jsonMapper);
                }

            }

            if(node.isDirectory()){//文件夹
                readJSON(FRContext.getCurrentEnv().listFile(nodePath));
            }
        }
    }

    private static void resetDirRelatedMap(){
        try{
            initDirMap(StableUtils.pathJoin(new String[]{ProjectConstants.ASSETS_NAME, GEO_JSON_DIR_NAME}));

            readDirURLMap(FRContext.getCurrentEnv().listFile(StableUtils.pathJoin(new String[]{ProjectConstants.ASSETS_NAME, GEO_JSON_DIR_NAME, GEOGRAPHIC_JSON})));
            readDirURLMap(FRContext.getCurrentEnv().listFile(StableUtils.pathJoin(new String[]{ProjectConstants.ASSETS_NAME, GEO_JSON_DIR_NAME, IMAGE_JSON})));

        }catch (Exception e){

            FRLogger.getLogger().error(e.getMessage());

        }

    }

    private static void initDirMap(String rootPath) throws Exception{
        FileNode[] nodes = FRContext.getCurrentEnv().listFile(rootPath);
        for(FileNode node : nodes){
            if(node.isDirectory()){
                DIR_MAP.put(node.getEnvPath(), true);
                GEOJSONHelper.initDirMap(node.getEnvPath());
            }
        }
    }

    private static void readDirURLMap(FileNode[] nodes) throws Exception{

        for(FileNode node : nodes){
            String nodePath = node.getEnvPath();

            if(nodePath.endsWith(GEOJSONHelper.GEO_JSON_SUFFIX)){
                String url = GEOJSONHelper.getPartialJSONURL(nodePath);
                String dirPath = url.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);
                DIR_URL_MAP.put(hasDirectory(dirPath) ? dirPath : url, url);
            }

            if(node.isDirectory()){//文件夹
                readDirURLMap(FRContext.getCurrentEnv().listFile(nodePath));
            }
        }
    }

    public static boolean isImageMap(String jsonURL){
        JSONMapper jsonMapper = GEOJSONHelper.getInstance().getGeoJSON(jsonURL);
        return jsonMapper != null && jsonMapper.isImageMap();
    }

    public static JSONMapper getGeoJSON(String jsonURL) {

        String baseURL = jsonURL.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);

        JSONMapper pointJSON = URL_JSON_MAP.get(baseURL + POINT + GEO_JSON_SUFFIX);

        JSONMapper areaJSON = URL_JSON_MAP.get(baseURL + AREA + GEO_JSON_SUFFIX);

        return (pointJSON == null && areaJSON == null) ? URL_JSON_MAP.get(jsonURL) : JSONMapper.addJSONMapper(pointJSON, areaJSON);
    }

    private static JSONMapper parseGeoJSON(JSONMapper mapper, boolean isPoint){
        Feature[] features = mapper.getGeoJSON().getFeatures();

        ArrayList<Feature> tmpFeatures = new ArrayList<Feature>();

        JSONMapper tempMapper = null;
        try {

            tempMapper = GeoJSONFactory.create(mapper.toString(), mapper.getPath());

            for(int i = 0, len = features.length; i < len; i++){
                String type = features[i].getGeometry().getType();
                if(ComparatorUtils.equals(type, "Point") && isPoint){
                    tmpFeatures.add(features[i]);
                }

                if(!isPoint && !ComparatorUtils.equals(type, "Point")){
                    tmpFeatures.add(features[i]);
                }
            }

            tempMapper.setGeoJSON(new FeatureCollection(tmpFeatures.toArray(new Feature[0])));

        }catch (Exception ex){
            FRLogger.getLogger().error(ex.getMessage());
        }

        return tempMapper;
    }

    private static JSONMapper parseAreaGeoJSON(JSONMapper mapper){

        return parseGeoJSON(mapper, false);

    }

    private static JSONMapper parsePointGeoJSON(JSONMapper mapper){

        return parseGeoJSON(mapper, true);

    }

    public static JSONMapper getPointGeoJSON(String jsonURL){

        if(URL_JSON_MAP.containsKey(jsonURL)){
            return parsePointGeoJSON(URL_JSON_MAP.get(jsonURL));
        }else{
            String baseURL = jsonURL.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);

            return URL_JSON_MAP.get(baseURL + POINT + GEO_JSON_SUFFIX);
        }
    }

    public static JSONMapper getAreaGeoJSON(String jsonURL){

        if(URL_JSON_MAP.containsKey(jsonURL)){

            return parseAreaGeoJSON(URL_JSON_MAP.get(jsonURL));

        }else{
            String baseURL = jsonURL.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);

            return URL_JSON_MAP.get(baseURL + AREA + GEO_JSON_SUFFIX);
        }

    }

    public static JSONMapper getImageGeoJSON(String jsonURL){
        return URL_JSON_MAP.get(jsonURL);
    }

    public static String completeJSONName(String path) {
        return path.contains(GEO_JSON_SUFFIX) || CompatibleGEOJSONHelper.isParamURL(path) ? path : path + GEO_JSON_SUFFIX;
    }

    /**
     * 判断路径是否指向一个有效的json文件
     * @param dirPath 路径
     * @return 路径是否有效
     */
    public static boolean isValidDirPath(String dirPath){
        return DIR_URL_MAP.get(dirPath) != null || StringUtils.contains(dirPath, getMapDataTypePath(MapDataType.PARAM));
    }

    public static String getDefaultJSONURL(){
        JSONArray top = GEOJSONHelper.getInstance().createGeographicFolderEntries();

        if(top.length() > 0){
            top = top.optJSONObject(0).optJSONArray("children");
        }

        for(int i = 0, count = top.length(); i < count; i++){
            JSONObject entry = top.optJSONObject(i);
            if(entry.optBoolean("isValid")){
                return StableUtils.pathJoin(new String[]{ProjectConstants.ASSETS_NAME, GEO_JSON_DIR_NAME, GEOGRAPHIC_JSON, entry.optString("name")}) + GEO_JSON_SUFFIX;
            }
        }

        return StringUtils.EMPTY;
    }

    public static String getDirPathFromJSONURL(String jsonURL){
        Iterator iterator = DIR_URL_MAP.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String dirPath = entry.getKey().toString();
            String url = entry.getValue().toString();
            if(ComparatorUtils.equals(jsonURL, url)){
                return dirPath;
            }
        }
        return StringUtils.EMPTY;
    }

    public static String getJsonUrlByPath(String dirPath){
        return CompatibleGEOJSONHelper.isParamURL(dirPath) ? dirPath : DIR_URL_MAP.get(dirPath);
    }

    public static JSONArray createGeographicFolderEntries(){

        return GEOJSONHelper.getInstance().createFolderEntries(MapDataType.GEOGRAPHIC);
    }

    public static JSONArray createImageFolderEntries(){

        return GEOJSONHelper.getInstance().createFolderEntries(MapDataType.IMAGE);

    }

    //相对于WebINF的目录路径
    public static String getMapDataTypePath(MapDataType type){
        String holderName = type.getStringType();
        return StableUtils.pathJoin(new String[]{ProjectConstants.ASSETS_NAME, GEO_JSON_DIR_NAME, holderName});
    }

    private static JSONArray createFolderEntries(MapDataType mapDataType){
        String dirPath = GEOJSONHelper.getMapDataTypePath(mapDataType);

        FileNode root = new FileNode(dirPath, true);

        JSONArray children = GEOJSONHelper.getInstance().constructFolderEntries(root);

        JSONObject rootNode = JSONObject.create();
        try {
            rootNode
                    .put("isRoot", true)
                    .put("name", mapDataType == MapDataType.GEOGRAPHIC ? Inter.getLocText("BI-FS-Module_Map_Editor_Geographic") : Inter.getLocText("BI-FS-Module_Map_Editor_Custom_Image"))
                    .put("children", children);
        }catch (Exception e){
            FRLogger.getLogger().error(e.getMessage());
        }

        return JSONArray.create().put(rootNode);

    }

    private static JSONArray constructFolderEntries(FileNode fileRoot){
        JSONArray folders = JSONArray.create();
        try{
            FileNode[] childFiles = FRContext.getCurrentEnv().listFile(fileRoot.getEnvPath());
            HashMap<String, Boolean> fileMap = new HashMap<String, Boolean>();
            for(FileNode node : childFiles){
                JSONObject entry = JSONObject.create();
                String nodePath = node.getEnvPath();
                String url = GEOJSONHelper.getPartialJSONURL(nodePath);
                String dirPath = url.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);

                String text = GEOJSONHelper.getPresentNameWithPath(nodePath);

                //如果是json文件，则只有非部分的json文件，以及和目录的名字不同的json文件需要显示在目录树上
                if(nodePath.endsWith(GEOJSONHelper.GEO_JSON_SUFFIX) && !hasDirectory(dirPath) && !fileMap.containsKey(url)){
                    fileMap.put(url, true);
                    entry.put("name", text);
                    entry.put("isValid", true);
                    entry.put("isFolder", false);
                    folders.put(entry);
                }

                if(node.isDirectory()){
                    JSONArray children = constructFolderEntries(node);
                    entry.put("name", text);
                    entry.put("isValid", GEOJSONHelper.getInstance().isValidDirPath(nodePath));
                    entry.put("children", children);
                    entry.put("isFolder", true);
                    folders.put(entry);
                }
            }
        }catch (Exception e){
            FRLogger.getLogger().error(e.getMessage());
        }

        return folders;
    }

    public static String getPartialJSONURL(String url){
        return url.replace(POINT, StringUtils.EMPTY).replace(AREA, StringUtils.EMPTY);
    }

    public static String getPresentNameWithPath(String dirPath){

        if(StringUtils.isBlank(dirPath)){
            return StringUtils.EMPTY;
        }

        int index = dirPath.lastIndexOf(CoreConstants.SEPARATOR);

        String name = dirPath.substring(index + 1).replace(GEOJSONHelper.GEO_JSON_SUFFIX, StringUtils.EMPTY)
                .replace(GEOJSONHelper.AREA, StringUtils.EMPTY).replace(GEOJSONHelper.POINT, StringUtils.EMPTY);

        return ROOT_NAME_MAP.containsKey(name) ? Inter.getLocText(ROOT_NAME_MAP.get(name)) : name;
    }

    public static boolean hasDirectory(String dirPath){
        return DIR_MAP.containsKey(dirPath);
    }

    public static void editEntry(String newPath, String oldPath){
        try {

            if(StringUtils.contains(oldPath, GEO_JSON_SUFFIX)){

                GEOJSONHelper.renameJSONFile(newPath, oldPath);

            }else{

                FRContext.getCurrentEnv().renameFile(newPath, oldPath);

                if(isValidDirPath(oldPath)){
                    GEOJSONHelper.renameJSONFile(newPath + GEO_JSON_SUFFIX, oldPath + GEO_JSON_SUFFIX);
                }

                readJSON(FRContext.getCurrentEnv().listFile(newPath));
            }

            resetDirRelatedMap();

        } catch (Exception e){
            FRContext.getLogger().error(e.getMessage(), e);
        }
    }

    private static void renameJSONFile(String newPath, String oldPath) throws Exception{

        if(URL_JSON_MAP.get(oldPath) != null){//path指向的是单独的文件
            FRContext.getCurrentEnv().renameFile(newPath, oldPath);

            URL_JSON_MAP.put(newPath, URL_JSON_MAP.get(oldPath));
            URL_JSON_MAP.remove(oldPath);

        }else{//两个json文件的拼凑
            String oldBaseURL = oldPath.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);
            String newBaseURL = newPath.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);

            String oldPointPath = oldBaseURL + POINT + GEO_JSON_SUFFIX;
            String oldAreaPath = oldBaseURL + AREA + GEO_JSON_SUFFIX;

            String newPointPath = newBaseURL + POINT + GEO_JSON_SUFFIX;
            String newAreaPath = newBaseURL + AREA + GEO_JSON_SUFFIX;

            URL_JSON_MAP.put(newPointPath, URL_JSON_MAP.get(oldPointPath));
            URL_JSON_MAP.put(newAreaPath, URL_JSON_MAP.get(oldAreaPath));

            URL_JSON_MAP.remove(oldPointPath);
            URL_JSON_MAP.remove(oldAreaPath);

            FRContext.getCurrentEnv().renameFile(newPointPath, oldPointPath);
            FRContext.getCurrentEnv().renameFile(newAreaPath, oldAreaPath);
        }

    }


    public static void deleteDirectory(FileNode node){

        try {

            FileNode[] nodes = FRContext.getCurrentEnv().listFile(node.getEnvPath());

            for(FileNode child: nodes){
                if(child.isDirectory()){
                    deleteDirectory(child);
                }else{
                    URL_JSON_MAP.remove(child.getEnvPath());
                }
            }

        }catch (Exception e){

            FRContext.getLogger().error(e.getMessage(), e);

        }

    }

    public static void deleteEntry(String filePath){

        if(StringUtils.contains(filePath, GEO_JSON_SUFFIX)){
            URL_JSON_MAP.remove(filePath);
        }else{
            deleteDirectory(new FileNode(filePath, true));
        }

        String baseURL = filePath.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);
        String areaPath = baseURL + AREA + GEO_JSON_SUFFIX;
        String pointPath = baseURL + POINT + GEO_JSON_SUFFIX;

        URL_JSON_MAP.remove(areaPath);
        URL_JSON_MAP.remove(pointPath);

        FRContext.getCurrentEnv().deleteFile(filePath);
        FRContext.getCurrentEnv().deleteFile(areaPath);
        FRContext.getCurrentEnv().deleteFile(pointPath);

        resetDirRelatedMap();
    }

    public static JSONObject createGeographicEntry(String name, String parentPath) {
        JSONObject entry = JSONObject.create();

        try {

            String areaURL, pointURL;
            if(StringUtils.contains(parentPath, GEO_JSON_SUFFIX)){

                String dirPath = parentPath.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);
                GeneralContext.getEnvProvider().createFolder(dirPath);

                areaURL = StableUtils.pathJoin(new String[]{dirPath, name + AREA + GEO_JSON_SUFFIX});
                pointURL = StableUtils.pathJoin(new String[]{dirPath, name + POINT + GEO_JSON_SUFFIX});

            }else{

                areaURL = StableUtils.pathJoin(new String[]{parentPath, name + AREA + GEO_JSON_SUFFIX});
                pointURL = StableUtils.pathJoin(new String[]{parentPath, name + POINT + GEO_JSON_SUFFIX});

            }


            GeneralContext.getEnvProvider().createFile(areaURL);
            GeneralContext.getEnvProvider().createFile(pointURL);

            JSONMapper areaMapper = GeoJSONFactory.create(JSONObject.create().put("type", "FeatureCollection").put("features", JSONArray.create()).toString(), areaURL);

            JSONMapper pointMapper = GeoJSONFactory.create(JSONObject.create().put("type", "FeatureCollection").put("features", JSONArray.create()).toString(), pointURL);

            URL_JSON_MAP.put(areaURL, areaMapper);
            URL_JSON_MAP.put(pointURL, pointMapper);

            writeGeoJSON(areaMapper);
            writeGeoJSON(pointMapper);

            entry.put("name", name);
            entry.put("isValid", true);
            entry.put("isFolder", false);

        }catch (Exception ex){
            FRContext.getLogger().error(ex.getMessage(), ex);
        }

        GEOJSONHelper.resetDirRelatedMap();

        return entry;
    }

    public static JSONObject createImageEntry(String name, String parentPath) {
        JSONObject entry = JSONObject.create();

        try {

            String url;

            if(StringUtils.contains(parentPath, GEO_JSON_SUFFIX)){

                String dirPath = parentPath.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);

                GeneralContext.getEnvProvider().createFolder(dirPath);

                url = StableUtils.pathJoin(new String[]{dirPath, name + GEO_JSON_SUFFIX});

            }else{

                url = StableUtils.pathJoin(new String[]{parentPath, name + GEO_JSON_SUFFIX});

                GeneralContext.getEnvProvider().createFile(url);
            }

            GeneralContext.getEnvProvider().createFile(url);

            JSONMapper mapper = GeoJSONFactory.create(JSONObject.create().put("type", "FeatureCollection").put("features", JSONArray.create()).toString(), url);

            URL_JSON_MAP.put(url, mapper);

            writeGeoJSON(mapper);

            entry.put("name", name);
            entry.put("isValid", true);
            entry.put("isFolder", false);

        }catch (Exception ex){
            FRContext.getLogger().error(ex.getMessage(), ex);
        }

        GEOJSONHelper.resetDirRelatedMap();

        return entry;
    }

    public static boolean saveJSONData(String dirPath, JSONArray jsonData){

        String url = GEOJSONHelper.getInstance().getJsonUrlByPath(dirPath);

        if(URL_JSON_MAP.get(url) != null){

            JSONMapper mapper = URL_JSON_MAP.get(url);

            if(jsonData.length() == 2){

                FeatureCollection area = GeoJSONFactory.createGEO(jsonData.optJSONObject(0).toString());
                FeatureCollection point = GeoJSONFactory.createGEO(jsonData.optJSONObject(1).toString());

                mapper.setGeoJSON(new FeatureCollection(ArrayUtils.addAll(area.getFeatures(), point.getFeatures())));

            }else{

                mapper.setGeoJSON(GeoJSONFactory.createGEO(jsonData.optJSONObject(0).toString()));

            }

            return GEOJSONHelper.writeGeoJSON(mapper);

        }else if(jsonData.length() == 2){

            String baseURL = url.replace(GEO_JSON_SUFFIX, StringUtils.EMPTY);

            JSONMapper pointJSON = URL_JSON_MAP.get(baseURL + POINT + GEO_JSON_SUFFIX);

            JSONMapper areaJSON = URL_JSON_MAP.get(baseURL + AREA + GEO_JSON_SUFFIX);

            if(pointJSON != null && areaJSON != null){
                areaJSON.setGeoJSON(GeoJSONFactory.createGEO(jsonData.optJSONObject(0).toString()));
                pointJSON.setGeoJSON(GeoJSONFactory.createGEO(jsonData.optJSONObject(1).toString()));
                return GEOJSONHelper.writeGeoJSON(areaJSON) && GEOJSONHelper.writeGeoJSON(pointJSON);
            }
        }

        return false;

    }

    public static boolean writeGeoJSON(JSONMapper jsonMapper) {
        String writeInfo = jsonMapper.toString();
        String path = jsonMapper.getPath();
        boolean success = true;
        try {
            OutputStream out = FRContext.getCurrentEnv().writeBean(path, StringUtils.EMPTY);

            out.write(writeInfo.getBytes(EncodeConstants.ENCODING_UTF_8));
            out.flush();
            out.close();
        } catch (Exception e){
            FRContext.getLogger().error(e.getMessage(), e);
            success = false;
        }
        return success;
    }
}
