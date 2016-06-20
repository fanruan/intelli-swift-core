package com.finebi.cube.gen.subset;

import com.finebi.cube.gen.oper.BITablePathIndexBuilder;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ICube;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BITablePathIndexBuilder4Test extends BITablePathIndexBuilder {
    public BITablePathIndexBuilder4Test(ICube cube, BICubeTablePath relationPath) {
        super(cube, relationPath);
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        System.out.println("Table Path Index!");
        BICubeBuildProbeTool.INSTANCE.getFlag().put("tablePath", 30);
        BICubeBuildProbeTool.INSTANCE.getFlag().put("tablePath:"+lastReceiveMessage.getFragment(),200);

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
