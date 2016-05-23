package com.finebi.cube.conf.relation.path;

import com.finebi.cube.conf.table.IBusinessTable;

/**
 * Created by Connery on 2016/1/14.
 */
public class BISonTablesManager extends BIDirectlyRelativeTablesManager {


    BIJuniorTablesManager juniorTablesManager;

    public BISonTablesManager(BIJuniorTablesManager juniorTablesManager) {
        super(juniorTablesManager);
    }

    @Override
    protected BIDirectlyRelativeTableContainer generateDirectTableContainer(IBusinessTable table) {
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