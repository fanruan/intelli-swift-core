package com.fr.bi.web.conf.utils;

import com.fr.bi.base.BIUser;
import com.fr.bi.cal.stable.cube.file.TableCubeFile;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.utils.file.BIPathUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/22.
 */
public class BIWebConfUtils {
    public static boolean checkCubeVersion(ICubeTableSource source, long userId) {
        if (source == null || BIConfigureManagerCenter.getDataSourceManager().getTableSourceByCore(source.fetchObjectCore(), new BIUser(userId)) == null) {
            return false;
        }
        Set<Table> keys = source.createTableKeys();
        Iterator<Table> it = keys.iterator();
        while (it.hasNext()) {
            Table key = it.next();
            TableCubeFile cube = new TableCubeFile(BIPathUtils.createTablePath(key.getID().getIdentityValue(), userId));
            if (!cube.checkCubeVersion()) {
                return false;
            }
        }
        return true;
    }
}