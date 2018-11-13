package com.fr.swift.creater;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.core.Core;

import java.util.List;

/**
 * This class created on 2017-11-30.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TestRelationSource implements RelationSource {

    private String relationName;


    public TestRelationSource(String relationName) {
        this.relationName = relationName;
    }

    @Override
    public Core fetchObjectCore() {
        return null;
    }

    @Override
    public SourceKey getSourceKey() {
        return new SourceKey(relationName);
    }

    @Override
    public SourceKey getPrimarySource() {
        return null;
    }

    @Override
    public SourceKey getForeignSource() {
        return null;
    }

    @Override
    public List<String> getPrimaryFields() {
        return null;
    }

    @Override
    public List<String> getForeignFields() {
        return null;
    }

    @Override
    public RelationSourceType getRelationType() {
        return null;
    }
}
