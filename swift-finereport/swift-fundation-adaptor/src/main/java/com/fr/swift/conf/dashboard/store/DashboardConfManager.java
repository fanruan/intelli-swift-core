package com.fr.swift.conf.dashboard.store;

import com.finebi.base.constant.FineEngineType;
import com.finebi.common.internalimp.config.session.FieldSessionImpl;
import com.finebi.common.internalimp.config.session.PackageSessionImpl;
import com.finebi.common.internalimp.config.session.RelationConfigurationSessionImpl;
import com.finebi.common.structure.config.session.EntryInfoSession;
import com.finebi.common.structure.config.session.FieldSession;
import com.finebi.common.structure.config.session.PackageSession;
import com.finebi.common.structure.config.session.RelationConfigurationSession;
import com.fr.swift.conf.dashboard.session.DashboardEntryInfoSession;

/**
 * @author yee
 * @date 2018/5/22
 */
public class DashboardConfManager {
    private static DashboardConfManager manager;
    private PackageSession packageSession;
    private EntryInfoSession entryInfoSession;
    private RelationConfigurationSession relationConfigurationSession;
    private FieldSession fieldSession;


    private DashboardConfManager() {
        packageSession = new PackageSessionImpl(DashboardPackageInfoStore.getPackageInfoDBStore(), FineEngineType.Cube);
        entryInfoSession = new DashboardEntryInfoSession(DashboardEntryInfoStore.getEntryInfoDbStore());
        relationConfigurationSession = new RelationConfigurationSessionImpl(DashboardRelationStore.getRelationStore(), FineEngineType.Cube);
        relationConfigurationSession.refresh();
        fieldSession = new FieldSessionImpl(DashboardFieldInfoStore.getFieldInfoStore(), FineEngineType.Cube);
    }

    public static DashboardConfManager getManager() {
        if (null == manager) {
            synchronized (DashboardConfManager.class) {
                manager = new DashboardConfManager();
            }
        }
        return manager;
    }

    public PackageSession getPackageSession() {
        return packageSession;
    }

    public EntryInfoSession getEntryInfoSession() {
        return entryInfoSession;
    }

    public RelationConfigurationSession getRelationConfigurationSession() {
        return relationConfigurationSession;
    }

    public FieldSession getFieldSession() {
        return fieldSession;
    }
}
