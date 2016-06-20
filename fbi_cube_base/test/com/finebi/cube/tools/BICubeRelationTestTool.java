package com.finebi.cube.tools;

import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationTestTool {
    public static BICubeRelation getTaTb() {
        CubeTableSource tableA;
        CubeTableSource tableB;
        tableA = BIMemoryDataSourceFactory.generateTableA();
        tableB = BIMemoryDataSourceFactory.generateTableB();
        return new BICubeRelation(
                BIColumnKey.covertColumnKey(new BICubeFieldSource(tableA, "name", DBConstant.CLASS.STRING, 6)),
                BIColumnKey.covertColumnKey(new BICubeFieldSource(tableB, "lover", DBConstant.CLASS.STRING, 6)),
                BITableKeyUtils.convert(tableA),
                BITableKeyUtils.convert(tableB));
    }

    public static BICubeRelation getNullTableRelation() {
        CubeTableSource tableA;
        CubeTableSource tableB;
        tableA = BIMemoryDataSourceFactory.generateTableNullParent();
        tableB = BIMemoryDataSourceFactory.generateTableNullChild();
        return new BICubeRelation(
                BIColumnKey.covertColumnKey(new BICubeFieldSource(tableA, "id", DBConstant.CLASS.INTEGER, 6)),
                BIColumnKey.covertColumnKey(new BICubeFieldSource(tableB, "P_id", DBConstant.CLASS.INTEGER, 6)),
                BITableKeyUtils.convert(tableA),
                BITableKeyUtils.convert(tableB));
    }

    public static BICubeRelation getTbTc() {
        CubeTableSource tableC;
        tableC = BIMemoryDataSourceFactory.generateTableC();
        CubeTableSource tableB;
        tableB = BIMemoryDataSourceFactory.generateTableB();

        return new BICubeRelation(
                BIColumnKey.covertColumnKey(new BICubeFieldSource(tableB, "name", DBConstant.CLASS.STRING, 6)),
                BIColumnKey.covertColumnKey(new BICubeFieldSource(tableC, "lover", DBConstant.CLASS.STRING, 6)),
                BITableKeyUtils.convert(tableB),
                BITableKeyUtils.convert(tableC));
    }


}
