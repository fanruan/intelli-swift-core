package com.finebi.cube.relation;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.HashSet;
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

    public BICubeGenerateRelation(BITableSourceRelation relation) {
        this.relation = relation;
        this.dependTableSourceSet = new HashSet<CubeTableSource>();
        this.dependTableSourceSet.add(relation.getForeignTable());
        this.dependTableSourceSet.add(relation.getPrimaryTable());
    }

    public Set<CubeTableSource> getDependTableSourceSet() {
        return dependTableSourceSet;
    }

    public BITableSourceRelation getRelation() {
        return relation;
    }
}
