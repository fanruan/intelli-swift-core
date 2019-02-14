package com.fr.swift.conf.dashboard.store;

import com.finebi.common.internalimp.config.store.fieldinfo.CubeFieldInfoDBStore;
import com.fr.config.ConfigContext;
import com.fr.swift.conf.dashboard.ConfConstants;

/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardFieldInfoStore extends CubeFieldInfoDBStore {
    @Override
    public String getNameSpace() {
        return ConfConstants.DASHBOARD_FIELD_INFO_NAMESPACE;
    }

    private static DashboardFieldInfoStore fieldInfoStore;

    public static DashboardFieldInfoStore getFieldInfoStore() {
        if (null == fieldInfoStore) {
            fieldInfoStore = ConfigContext.getConfigInstance(DashboardFieldInfoStore.class);
        }
        return fieldInfoStore;
    }

}
