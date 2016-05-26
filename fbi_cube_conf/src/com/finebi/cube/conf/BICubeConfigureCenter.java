package com.finebi.cube.conf;

import com.finebi.cube.conf.singletable.SingleTableUpdateManager;
import com.finebi.cube.conf.timer.UpdateFrequencyManager;
import com.fr.stable.bridge.StableFactory;

/**
 * BI的配置模块的接口
 * Created by GUY on 2015/4/2.
 *
 * @author Connery
 */
public class BICubeConfigureCenter {


    /**
     * 获得业务包接口
     *
     * @return 业务包接口
     */
    public static BISystemPackageConfigurationProvider getPackageManager() {
        return StableFactory.getMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, BISystemPackageConfigurationProvider.class);
    }

    /**
     * 获得表关联的接口
     *
     * @return 表关联的接口
     */
    public static BITableRelationConfigurationProvider getTableRelationManager() {
        return StableFactory.getMarkedObject(BITableRelationConfigurationProvider.XML_TAG, BITableRelationConfigurationProvider.class);
    }

    /**
     * 获得别名的接口
     *
     * @return 别名的接口
     */
    public static BIAliasManagerProvider getAliasManager() {
        return StableFactory.getMarkedObject(BIAliasManagerProvider.XML_TAG, BIAliasManagerProvider.class);
    }

    /**
     * 获得数据源的配置接口
     *
     * @return 数据源的配置接口
     */
    public static BIDataSourceManagerProvider getDataSourceManager() {
        return StableFactory.getMarkedObject(BIDataSourceManagerProvider.XML_TAG, BIDataSourceManagerProvider.class);
    }

    public static BICubeManagerProvider getCubeManager() {
        return StableFactory.getMarkedObject(BICubeManagerProvider.XML_TAG, BICubeManagerProvider.class);
    }

    public static SingleTableUpdateManager getTableUpdateManager() {
        return StableFactory.getMarkedObject(SingleTableUpdateManager.XML_TAG, SingleTableUpdateManager.class);
    }

    public static UpdateFrequencyManager getTableUpdateFreguency() {
        return StableFactory.getMarkedObject(UpdateFrequencyManager.XML_TAG, UpdateFrequencyManager.class);
    }
}
