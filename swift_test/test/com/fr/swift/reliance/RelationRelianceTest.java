package com.fr.swift.reliance;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.relation.RelationSourceImpl;
import com.fr.swift.util.RelationNodeUtils;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationRelianceTest extends TestCase {
    private List<DataSource> dataSources;
    private List<RelationSource> relationSources;

    /**
     * A-B, B-C, A-C, B-D, B-undefined    5
     * result A-B, B-C, A-C, B-D     4
     * B-undefined子表是未知表所以不更新
     */
    public void testReliance() {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = RelationSourceGen.genRelationSource(dataSources, true);
        RelationReliance reliance = new RelationReliance(relationSources, dataSources);
        RelationNodeUtils.calculateRelationNode(reliance);
        assertEquals(5, relationSources.size());
        assertEquals(4, reliance.getHeadNode().size());
    }

    /**
     * A-B, B-C, A-C, B-D, undefined-B    5
     * result A-B, B-C, A-C, B-D, undefined-B   5
     * undefined-B子表是已知表所以尝试更新
     */
    public void testRelianceChild() {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = new ArrayList<>(RelationSourceGen.genRelationSource(dataSources, true));
        relationSources.set(relationSources.size() - 1, new RelationSourceImpl(
                new SourceKey("undefined"), dataSources.get(1).getSourceKey(), Arrays.asList("undefined"), Arrays.asList("BA")
        ));
        RelationReliance reliance = new RelationReliance(relationSources, dataSources);
        RelationNodeUtils.calculateRelationNode(reliance);
        assertEquals(5, relationSources.size());
        assertEquals(5, reliance.getHeadNode().size());
    }
}
