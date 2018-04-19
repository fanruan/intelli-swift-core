package com.fr.swift.reliance;

import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SourcePath;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationPathReliance extends AbstractRelationReliance<RelationPathNode, SourcePath> {

    private Map<SourceKey, RelationNode> relationNodeMap;

    public RelationPathReliance(List<SourcePath> allRelationSource, RelationReliance relationReliance) {
        super(allRelationSource);
        this.relationNodeMap = relationReliance.getHeadNode();
        this.allDataSourceList = relationReliance.getAllDataSourceList();
    }

    public Map<SourceKey, RelationNode> getRelationNodeMap() {
        return relationNodeMap;
    }

    @Override
    protected void handleSingleRelationSource(List<SourcePath> allRelationSource) {
        this.allRelationSource = new HashMap<SourceKey, SourcePath>();
        for (SourcePath source : allRelationSource) {
            if (source.getRelations().size() > 1 && !this.allRelationSource.containsKey(source.getSourceKey())) {
                this.allRelationSource.put(source.getSourceKey(), source);
            }
        }
    }
}
