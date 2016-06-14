package com.finebi.cube.relation;

import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by kary on 2016/6/13.
 */
public class BITableSourceRelation4Incremental extends BITableSourceRelation {
    protected Set<CubeTableSource> cubeTableSourceSet;

    public BITableSourceRelation4Incremental(BITableSourceRelation biTableSourceRelation, Set<CubeTableSource> cubeTableSourceSet) {
        super(biTableSourceRelation.getPrimaryField(), biTableSourceRelation.getForeignField(), biTableSourceRelation.getPrimaryTable(), biTableSourceRelation.getForeignTable());
        this.cubeTableSourceSet = cubeTableSourceSet;
    }

    public Set<CubeTableSource> getCubeTableSourceSet() {
        return cubeTableSourceSet;
    }
}
