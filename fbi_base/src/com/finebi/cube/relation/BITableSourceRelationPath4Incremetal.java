package com.finebi.cube.relation;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 *
 */
public class BITableSourceRelationPath4Incremetal {
    protected BITableSourceRelationPath biTableSourceRelationPath;
    protected Set<BITableSourceRelation> biTableSourceRelation;

    public BITableSourceRelationPath4Incremetal(BITableSourceRelationPath biTableSourceRelationPath, Set<BITableSourceRelation> biTableSourceRelation) {
        this.biTableSourceRelationPath = biTableSourceRelationPath;
        this.biTableSourceRelation = biTableSourceRelation;
    }

    public String getSourceID() {
       return biTableSourceRelationPath.getSourceID();
    }

    public BITableSourceRelationPath getBiTableSourceRelationPath() {
        return biTableSourceRelationPath;
    }

    public Set<BITableSourceRelation> getBiTableSourceRelation() {
        return biTableSourceRelation;
    }
}