package com.fr.bi.cal.stable.index.utils;


import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.engine.index.key.IndexKey;
import com.fr.bi.stable.relation.BIBasicRelation;

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
    public static int createRelationVersionValue(ICubeDataLoader loader, List<? extends BIBasicRelation> relations) {
        List<Integer> res = new ArrayList<Integer>();
        BIField start = getRelationPrimField(relations);
        if (start == null) {
            return 0;
        }
        res.add(loader.getTableIndex(start).getTableVersion(new IndexKey(start.getFieldName())));

        for (BIBasicRelation relation : relations) {
            BIField t = (BIField) relation.getForeignKey();
            res.add(loader.getTableIndex(t).getTableVersion(new IndexKey(t.getFieldName())));
        }
        return Arrays.hashCode(res.toArray());
    }

    public static BIField getRelationPrimField(List<? extends BIBasicRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return null;
        }
        return (BIField) relations.get(0).getPrimaryKey();
    }
}