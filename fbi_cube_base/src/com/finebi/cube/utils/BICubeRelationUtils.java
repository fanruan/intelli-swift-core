package com.finebi.cube.utils;

import com.finebi.cube.ICubeConfiguration;
import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.location.BICubeResourceRetrieval;
import com.finebi.cube.relation.BITableSourceRelation;
import com.finebi.cube.structure.BICube;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.ICubeFieldSource;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationUtils {
    public static BICubeRelation convert(BITableSourceRelation sourceRelation) {
        ICubeFieldSource primaryField = sourceRelation.getPrimaryField();
        ICubeFieldSource foreignField = sourceRelation.getForeignField();
        return new BICubeRelation(
                BIColumnKey.covertColumnKey(primaryField),
                BIColumnKey.covertColumnKey(foreignField),
                new BITableKey(sourceRelation.getPrimaryTable()),
                new BITableKey(sourceRelation.getForeignTable())
        );
    }

    public static boolean isRelationExisted(BITableSourceRelation relation, ICubeConfiguration cubeConfiguration) {
        BICube cube = new BICube(new BICubeResourceRetrieval(cubeConfiguration), BIFactoryHelper.getObject(ICubeResourceDiscovery.class));
        BICubeRelation cubeRelation = convert(relation);
        ITableKey tableKey = cubeRelation.getPrimaryTable();
        return cube.exist(tableKey, cubeRelation);
    }
}
