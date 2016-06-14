package com.finebi.cube.relation;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class BITableSourceRelationPath4Incremetal extends BITableSourceRelationPath {
    protected Set<BITableSourceRelation> biTableSourceRelation;

    public BITableSourceRelationPath4Incremetal(BITableSourceRelationPath biTableSourceRelationPath, Set<BITableSourceRelation> biTableSourceRelation) {
        this.biTableSourceRelation = biTableSourceRelation;
        super.setContainer(biTableSourceRelationPath.getAllRelations());
    }

    public Set<BITableSourceRelation> getBiTableSourceRelation() {
        return biTableSourceRelation;
    }
}