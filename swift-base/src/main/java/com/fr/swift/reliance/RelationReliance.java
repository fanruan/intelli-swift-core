package com.fr.swift.reliance;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.RelationSourceType;
import com.fr.swift.source.SourceKey;

import java.util.HashMap;
import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationReliance extends AbstractRelationReliance<RelationNode, RelationSource> {

    public RelationReliance(List<RelationSource> allRelationSource, SourceReliance sourceReliance) {
        super(allRelationSource, sourceReliance);
    }

    public RelationReliance(List<RelationSource> allRelationSource, List<DataSource> sourceReliance) {
        super(allRelationSource, sourceReliance);
    }

    @Override
    protected void handleSingleRelationSource(List<RelationSource> allRelationSource) {
        {
            this.allRelationSource = new HashMap<SourceKey, RelationSource>();
            for (RelationSource source : allRelationSource) {
                if (source.getRelationType() == RelationSourceType.RELATION && !this.allRelationSource.containsKey(source.getSourceKey())) {
                    this.allRelationSource.put(source.getSourceKey(), source);
                }
            }
        }
    }
}
