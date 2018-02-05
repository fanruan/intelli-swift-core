package com.fr.swift.cube.gen.oper;

import com.fr.base.FRContext;
import com.fr.dav.LocalEnv;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.relation.BICubeLogicColumnKey;
import com.fr.swift.relation.BICubeMultiRelation;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SourceKey;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018/2/5
 */
public class MultiRelationIndexBuilderTest extends TestCase {

    public void setUp() {
        FRContext.setCurrentEnv(new LocalEnv("/Users/yee/work/bi/bi/env/WebReport/WEB-INF/"));
    }

    public void testWork() {
        System.setProperty("user.dir", "/Users/yee/tomcat8/bin");
        SourceKey primaryTable = new SourceKey("07cf9a16");
        SourceKey foreignTable = new SourceKey("c45134cb");
        List<ColumnKey> primaryFields = new ArrayList<ColumnKey>();
        List<ColumnKey> foreignFields = new ArrayList<ColumnKey>();
        primaryFields.add(new ColumnKey("客户ID"));
        foreignFields.add(new ColumnKey("客户ID"));
        BICubeMultiRelation relation = new BICubeMultiRelation(new BICubeLogicColumnKey(primaryFields), new BICubeLogicColumnKey(foreignFields), primaryTable, foreignTable);
        new MultiRelationIndexBuilder(relation, LocalSegmentProvider.getInstance()).work();
    }
}