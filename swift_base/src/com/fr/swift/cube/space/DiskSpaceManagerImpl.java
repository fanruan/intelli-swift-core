package com.fr.swift.cube.space;

import com.fr.swift.setting.PerformancePlugManager;

import java.io.File;

/**
 * Created by Handsome on 2017/12/25 0025 14:17
 */
public class DiskSpaceManagerImpl implements DiskSpaceManager {

    @Override
    public double getCubeSize(File file) {
        if (!file.exists()) {
            return 0;
        }
        return getDirSize(file);
    }

    @Override
    public double getSpaceOfTakingup(File file) {

        return getCubeSize(file) * PerformancePlugManager.getInstance().getMinCubeFreeHDSpaceRate();
    }

    @Override
    public double getCurrentLeftSpace(File file) {
        return file.getFreeSpace();
    }

    private double getDirSize(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            double size = 0;
            if (null != children) {
                for (File f : children) {
                    size += getDirSize(f);
                }
            }
            return size;
        } else {
            double size = file.length();
            return size;
        }
    }
}
