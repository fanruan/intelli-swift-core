package com.finebi.cube.relation;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class BICubeGenerateRelationPath {
   protected BITableSourceRelationPath biTableSourceRelationPath;
    protected Set<BITableSourceRelationPath> dependRelationPath;

    public BITableSourceRelationPath getBiTableSourceRelationPath() {
        return biTableSourceRelationPath;
    }

    public Set<BITableSourceRelationPath> getDependRelationPath() {
        return dependRelationPath;
    }

    public BICubeGenerateRelationPath(BITableSourceRelationPath biTableSourceRelationPath, Set<BITableSourceRelationPath> dependRelationPath) {

        this.biTableSourceRelationPath = biTableSourceRelationPath;
        this.dependRelationPath = dependRelationPath;
    }
}