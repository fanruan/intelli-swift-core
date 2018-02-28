package com.fr.swift.source;

import com.fr.swift.source.core.CoreService;

/**
 * This class created on 2017-11-29.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public interface SourcePath extends Source, CoreService {

    void addRelationAtHead();

    void addRelationAtTail();

    void removeFirstRelation();

    void removeLastRelation();

}
