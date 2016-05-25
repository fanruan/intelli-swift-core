package com.fr.bi.conf.provider;

import com.fr.stable.bridge.StableFactory;

/**
 * BI的配置模块的接口
 * Created by GUY on 2015/4/2.
 *
 * @author Connery
 */
public class BIConfigureManagerCenter {


    /**
     * 获取角色权限接口
     */
    public static BIAuthorityManageProvider getAuthorityManager() {
        return StableFactory.getMarkedObject(BIAuthorityManageProvider.XML_TAG, BIAuthorityManageProvider.class);
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
        return StableFactory.getMarkedObject(BIUpdateFrequencyManagerProvider.XML_TAG, BIUpdateFrequencyManagerProvider.class);
    }

    public static BICubeConfManagerProvider getCubeConfManager() {
        return StableFactory.getMarkedObject(BICubeConfManagerProvider.XML_TAG, BICubeConfManagerProvider.class);
    }
}
