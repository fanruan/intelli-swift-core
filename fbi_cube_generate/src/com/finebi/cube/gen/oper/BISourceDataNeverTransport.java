package com.finebi.cube.gen.oper;

import com.finebi.cube.structure.Cube;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Set;

/**
 * Created by wuk on 16/7/13.
 */
public class BISourceDataNeverTransport extends BISourceDataTransport{
    public BISourceDataNeverTransport(Cube cube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, long version) {
        super(cube, tableSource, allSources, parentTableSource, version);
    }
}
