package com.fr.swift.reliance;

import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationPathNode implements IRelationNode<SourcePath, RelationSource> {

    private SourcePath node;
    private List<RelationSource> depend;

    public RelationPathNode(SourcePath node, List<RelationSource> depend) {
        this.node = node;
        this.depend = depend;
    }

    @Override
    public List<RelationSource> getDepend() {
        return depend;
    }

    @Override
    public SourcePath getNode() {
        return node;
    }

    @Override
    public SourceKey getKey() {
        return node.getSourceKey();
    }
}
