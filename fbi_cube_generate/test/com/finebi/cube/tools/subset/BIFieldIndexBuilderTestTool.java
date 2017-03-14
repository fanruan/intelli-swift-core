package com.finebi.cube.tools.subset;

import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.tools.BICubeBuildProbeTool;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Map;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldIndexBuilderTestTool extends BIFieldIndexGenerator {
    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        System.out.println("Field Index!");
        BICubeBuildProbeTool.INSTANCE.getFlag().put(hostBICubeFieldSource.getFieldName() + tableSource.getSourceID(), 11);
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    public void release() {

    }

    public BIFieldIndexBuilderTestTool(Cube cube, Cube integrityCube, CubeTableSource tableSource, ICubeFieldSource hostBICubeFieldSource, BIColumnKey targetColumnKey, Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        super(cube, integrityCube, tableSource, hostBICubeFieldSource, targetColumnKey, tablesNeed2GenerateMap);
    }
}
