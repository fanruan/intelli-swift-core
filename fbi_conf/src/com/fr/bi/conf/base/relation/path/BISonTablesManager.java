package com.fr.bi.conf.base.relation.path;

import com.fr.bi.stable.data.Table;

/**
 * Created by Connery on 2016/1/14.
 */
public class BISonTablesManager extends BIDirectlyRelativeTablesManager {


    BIJuniorTablesManager juniorTablesManager;

    public BISonTablesManager(BIJuniorTablesManager juniorTablesManager) {
        super(juniorTablesManager);
    }

    @Override
    protected BIDirectlyRelativeTableContainer generateDirectTableContainer(Table table) {
        return new BISonTableContainer(table);
    }

    @Override
    public void buildDirectRelation(BIDirectlyRelativeTableContainer primaryDirectContainer, BIDirectlyRelativeTableContainer foreignDirectContainer) {
        if (!primaryDirectContainer.containDirectlyRelation(foreignDirectContainer)) {
            primaryDirectContainer.addDirectlyRelativeContainer(foreignDirectContainer);
        }
    }

    @Override
    public void demolishDirectRelation(BIDirectlyRelativeTableContainer primaryDirectContainer, BIDirectlyRelativeTableContainer foreignDirectContainer) {
        if (primaryDirectContainer.containDirectlyRelation(foreignDirectContainer)) {
            primaryDirectContainer.removeDirectlyRelativeContainer(foreignDirectContainer);
        }
    }
}