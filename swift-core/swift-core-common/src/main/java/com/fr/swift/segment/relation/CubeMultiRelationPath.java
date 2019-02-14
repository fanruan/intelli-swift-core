package com.fr.swift.segment.relation;

import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/9/7
 */
public interface CubeMultiRelationPath {
    CubeMultiRelationPath addRelationAtTail(CubeMultiRelation relation);

    CubeMultiRelationPath addRelationAtHead(CubeMultiRelation relation);

    void removeLastRelation();

    void removeFirstRelation();

    boolean canRelationsBuildPath(CubeMultiRelation part_head, CubeMultiRelation part_tail);

    Boolean isEmptyPath();

    CubeMultiRelation getLastRelation();

    CubeMultiRelation getFirstRelation();

    List<CubeMultiRelation> getAllRelations();

    SourceKey getStartTable();

    String getKey();

    void add(CubeMultiRelation convert2CubeRelation);

    int size();

    SourceKey getEndTable();

    void copyFrom(CubeMultiRelationPath relationPath);
}
