package com.finebi.cube.tools;

import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.utils.BITableKeyUtils;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.data.db.DBField;
import com.fr.bi.stable.data.source.ITableSource;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationTestTool {
    public static BICubeRelation getTaTb() {
        ITableSource tableA;
        ITableSource tableB;
        tableA = BIMemoryDataSourceFactory.generateTableA();
        tableB = BIMemoryDataSourceFactory.generateTableB();
        return new BICubeRelation(
                BIColumnKey.covertColumnKey(new DBField("tableA", "name", DBConstant.CLASS.STRING, 6)),
                BIColumnKey.covertColumnKey(new DBField("tableB", "lover", DBConstant.CLASS.STRING, 6)),
                BITableKeyUtils.convert(tableA),
                BITableKeyUtils.convert(tableB));
    }

    public static BICubeRelation getTbTc() {
        ITableSource tableC;
        tableC = BIMemoryDataSourceFactory.generateTableC();
        ITableSource tableB;
        tableB = BIMemoryDataSourceFactory.generateTableB();

        return new BICubeRelation(
                BIColumnKey.covertColumnKey(new DBField("tableB", "name", DBConstant.CLASS.STRING, 6)),
                BIColumnKey.covertColumnKey(new DBField("tableC", "lover", DBConstant.CLASS.STRING, 6)),
                BITableKeyUtils.convert(tableB),
                BITableKeyUtils.convert(tableC));
    }


}
