package com.fr.swift.conf.dashboard.store;

import com.finebi.common.internalimp.config.store.pack.CubePackageInfoDBStore;
import com.fr.config.ConfigContext;
import com.fr.swift.conf.dashboard.ConfConstants;


/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardPackageInfoStore extends CubePackageInfoDBStore {
    @Override
    public String getNameSpace() {
        return ConfConstants.DASHBOARD_PACKAGE_INFO_NAMESPACE;
    }

    private static DashboardPackageInfoStore packageInfoDBStore;

    public static DashboardPackageInfoStore getPackageInfoDBStore() {
        if (packageInfoDBStore == null) {
            packageInfoDBStore = ConfigContext.getConfigInstance(DashboardPackageInfoStore.class);
        }
        return packageInfoDBStore;
    }
}
