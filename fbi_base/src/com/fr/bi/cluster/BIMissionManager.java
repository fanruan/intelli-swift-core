package com.fr.bi.cluster;

import com.fr.json.JSONCreator;

import java.util.HashSet;

/**
 * Created by Connery on 2015/4/2.
 */
public interface BIMissionManager {

    void prepareMissions(int taskName, HashSet<JSONCreator> ingredient);

    void startBuildCube(String basePath, String tmpPath, long userId);
}