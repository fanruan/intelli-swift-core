package com.fr.swift.creater;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;
import com.fr.swift.source.core.Core;

/**
 * This class created on 2017-11-30.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TestPathSource implements SourcePath {

    private String pathName;

    public TestPathSource(String pathName) {
        this.pathName = pathName;
    }

    @Override
    public Core fetchObjectCore() {
        return null;
    }

    @Override
    public SourceKey getSourceKey() {
        return new SourceKey(pathName);
    }

    @Override
    public void addRelationAtHead() {

    }

    @Override
    public void addRelationAtTail() {

    }

    @Override
    public void removeFirstRelation() {

    }

    @Override
    public void removeLastRelation() {

    }
}
