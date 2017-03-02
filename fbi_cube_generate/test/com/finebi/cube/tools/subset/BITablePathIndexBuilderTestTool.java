package com.finebi.cube.tools.subset;

import com.finebi.cube.gen.oper.BITablePathIndexBuilder;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.tools.BICubeBuildProbeTool;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BITablePathIndexBuilderTestTool extends BITablePathIndexBuilder {
    public BITablePathIndexBuilderTestTool(Cube cube, BICubeTablePath relationPath) {
        super(cube, null, relationPath);
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        System.out.println("Table Path Index!");
        BICubeBuildProbeTool.INSTANCE.getFlag().put("tablePath", 30);
        BICubeBuildProbeTool.INSTANCE.getFlag().put("tablePath:" + lastReceiveMessage.getFragment(), 200);

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
}
