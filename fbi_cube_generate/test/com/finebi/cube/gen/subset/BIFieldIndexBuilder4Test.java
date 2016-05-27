package com.finebi.cube.gen.subset;

import com.finebi.cube.gen.oper.BIFieldIndexGenerator;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.ICube;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.stable.data.db.CubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldIndexBuilder4Test extends BIFieldIndexGenerator {
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
    public BIFieldIndexBuilder4Test(ICube cube, CubeTableSource tableSource, CubeFieldSource hostBICubeFieldSource, BIColumnKey targetColumnKey) {
        super(cube, tableSource, hostBICubeFieldSource, targetColumnKey);
    }
}
