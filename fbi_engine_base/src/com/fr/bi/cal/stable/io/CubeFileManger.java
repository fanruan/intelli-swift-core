package com.fr.bi.cal.stable.io;


import com.fr.bi.stable.utils.file.BIFileUtils;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.io.File;

/**
 * cube的一些文件操作
 * Created by GUY on 2015/3/23.
 */
public class CubeFileManger {

    public static void deleteTempFile(long userId) {
        BIFileUtils.delete(new File(BIPathUtils.createUserTotalTempPath(userId)));
    }
}