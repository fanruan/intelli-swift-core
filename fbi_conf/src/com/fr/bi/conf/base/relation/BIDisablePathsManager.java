package com.fr.bi.conf.base.relation;

import com.fr.bi.stable.exception.BITablePathAbsentException;
import com.fr.bi.stable.exception.BITablePathDuplicationException;
import com.fr.bi.stable.relation.BITableRelationPath;


/**
 * Created by Connery on 2016/1/15.
 */
public class BIDisablePathsManager {
    private BIDisablePathContainer disablePathContainer;

    public BIDisablePathsManager() {
        disablePathContainer = new BIDisablePathContainer();
    }

    public boolean isPathDisable(BITableRelationPath path) {
        return disablePathContainer.contain(path);
    }

    public void addDisabledPath(BITableRelationPath path) throws BITablePathDuplicationException {
        if (!disablePathContainer.contain(path)) {
            disablePathContainer.add(path);
        } else {
            throw new BITablePathDuplicationException();
        }
    }

    public void removeDisablePath(BITableRelationPath path) throws BITablePathAbsentException {
        if (disablePathContainer.contain(path)) {
            disablePathContainer.remove(path);
        } else {
            throw new BITablePathAbsentException();
        }
    }


}