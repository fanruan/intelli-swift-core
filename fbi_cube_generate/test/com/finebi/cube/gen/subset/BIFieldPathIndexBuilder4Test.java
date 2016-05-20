package com.finebi.cube.gen.subset;

import com.finebi.cube.gen.oper.BIFieldPathIndexBuilder;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BICubeTablePath;
import com.finebi.cube.structure.ICube;
import com.fr.bi.stable.data.db.BICubeFieldSource;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BIFieldPathIndexBuilder4Test extends BIFieldPathIndexBuilder {
    public BIFieldPathIndexBuilder4Test(ICube cube, BICubeFieldSource field, BICubeTablePath relationPath) {
        super(cube, field, relationPath);
    }

    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        System.out.println("Path Path Index!");
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }    @Override
    public void release() {

    }
}
