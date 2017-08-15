package com.fr.fs.mapeditor.server;

import com.fr.base.FRContext;
import com.fr.file.filetree.FileNode;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.general.GeneralContext;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by eason on 2017/8/5.
 */
public class DeprecatedGEOJSONTreeHelper {

    public static final String GEO_JSON_DIR_NAME = "geojson";

    private static DefaultMutableTreeNode areaRoot;
    private static DefaultMutableTreeNode pointRoot;
    private static DefaultMutableTreeNode imageRoot;

    public static final String POINT_ROOT_NAME = "point";
    public static final String AREA_ROOT_NAME = "area";
    public static final String IMAGE_ROOT_NAME = "image";

    private static DeprecatedGEOJSONTreeHelper helperInstance = null;

    public synchronized static DeprecatedGEOJSONTreeHelper getInstance() {
        if (helperInstance == null) {
            helperInstance = new DeprecatedGEOJSONTreeHelper();
            areaRoot = initRootNode(MapDataType.AREA);
            pointRoot = initRootNode(MapDataType.POINT);
            imageRoot = initRootNode(MapDataType.IMAGE);
        }
        return helperInstance;
    }

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            public void envChanged() {
                helperInstance.reset();
            }
        });
    }

    private synchronized static void reset() {
        helperInstance = null;
    }

    public static DefaultMutableTreeNode getNodeWithPath(String path){

        DefaultMutableTreeNode root = null;

        if(path.contains(getMapDataTypePath(MapDataType.AREA))){
            root = areaRoot;
        } else if(path.contains(getMapDataTypePath(MapDataType.POINT))){
            root = pointRoot;
        } else if(path.contains(getMapDataTypePath(MapDataType.IMAGE))){
            root = imageRoot;
        }

        Enumeration<DefaultMutableTreeNode> els = root.postorderEnumeration();
        while (els.hasMoreElements()) {
            DefaultMutableTreeNode child = els.nextElement();
            String filePath = getGeoJSONFileName(child);
            if(ComparatorUtils.equals(filePath, path)){
                return child;
            }
        }

        return (DefaultMutableTreeNode)root.getFirstChild();
    }

    //服务器那边根据区域/点/自定义图片区分
    public static DefaultMutableTreeNode initRootNode(MapDataType type) {

        String rootPath = getMapDataTypePath(type);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootPath);

        readChildFiles(new FileNode(rootPath, true), rootNode);

        return rootNode;
    }


    private static void readChildFiles(FileNode parent, DefaultMutableTreeNode parentNode) {

        try {
            FileNode[] childFiles = FRContext.getCurrentEnv().listFile(parent.getEnvPath());
            List<String> dirPathList = new ArrayList<String>();

            for(FileNode node : childFiles){
                if(node.isDirectory()){
                    dirPathList.add(node.getEnvPath());
                }
            }

            for(FileNode node : childFiles){
                if(node.isDirectory()){//文件夹
                    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(node.getEnvPath());
                    parentNode.insert(childNode, 0);
                    readChildFiles(node, childNode);
                } else if(node.getEnvPath().endsWith(GEOJSONHelper.GEO_JSON_SUFFIX)){
                    if(!hasSameNameDir(node.getEnvPath(), dirPathList)){
                        parentNode.add(new DefaultMutableTreeNode(node.getEnvPath()));
                    }
                }
            }
        }catch (Exception ex){
            FRLogger.getLogger().error(ex.getMessage());
        }

    }

    //json文件有对应的同名文件夹,则不加入树结构中.
    private static boolean hasSameNameDir(String nodeName, List<String> dirPathList){
        for(String dirName : dirPathList){
            if(ComparatorUtils.equals(nodeName.replace(GEOJSONHelper.GEO_JSON_SUFFIX, StringUtils.EMPTY), dirName)){
                return true;
            }
        }
        return false;
    }

    //服务器那边根据区域/点/自定义图片根节点路径
    public static String getMapDataTypePath(MapDataType type){
        String holderName;
        switch (type) {
            case AREA:
                holderName = AREA_ROOT_NAME;
                break;
            case POINT:
                holderName = POINT_ROOT_NAME;
                break;
            case IMAGE:
                holderName = IMAGE_ROOT_NAME;
                break;
            default:
                holderName = AREA_ROOT_NAME;
        }

        return StableUtils.pathJoin(new String[]{ProjectConstants.RESOURCES_NAME, GEO_JSON_DIR_NAME, holderName});
    }


    public static DefaultMutableTreeNode getAreaRootNode() {
        return areaRoot;
    }

    //非叶子节点,根据文件夹名返回对应的json文件
    public static String getGeoJSONFileName(DefaultMutableTreeNode node){
        if(node == null){
            return StringUtils.EMPTY;
        }
        String path = node.getUserObject().toString();

        return StringUtils.contains(path, GEOJSONHelper.GEO_JSON_SUFFIX) ? path : (path + GEOJSONHelper.GEO_JSON_SUFFIX);
    }
}
