package com.fr.bi.cal.stable.index.utils;


import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.relation.BITableSourceRelation;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.engine.index.key.IndexKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by GUY on 2015/3/24.
 */
public class BIVersionUtils {

    /**
     * 创建关联版本值
     *
     * @param loader    CubeTILoader对象
     * @param relations 关联数组
     * @return int值
     */
    public static int createRelationVersionValue(ICubeDataLoader loader, List<? extends BITableSourceRelation> relations) {
        List<Integer> res = new ArrayList<Integer>();
        CubeFieldSource start = getRelationPrimField(relations);
        if (start == null) {
            return 0;
        }
        res.add(loader.getTableIndex(start.getTableBelongTo()).getTableVersion(new IndexKey(start.getFieldName())));

        for (BITableSourceRelation relation : relations) {
            CubeFieldSource t = relation.getForeignKey();
            res.add(loader.getTableIndex(t.getTableBelongTo()).getTableVersion(new IndexKey(t.getFieldName())));
        }
        return Arrays.hashCode(res.toArray());
    }

    public static CubeFieldSource getRelationPrimField(List<? extends BITableSourceRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return null;
        }
        return relations.get(0).getPrimaryKey();
    }
}