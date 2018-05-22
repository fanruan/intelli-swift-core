package com.fr.swift.conf.dashboard.store;

import com.finebi.common.internalimp.config.store.id.DefaultTableIdNameStore;
import com.fr.config.ConfigContext;
import com.fr.engine.constant.Null;
import com.fr.swift.conf.dashboard.ConfConstants;

/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardTableIdNameStore extends DefaultTableIdNameStore {
    private static DashboardTableIdNameStore INSTANCE;

    public static DashboardTableIdNameStore getInstance() {
        if (Null.isNull(INSTANCE)) {
            INSTANCE = ConfigContext.getConfigInstance(DashboardTableIdNameStore.class);
        }
        return INSTANCE;
    }

    @Override
    public String getNameSpace() {
        return ConfConstants.DASHBOARD_TABLE_ID_NAME_NAMESPACE;
    }
}
