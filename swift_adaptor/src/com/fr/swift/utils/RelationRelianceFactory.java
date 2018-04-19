package com.fr.swift.utils;

import com.fr.swift.reliance.RelationPathReliance;
import com.fr.swift.reliance.RelationReliance;
import com.fr.swift.reliance.SourceReliance;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.util.RelationNodeUtils;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationRelianceFactory {
    public static RelationReliance generateRelationReliance(List<RelationSource> allRelationSource, SourceReliance sourceReliance) {
        RelationReliance relationReliance = new RelationReliance(allRelationSource, sourceReliance);
        RelationNodeUtils.calculateRelationNode(relationReliance);
        return relationReliance;
    }

    public static RelationPathReliance generateRelationPathReliance(List<SourcePath> allRelationPathSource, RelationReliance sourceReliance) {
        RelationPathReliance relationPathReliance = new RelationPathReliance(allRelationPathSource, sourceReliance);
        RelationNodeUtils.calculateRelationPathNode(relationPathReliance);
        return relationPathReliance;
    }

    public static RelationReliance generateRelationReliance(List<RelationSource> allRelationSource, List<DataSource> sourceReliance) {
        RelationReliance relationReliance = new RelationReliance(allRelationSource, sourceReliance);
        RelationNodeUtils.calculateRelationNode(relationReliance);
        return relationReliance;
    }

//    public static RelationPathReliance generateRelationPathReliance(List<SourcePath> allRelationPathSource, List<DataSource>  sourceReliance) {
//        RelationPathReliance relationPathReliance = new RelationPathReliance(allRelationPathSource, sourceReliance);
//        RelationNodeUtils.calculateRelationPathNode(relationPathReliance);
//        return relationPathReliance;
//    }
}
