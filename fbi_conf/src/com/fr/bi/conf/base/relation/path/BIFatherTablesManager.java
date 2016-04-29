package com.fr.bi.conf.base.relation.path;

import com.fr.bi.stable.data.Table;

/**
 * Created by Connery on 2016/1/14.
 */
class BIFatherTablesManager extends BIDirectlyRelativeTablesManager {
    public BIFatherTablesManager(BIIndirectlyRelativeTablesManager indirectTablesManager) {
        super(indirectTablesManager);
    }

    @Override
    protected BIDirectlyRelativeTableContainer generateDirectTableContainer(Table table) {
        return new BIFatherTableContainer(table);
    }

    @Override
    public void buildDirectRelation(BIDirectlyRelativeTableContainer primaryDirectContainer, BIDirectlyRelativeTableContainer foreignDirectContainer) {
        if (!foreignDirectContainer.containDirectlyRelation(primaryDirectContainer)) {
            foreignDirectContainer.addDirectlyRelativeContainer(primaryDirectContainer);
        }
    }

    @Override
    public void demolishDirectRelation(BIDirectlyRelativeTableContainer primaryDirectContainer, BIDirectlyRelativeTableContainer foreignDirectContainer) {
        if (foreignDirectContainer.containDirectlyRelation(primaryDirectContainer)) {
            foreignDirectContainer.removeDirectlyRelativeContainer(primaryDirectContainer);
        }
    }
}