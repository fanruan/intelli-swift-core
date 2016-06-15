package com.fr.bi.util;

import com.fr.base.FRContext;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;

import java.io.File;

/**
 * Created by Young's on 2016/5/19.
 */
public class BIConfigurePathUtils {

    private static final String CUBE = "cubes";
    private final static String BASEPATH = File.separator + ProjectConstants.RESOURCES_NAME + File.separator + CUBE;

    /**
     * base路径
     *
     * @return base路径
     */
    public static String createBasePath() {
        String cubePath = BIConfigureManagerCenter.getCubeConfManager().getCubePath();
        return cubePath == null ? FRContext.getCurrentEnv().getPath() + BASEPATH : cubePath;
    }

    public static String getProjectLibPath() {
        return FRContext.getCurrentEnv().getPath() + File.separator + "lib";
    }

    public static String checkCubePath(String cubePath) {
        if (StringUtils.isEmpty(cubePath)) {
            return "";
        }
        String oldPath = BIConfigureManagerCenter.getCubeConfManager().getCubePath();
        if (ComparatorUtils.equals(cubePath, oldPath)) {
            return oldPath;
        }
        File file = new File(cubePath);
        if (!file.exists()) {
            try {
                file.mkdirs();
                return file.getAbsolutePath();
            } catch (Exception e) {
                return "";
            } finally {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (!file.isDirectory() || file.list().length > 0) {
            return "";
        }
        return file.getAbsolutePath();
    }
}
