package com.finebi.cube.relation;

import com.fr.bi.stable.exception.BITablePathEmptyException;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class BICubeGenerateRelationPath {
    private BITableSourceRelationPath biTableSourceRelationPath;
    private Set<BITableSourceRelationPath> dependRelationPathSet;

    public BITableSourceRelationPath getBiTableSourceRelationPath() {
        return biTableSourceRelationPath;
    }

    public Set<BITableSourceRelationPath> getDependRelationPathSet() {
        return dependRelationPathSet;
    }

    public BICubeGenerateRelationPath(BITableSourceRelationPath biTableSourceRelationPath, Set<BITableSourceRelationPath> dependRelationPath) {
        this.biTableSourceRelationPath = biTableSourceRelationPath;
        this.dependRelationPathSet = dependRelationPath;
    }

    public BICubeGenerateRelationPath(BITableSourceRelationPath biTableSourceRelationPath) {
        this.biTableSourceRelationPath = biTableSourceRelationPath;
        try {
            dependRelationPathSet=new HashSet<BITableSourceRelationPath>();
            dependRelationPathSet.add(new BITableSourceRelationPath(biTableSourceRelationPath.getLastRelation()));
            BITableSourceRelationPath copyPath = new BITableSourceRelationPath();
            copyPath.copyFrom(biTableSourceRelationPath);
            copyPath.removeLastRelation();
            dependRelationPathSet.add(copyPath);
        } catch (BITablePathEmptyException e) {
            BILogger.getLogger().error(e.getMessage());
        }

    }
}
