package com.fr.bi.web.conf.utils;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.api.ICubeTableService;
import com.fr.bi.stable.data.source.CubeTableSource;

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

        while (it.hasNext()) {
            CubeTableSource key = it.next();
            ICubeTableService cube = loader.getTableIndex(key);
//            if (!cube.getTableVersion()) {
//                return false;
//            }
        }

        return true;
    }
}