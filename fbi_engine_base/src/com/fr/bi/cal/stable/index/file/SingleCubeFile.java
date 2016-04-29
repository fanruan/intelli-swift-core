package com.fr.bi.cal.stable.index.file;

import java.io.File;

/**
 * Cube文件接口
 *
 * @author Daniel
 */
public interface SingleCubeFile {

    /**
     * Cube文件的路径
     * @return 文件路径
     */
     String getPath();

    /**
     * 生成文件
     * @return 生成的cube文件
     */
     File createFile();
}