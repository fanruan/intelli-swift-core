package com.fr.swift.conf.dashboard.store;

import com.finebi.common.internalimp.config.store.relation.CubeRelationConfigurationDBStore;
import com.fr.config.ConfigContext;
import com.fr.swift.conf.dashboard.ConfConstants;

/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardRelationStore extends CubeRelationConfigurationDBStore {
    @Override
    public String getNameSpace() {
        return ConfConstants.DASHBOARD_RELATION_NAMESPACE;
    }

    private static DashboardRelationStore relationStore;

    public static DashboardRelationStore getRelationStore() {
        if (null == relationStore) {
            relationStore = ConfigContext.getConfigInstance(DashboardRelationStore.class);
        }
        return relationStore;
    }
}
