package com.fr.swift.segment;

import java.io.File;

/**
 * Created by Handsome on 2017/12/25 0025 13:58
 */
public interface DiskSpaceManager {

    /**
     * cube已占空间
     *
     * @param file
     * @return
     */
    double getCubeSize(File file);

    /**
     * 即将占用的空间
     *
     * @param file
     * @return
     */
    double getSpaceOfTakingup(File file);


    /**
     * 当前剩余的空间
     *
     * @param file
     * @return
     */

    double getCurrentLeftSpace(File file);
}