package com.fr.swift.relation.utils;

import com.fr.swift.relation.BICubeLogicColumnKey;
import com.fr.swift.relation.BICubeMultiRelation;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.IRelationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/29
 */
public class BIMultiRelationHelper {
    public static BICubeMultiRelation convert2CubeRelation(IRelationSource source) {
        List<ColumnKey> primaryKeys = new ArrayList<ColumnKey>();
        List<ColumnKey> foreignKeys = new ArrayList<ColumnKey>();
        List<String> primaryFields = source.getPrimaryFields();
        List<String> foreignFields = source.getForeignFields();
        for (int i = 0, len = primaryFields.size(); i < len; i++) {
            ColumnKey key = new ColumnKey(primaryFields.get(i));
            key.setRelation(source);
            primaryKeys.add(key);
        }
        for (int i = 0, len = foreignFields.size(); i < len; i++) {
            ColumnKey key = new ColumnKey(foreignFields.get(i));
            key.setRelation(source);
            foreignKeys.add(key);
        }
        return new BICubeMultiRelation(new BICubeLogicColumnKey(primaryKeys), new BICubeLogicColumnKey(foreignKeys), source.getPrimarySource(), source.getForeignSource());
    }
}
