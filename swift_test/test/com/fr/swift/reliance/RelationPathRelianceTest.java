package com.fr.swift.reliance;

import com.fr.swift.source.DataSource;
import com.fr.swift.source.RelationSource;
import com.fr.swift.source.SourcePath;
import com.fr.swift.util.RelationNodeUtils;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * @author yee
 * @date 2018/4/18
 */
public class RelationPathRelianceTest extends TestCase {
    private List<DataSource> dataSources;
    private List<RelationSource> relationSources;
    private List<SourcePath> sourcePaths;

    /**
     * DataSource A - G
     * A-B, B-C, A-C, B-D, B-undefined    5个关联
     * A-B, B-C, A-C, B-D, B-undefined    5个单路径
     * A-B-C, A-B-D, A-B-undefined 3个多表路径
     * 除去DataSource中不包含的表和单路径，还有2个Path
     */
    public void testReliance() {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = RelationSourceGen.genRelationSource(dataSources, true);
        sourcePaths = RelationSourceGen.calPath(relationSources);
        RelationReliance relationReliance = new RelationReliance(relationSources, dataSources);
        RelationNodeUtils.calculateRelationNode(relationReliance);
        RelationPathReliance reliance = new RelationPathReliance(sourcePaths, relationReliance);
        RelationNodeUtils.calculateRelationPathNode(reliance);
        assertEquals(8, sourcePaths.size());
        assertEquals(2, reliance.getHeadNode().size());
    }
    /**
     * DataSource A - G
     * A-B, B-C, C-D, A-C, B-D    5个关联
     * A-B, B-C, C-D, A-C, B-D   5个单路径
     * A-B-C, A-B-D, A-B-C-D, A-C-D, B-C-D 5个多表路径
     * 除去DataSource中不包含的表和单路径，还有5个Path
     */
    public void testMoreThan3Table() {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = RelationSourceGen.genRelationSource(dataSources, false);
        sourcePaths = RelationSourceGen.calPath(relationSources);
        RelationReliance relationReliance = new RelationReliance(relationSources, dataSources);
        RelationNodeUtils.calculateRelationNode(relationReliance);
        RelationPathReliance reliance = new RelationPathReliance(sourcePaths, relationReliance);
        RelationNodeUtils.calculateRelationPathNode(reliance);
        assertEquals(10, sourcePaths.size());
        assertEquals(5, reliance.getHeadNode().size());
    }

    /**
     * DataSource A - G
     * A-B-C-D 一条路径
     * A-B-C, A-B-C-D 变成2条路径
     * A-B-C依赖关联A-B, B-C
     * A-B-C-D依赖关联A-B-C, C-D
     * 除去DataSource中不包含的表和单路径，还有2个Path
     */
    public void testSinglePath() {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = RelationSourceGen.relationSourceForSinglePath(dataSources);
        sourcePaths = Collections.singletonList(RelationSourceGen.genSinglePath(relationSources));
        RelationReliance relationReliance = new RelationReliance(relationSources, dataSources);
        RelationNodeUtils.calculateRelationNode(relationReliance);
        RelationPathReliance reliance = new RelationPathReliance(sourcePaths, relationReliance);
        RelationNodeUtils.calculateRelationPathNode(reliance);
        assertEquals(1, sourcePaths.size());
        assertEquals(2, reliance.getHeadNode().size());
    }
}
