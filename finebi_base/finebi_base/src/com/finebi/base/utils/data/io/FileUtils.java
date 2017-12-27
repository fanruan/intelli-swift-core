package com.finebi.base.utils.data.io;

import com.finebi.log.BILoggerFactory;
import com.fr.stable.StableUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by andrew_asa on 2017/12/21.
 */
public class FileUtils {

    /**
     * 新建文件，旧文件删除
     *
     * @param f
     */
    public static void createFile(File f) {

        delete(f);
        try {
            createDirs(f.getParentFile());
            f.createNewFile();
        } catch (IOException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
        }
    }

    public static boolean delete(File f) {

        return StableUtils.deleteFile(f);
    }

    public static void createDirs(File f) {

        StableUtils.mkdirs(f);
    }
}
