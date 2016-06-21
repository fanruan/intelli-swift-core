package com.finebi.cube.structure.table;

import com.finebi.cube.BICubeTestBase;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.tools.*;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.code.BILogger;

import java.util.HashSet;
import java.util.Set;

/**
 * This class created on 2016/6/20.
 *
 * @author Connery
 * @since 4.0
 */
public class CompoundCubeTableReaderTest extends BICubeTestBase {
    private BIMemDataSourceTestToolCube tableSource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tableSource = new BIMemDataSourceTestToolCube();
    }


}
