package com.fr.bi.conf.provider;

import com.fr.bi.conf.manager.update.BIUpdateSettingManager;
import com.fr.stable.bridge.StableFactory;

/**
 * BI的配置模块的接口
 * Created by GUY on 2015/4/2.
 *
 * @author Connery
 */
public class BIConfigureManagerCenter {

    /**
     * 获得数据源的配置接口
     *
     * @return 数据源的配置接口
     */
    public static BIDataSourceManagerProvider getDataSourceManager() {
        return StableFactory.getMarkedObject(BIDataSourceManagerProvider.XML_TAG, BIDataSourceManagerProvider.class);
    }

    /**
     * 获得业务包接口
     *
     * @return 业务包接口
     */
    public static BISystemPackageConfigurationProvider getPackageManager() {
        return StableFactory.getMarkedObject(BISystemPackageConfigurationProvider.XML_TAG, BISystemPackageConfigurationProvider.class);
    }
   /** 获取角色权限接口*/
    public static BIAuthorityManageProvider getAuthorityManager() {
        return StableFactory.getMarkedObject(BIAuthorityManageProvider.XML_TAG, BIAuthorityManageProvider.class);
    }
    /**
     * 获得表关联的接口
     * @return 表关联的接口
     */
    public static BITableRelationConfigurationProvider getTableRelationManager() {
        return StableFactory.getMarkedObject(BITableRelationConfigurationProvider.XML_TAG, BITableRelationConfigurationProvider.class);
    }

    public static BICubeManagerProvider getCubeManager() {
        return StableFactory.getMarkedObject(BICubeManagerProvider.XML_TAG, BICubeManagerProvider.class);
    }
    /**
     * 获得别名的接口
     * @return 别名的接口
     */
    public static BIAliasManagerProvider getAliasManager() {
        return StableFactory.getMarkedObject(BIAliasManagerProvider.XML_TAG, BIAliasManagerProvider.class);
    }

    public static BILogManagerProvider getLogManager() {
        return StableFactory.getMarkedObject(BILogManagerProvider.XML_TAG, BILogManagerProvider.class);
    }

    public static BIUserLoginInformationProvider getUserLoginInformationManager() {
        return StableFactory.getMarkedObject(BIUserLoginInformationProvider.XML_TAG, BIUserLoginInformationProvider.class);
    }

    public static ICubeGeneratorConfigure getCubeGeneratorCofiguration() {
        return StableFactory.getMarkedObject(ICubeGeneratorConfigure.XML_TAG, ICubeGeneratorConfigure.class);
    }

    public static BIExcelViewManagerProvider getExcelViewManager() {
        return StableFactory.getMarkedObject(BIExcelViewManagerProvider.XML_TAG, BIExcelViewManagerProvider.class);
    }

    public static BIUpdateFrequencyManagerProvider getUpdateFrequencyManager() {
        return StableFactory.getMarkedObject(BIUpdateFrequencyManagerProvider.XML_TAG, BIUpdateSettingManager.class);
    }
}
