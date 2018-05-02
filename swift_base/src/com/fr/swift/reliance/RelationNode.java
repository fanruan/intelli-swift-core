package com.fr.swift.reliance;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.Source;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationNode implements IRelationNode<RelationSource, Source> {
    private RelationSource node;
    private List<Source> depend;

    public RelationNode(RelationSource node, List<Source> depend) {
        this.node = node;
        this.depend = depend;
    }

    @Override
    public List<Source> getDepend() {
        return depend;
    }

    @Override
    public RelationSource getNode() {
        return node;
    }

    @Override
    public SourceKey getKey() {
        return node.getSourceKey();
    }
}
