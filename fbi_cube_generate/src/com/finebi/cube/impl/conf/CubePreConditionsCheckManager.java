package com.finebi.cube.impl.conf;

import com.finebi.cube.conf.CubePreConditionsCheck;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.io.File;

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
    public boolean ConnectionCheck(CubeTableSource source, long userId) {
//        boolean isSqlValid = true;
//            if (source.getType() == BIBaseConstant.TABLETYPE.DB || source.getType() == BIBaseConstant.TABLETYPE.SQL) {
//                try {
//                    source.createPreviewJSON(new ArrayList<String>(), BICubeManager.getInstance().fetchCubeLoader(userId), userId);
//                } catch (Exception e) {
//                    isSqlValid = false;
//                    BILoggerFactory.getLogger().error(e.getMessage(), e);
//                }
//            }
//        return isSqlValid;
        return source.canExecute();
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
