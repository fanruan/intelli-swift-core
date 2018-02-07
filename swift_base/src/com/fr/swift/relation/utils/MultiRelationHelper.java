package com.fr.swift.relation.utils;

import com.fr.swift.relation.CubeLogicColumnKey;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.IRelationSource;
import com.fr.swift.source.SourceKey;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/29
 */
public class MultiRelationHelper {
    public static CubeMultiRelation convert2CubeRelation(IRelationSource source) {
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
        SourceKey primarySource = source.getPrimarySource();
        SourceKey foreignSource = source.getPrimarySource();
        return new CubeMultiRelation(new CubeLogicColumnKey(primarySource, primaryKeys),
                new CubeLogicColumnKey(foreignSource, foreignKeys), primarySource, foreignSource);
    }
}
