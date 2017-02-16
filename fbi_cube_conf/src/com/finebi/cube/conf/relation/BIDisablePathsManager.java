package com.finebi.cube.conf.relation;

import com.finebi.cube.relation.BITableRelation;
import com.finebi.cube.relation.BITableRelationPath;
import com.fr.bi.stable.exception.BITablePathAbsentException;
import com.fr.bi.stable.exception.BITablePathDuplicationException;

import java.util.List;
import java.util.Set;


/**
 * Created by Connery on 2016/1/15.
 */
public class BIDisablePathsManager {
    private BIDisablePathContainer disablePathContainer;

    public BIDisablePathsManager() {
        disablePathContainer = new BIDisablePathContainer();
    }

    public boolean isPathDisable(BITableRelationPath path) {
        List<BITableRelation> relationList = path.getAllRelations();

        Set<BITableRelationPath> disablePaths = disablePathContainer.getContainer();
        for (BITableRelationPath disablePath : disablePaths) {
            List<BITableRelation> disableRelationList = disablePath.getAllRelations();
            if (relationList.containsAll(disableRelationList)) {
                return true;
            }
        }
        return false;
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