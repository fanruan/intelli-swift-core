package com.fr.swift.reliance;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationPathNode implements IRelationNode<RelationSource, RelationSource> {

    private RelationSource node;
    private List<RelationSource> depend;

    public RelationPathNode(RelationSource node, List<RelationSource> depend) {
        this.node = node;
        this.depend = depend;
    }

    @Override
    public List<RelationSource> getDepend() {
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
