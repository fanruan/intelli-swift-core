package com.finebi.cube.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/4/6.
 *
 * @author Connery
 * @since 4.0
 */
public class BITableKeyUtils {
    public static ITableKey convert(CubeTableSource tableSource) {
        return new BITableKey(tableSource);
    }

    public static boolean isTableExisted(CubeTableSource tableSource,ICubeConfiguration cubeConfiguration) {
        BICube iCube = new BICube(new BICubeResourceRetrieval(cubeConfiguration), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        ITableKey iTableKey = new BITableKey(tableSource);
        return iCube.exist(iTableKey);
    }

}
