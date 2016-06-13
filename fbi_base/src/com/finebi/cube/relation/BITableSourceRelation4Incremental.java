package com.finebi.cube.relation;

import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * Created by kary on 2016/6/13.
 */
public class BITableSourceRelation4Incremental {
    protected BITableSourceRelation biTableSourceRelation;
    //已经生成完成的tableSource
    protected CubeTableSource cubeTableSource;

    public BITableSourceRelation4Incremental(BITableSourceRelation biTableSourceRelation, CubeTableSource cubeTableSource) {
        this.biTableSourceRelation = biTableSourceRelation;
        this.cubeTableSource = cubeTableSource;
    }

    public BITableSourceRelation getBiTableSourceRelation() {
        return biTableSourceRelation;
    }

    public CubeTableSource getCubeTableSource() {
        return cubeTableSource;
    }
}
