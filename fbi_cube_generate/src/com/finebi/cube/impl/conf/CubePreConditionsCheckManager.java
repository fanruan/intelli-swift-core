package com.finebi.cube.impl.conf;

import com.finebi.cube.api.BICubeManager;
import com.finebi.cube.conf.CubePreConditionsCheck;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by kary on 2016/6/20.
 */
public class CubePreConditionsCheckManager implements CubePreConditionsCheck {

    @Override
    public boolean HDSpaceCheck(File file) {
        if (!file.exists()) {
            return true;
        }
        double dirSize = getDirSize(file);
        double availableSpace = getAvailableSpace(file);
        return (availableSpace > dirSize * 2);
    }

    @Override
    public Map<CubeTableSource, Boolean> ConnectionCheck(Set<CubeTableSource> cubeTableSourceSet, long userId) {
        Map tableSourceSqlValidMap = new HashMap<CubeTableSource,Boolean>();
        for (CubeTableSource source : cubeTableSourceSet) {
            if (source.getType() == BIBaseConstant.TABLETYPE.DB || source.getType() == BIBaseConstant.TABLETYPE.SQL) {
                boolean isSqlValid = true;
                try {
                    source.createPreviewJSON(new ArrayList<String>(), BICubeManager.getInstance().fetchCubeLoader(userId), userId);
                } catch (Exception e) {
                    isSqlValid = false;
                    BILogger.getLogger().error(e.getMessage(), e);
                }
                tableSourceSqlValidMap.put(source, isSqlValid);
            }
        }
        return  tableSourceSqlValidMap;
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

    private double getAvailableSpace(File file) {
        return file.getFreeSpace();
    }
}
