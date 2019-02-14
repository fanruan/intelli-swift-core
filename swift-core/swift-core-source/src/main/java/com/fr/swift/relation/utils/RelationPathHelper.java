package com.fr.swift.relation.utils;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.CubeMultiRelation;
import com.fr.swift.segment.relation.CubeMultiRelationPath;
import com.fr.swift.segment.relation.impl.CubeLogicColumnKey;
import com.fr.swift.segment.relation.impl.CubeMultiRelationImpl;
import com.fr.swift.segment.relation.impl.CubeMultiRelationPathImpl;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.relation.RelationPathSourceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/1/29
 */
public class RelationPathHelper {
    /**
     * RelationSource转换成CubeMultiRelation
     *
     * @param source 要转换的RelationSource 一般是RelationSourceImpl
     * @return 关联
     */
    public static CubeMultiRelation convert2CubeRelation(RelationSource source) {
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
        SourceKey foreignSource = source.getForeignSource();
        return new CubeMultiRelationImpl(new CubeLogicColumnKey(primarySource, primaryKeys),
                new CubeLogicColumnKey(foreignSource, foreignKeys), primarySource, foreignSource);
    }

    /**
     * RelationSource转换成CubeMultiRelationPath
     *
     * @param source RelationPathSourceImpl
     * @return 路径
     */
    public static CubeMultiRelationPath convert2CubeRelationPath(RelationSource source) {
        CubeMultiRelationPath path = new CubeMultiRelationPathImpl();
        if (source.getRelationType() == RelationSourceType.RELATION) {
            path.add(convert2CubeRelation(source));
        } else {
            RelationPathSourceImpl pathSource = (RelationPathSourceImpl) source;
            List<RelationSource> relationSources = pathSource.getRelations();
            for (RelationSource relationSource : relationSources) {
                path.add(convert2CubeRelation(relationSource));
            }
        }
        return path;
    }
}
