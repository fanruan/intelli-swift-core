package com.finebi.cube.relation;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class BICubeGenerateRelation {
    protected Set<CubeTableSource> dependTableSourceSet;
    protected BITableSourceRelation relation;

    public BICubeGenerateRelation(BITableSourceRelation biTableSourceRelation, Set<CubeTableSource> cubeTableSourceSet) {
        this.relation = biTableSourceRelation;
        this.dependTableSourceSet = cubeTableSourceSet;
    }

    public Set<CubeTableSource> getDependTableSourceSet() {
        return dependTableSourceSet;
    }

    public BITableSourceRelation getRelation() {
        return relation;
    }
}
