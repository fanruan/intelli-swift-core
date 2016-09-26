package com.fr.bi;

import com.fr.bi.cal.stable.loader.CubeReadingTableIndexLoader;
import com.fr.fs.control.UserControl;

/**
 * This class created on 2016/9/19.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class CubeReaderCacheUtils {
    public static void clearCubeReader() {
        CubeReadingTableIndexLoader.getInstance(UserControl.getInstance().getSuperManagerID()).clear();
    }

    public static String clearUserMapCache() {
       return   ((CubeReadingTableIndexLoader) CubeReadingTableIndexLoader.getInstance(UserControl.getInstance().getSuperManagerID())).clearUserMapCache();
    }
}
