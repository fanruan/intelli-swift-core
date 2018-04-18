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
     * A-B, B-C, A-C, B-D, undefined-B    5个关联
     * A-B, B-C, A-C, B-D, undefined-B    5个单路径
     * A-B-C, A-B-D, undefined-B-C, undefined-B-D 4个多表路径
     * 除去DataSource中不包含的表和单路径，还有2个Path
     * result 2
     */
    public void testReliance() {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = RelationSourceGen.genRelationSource(dataSources, true);
        sourcePaths = RelationSourceGen.calPath(relationSources);
        RelationPathReliance reliance = new RelationPathReliance(sourcePaths, dataSources);
        RelationNodeUtils.calculateRelationPathNode(reliance);
        assertEquals(9, sourcePaths.size());
        assertEquals(2, reliance.getHeadNode().size());
    }
    /**
     * DataSource A - G
     * A-B, B-C, A-C, B-D    5个关联
     * A-B, B-C, C-D, A-C, B-D   5个单路径
     * A-B-C, A-B-D, A-B-C-D, A-C-D 4个多表路径
     * 除去DataSource中不包含的表和单路径，还有4个Path
     * result 4
     */
    public void testMoreThan3Table() {
        dataSources = RelationSourceGen.genDataSource();
        relationSources = RelationSourceGen.genRelationSource(dataSources, false);
        sourcePaths = RelationSourceGen.calPath(relationSources);
        RelationPathReliance reliance = new RelationPathReliance(sourcePaths, dataSources);
        RelationNodeUtils.calculateRelationPathNode(reliance);
        assertEquals(12, sourcePaths.size());
        assertEquals(4, reliance.getHeadNode().size());
    }
}
