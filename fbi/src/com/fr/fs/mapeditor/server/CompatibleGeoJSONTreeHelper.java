package com.fr.fs.mapeditor.server;

import com.fr.plugin.chart.map.designer.type.GEOJSONTreeHelper;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by eason on 2017/8/5.
 */
public class CompatibleGeoJSONTreeHelper {

    public static DefaultMutableTreeNode getNodeByJSONPath(String jsonURL){

        return CompatibleGEOJSONHelper.isDeprecated(jsonURL) ? DeprecatedGEOJSONTreeHelper.getInstance().getNodeWithPath(jsonURL) : GEOJSONTreeHelper.getInstance().getNodeByJSONPath(jsonURL);

    }


    public static DefaultMutableTreeNode getRootNodeWithoutPara(String url){
        return CompatibleGEOJSONHelper.isDeprecated(url) ? DeprecatedGEOJSONTreeHelper.getInstance().getAreaRootNode() : GEOJSONTreeHelper.getInstance().getRootNodeWithoutPara();
    }

}
