package com.fr.bi.web.conf.utils;

import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.fr.bi.base.BIBusinessPackagePersistThread;
import com.fr.bi.base.BIBusinessPackagePersistThreadHolder;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;

/**
 * Created by Young's on 2016/12/20.
 */
public class BIWriteConfigResourcesUtils {

    public static void writeResources(long userId) {
        try {
            BICubeConfigureCenter.getPackageManager().persistData(userId);
            BICubeConfigureCenter.getTableRelationManager().persistData(userId);
            BICubeConfigureCenter.getAliasManager().persistData(userId);
            BICubeConfigureCenter.getDataSourceManager().persistData(userId);
            BIConfigureManagerCenter.getCubeConfManager().persistData(userId);
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
    }

    public static void writeResourceAsync(final long userId) {
        //单独的线程写业务包配置文件，web端立即返回
        BIBusinessPackagePersistThreadHolder.getInstance().getBiBusinessPackagePersistThread().triggerWork(new BIBusinessPackagePersistThread.Action() {
            @Override
            public void work() {
                writeResources(userId);
            }
        });
    }
}
