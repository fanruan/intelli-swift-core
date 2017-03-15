package com.finebi.cube.conf.relation.path;

/**
 * Created by Connery on 2016/1/14.
 */
class BIJuniorTablesManager extends BIIndirectlyRelativeTablesManager {


    @Override
    protected BIDirectlyRelativeTablesManager generateDirectlyRelativeManager() {
        return new BISonTablesManager(this);
    }

    @Override
    protected BIIndirectlyRelativeTableContainer generateIndirectlyRelativeContainer() {
        return new BIJuniorTableContainer();
    }
}