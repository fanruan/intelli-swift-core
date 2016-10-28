package com.finebi.cube.utils;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.finebi.cube.common.log.BILoggerFactory;

import java.util.Iterator;

/**
 * Created by 小灰灰 on 2015/12/22.
 */
public class BIWebConfUtils {
    public static boolean checkCubeVersion(CubeTableSource source, long userId) {
        if (source == null) {
            return false;
        }
        ICubeDataLoader loader = BICubeManager.getInstance().fetchCubeLoader(userId);
        Iterator<CubeTableSource> it = source.createSourceMap().values().iterator();
        try {
            while (it.hasNext()) {
                CubeTableSource key = it.next();
                loader.getTableIndex(key);
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
            return false;
        }
        return true;
    }
}