package com.finebi.cube.tools;

import com.finebi.cube.relation.BICubeGenerateRelation;
import com.finebi.cube.relation.BICubeGenerateRelationPath;
import com.finebi.cube.relation.BITableSourceRelationPath;
import com.fr.bi.stable.exception.BITablePathEmptyException;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by kary on 2016/6/16.
 * 生成BICubeGenerateRelation和BICubeGenerateRelationPath以供测试使用
 */
public class BIMemoryCubeGenerateFactory1 {
    public static Set<BICubeGenerateRelation> getCubeGenerateRelationABC() {
        Set<BICubeGenerateRelation> biTableRelation4CubeGenerates = new LinkedHashSet<BICubeGenerateRelation>();
        BICubeGenerateRelation biTableRelation4CubeGenerateAB = new BICubeGenerateRelation(BITableSourceRelationTestTool.getMemoryAB(), BIMemoryDataSourceFactory.getDataSourceSetWithAB());
        BICubeGenerateRelation biTableRelation4CubeGenerateBC = new BICubeGenerateRelation(BITableSourceRelationTestTool.getMemoryBC(), BIMemoryDataSourceFactory.getDataSourceSetWithBC());
        biTableRelation4CubeGenerates.add(biTableRelation4CubeGenerateAB);
        biTableRelation4CubeGenerates.add(biTableRelation4CubeGenerateBC);
        return biTableRelation4CubeGenerates;
    }

    public static Set<BICubeGenerateRelationPath> getCubeGenerateRelationPathABC() {
        BITableSourceRelationPath abcPath = BITableSourceRelationPathTestTool.getABCPath();
        Set<BICubeGenerateRelationPath> biTableRelationPath4CubeGenerateSet = new HashSet<BICubeGenerateRelationPath>();
        Set<BITableSourceRelationPath> dependPath = new HashSet<BITableSourceRelationPath>();
        try {
            BITableSourceRelationPath dependPath1 = new BITableSourceRelationPath(abcPath.getLastRelation());
            dependPath.add(dependPath1);
            BITableSourceRelationPath dependPath2 = new BITableSourceRelationPath();
            dependPath2.copyFrom(abcPath);
            dependPath2.removeLastRelation();
            dependPath.add(dependPath2);
        } catch (BITablePathEmptyException e) {
            e.printStackTrace();
        }
        BICubeGenerateRelationPath biTableRelationPath4CubeGenerate = new BICubeGenerateRelationPath(abcPath, dependPath);
        biTableRelationPath4CubeGenerateSet.add(biTableRelationPath4CubeGenerate);
        return biTableRelationPath4CubeGenerateSet;
    }
}
