package com.fr.bi.stable.relation.utils;


import com.fr.bi.stable.data.BITableID;
import com.fr.bi.stable.data.Table;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.general.ComparatorUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Created by GUY on 2015/3/24.
 */
public class BIRelationUtils {

    /**
     * 表key是否在关联中
     *
     * @param relationList
     * @param key
     * @return
     */
    public static boolean isRelationRepeated(List<BITableSourceRelation> relationList, ICubeTableSource key) {
        if (relationList == null || key == null) {
            return false;
        }
        Iterator<BITableSourceRelation> it = relationList.iterator();
        while (it.hasNext()) {
            BITableSourceRelation r = it.next();
            if (ComparatorUtils.equals(r.getPrimaryTable(), key)
                    || ComparatorUtils.equals(r.getPrimaryTable(), key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimaryRelationKey(List<BITableSourceRelation> relationList, BITableID key) {
        if (relationList == null || key == null || relationList.isEmpty()) {
            return false;
        }
        BITableSourceRelation[] relations = relationList.toArray(new BITableSourceRelation[relationList.size()]);
        return ComparatorUtils.equals(relations[0].getPrimaryKey(), key);
    }

    public static boolean isForeignRelationKey(List<BITableSourceRelation> relationList, Table key) {
        if (relationList == null || key == null || relationList.isEmpty()) {
            return false;
        }
        BITableSourceRelation[] relations = relationList.toArray(new BITableSourceRelation[relationList.size()]);
        return ComparatorUtils.equals(relations[relations.length - 1].getForeignKey(), key);
    }
}