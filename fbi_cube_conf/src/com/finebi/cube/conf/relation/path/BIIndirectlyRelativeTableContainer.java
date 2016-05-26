package com.finebi.cube.conf.relation.path;

/**
 * Created by Connery on 2016/1/14.
 */
class BIIndirectlyRelativeTableContainer extends BITableContainer {

    protected void copyFrom(BITableContainer indirectlyRelativeTableContainer) {
        container.addAll(indirectlyRelativeTableContainer.getContainer());
    }
}