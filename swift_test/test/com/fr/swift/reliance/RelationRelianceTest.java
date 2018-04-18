package com.fr.swift.reliance;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.util.RelationNodeUtils;
import junit.framework.TestCase;

import java.util.List;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationRelianceTest extends TestCase {
    private List<DataSource> dataSources;
    private List<RelationSource> relationSources;

    @Override
    protected void setUp() throws Exception {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = RelationSourceGen.genRelationSource(dataSources, true);
    }

    /**
     * A-B, B-C, A-C, B-D, undefined-B    5
     * result A-B, B-C, A-C, B-D     4
     */
    public void testReliance() {
        RelationReliance reliance = new RelationReliance(relationSources, dataSources);
        RelationNodeUtils.calculateRelationNode(reliance);
        assertEquals(5, relationSources.size());
        assertEquals(4, reliance.getHeadNode().size());
    }
}
