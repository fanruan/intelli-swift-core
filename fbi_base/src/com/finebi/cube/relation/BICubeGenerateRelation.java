package com.finebi.cube.relation;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class BICubeGenerateRelation {
    protected Set<CubeTableSource> cubeTableSourceSet;
    protected BITableSourceRelation biTableSourceRelation;

    public BICubeGenerateRelation(BITableSourceRelation biTableSourceRelation, Set<CubeTableSource> cubeTableSourceSet) {
        this.biTableSourceRelation = biTableSourceRelation;
        this.cubeTableSourceSet = cubeTableSourceSet;
    }

    public Set<CubeTableSource> getCubeTableSourceSet() {
        return cubeTableSourceSet;
    }

    public BITableSourceRelation getBiTableSourceRelation() {
        return biTableSourceRelation;
    }
}
