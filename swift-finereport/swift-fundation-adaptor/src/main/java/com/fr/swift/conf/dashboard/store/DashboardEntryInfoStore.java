package com.fr.swift.conf.dashboard.store;

import com.finebi.common.internalimp.config.store.entryinfo.CubeEntryInfoDBStore;
import com.fr.config.ConfigContext;
import com.fr.swift.conf.dashboard.ConfConstants;

/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardEntryInfoStore extends CubeEntryInfoDBStore {
    private static DashboardEntryInfoStore entryInfoDbStore;

    @Override
    public String getNameSpace() {
        return ConfConstants.DASHBOARD_ENTRY_INFO_NAMESPACE;
    }

    public static DashboardEntryInfoStore getEntryInfoDbStore() {
        if (entryInfoDbStore == null) {
            entryInfoDbStore = ConfigContext.getConfigInstance(DashboardEntryInfoStore.class);
        }
        return entryInfoDbStore;
    }
}
