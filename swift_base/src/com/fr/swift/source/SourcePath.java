package com.fr.swift.source;

import com.fr.swift.source.core.CoreService;

import java.util.List;

/**
 * This class created on 2017-11-29.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public interface SourcePath extends RelationSource, Source, CoreService {

    SourcePath addRelationAtHead(RelationSource relationSource);

    SourcePath addRelationAtTail(RelationSource relationSource);

    SourcePath removeFirstRelation();

    SourcePath removeLastRelation();

    List<RelationSource> getRelations();

    SourcePath clone();
}
