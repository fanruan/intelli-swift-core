package com.finebi.cube.gen.subset;

import com.finebi.cube.gen.oper.BIRelationIndexGenerator;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.ICube;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BIRelationIndexBuilder4Test extends BIRelationIndexGenerator {
    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        System.out.println("Relation Index!");
        BICubeBuildProbeTool.INSTANCE.getFlag().put("RelationIndex", 20);
        BICubeBuildProbeTool.INSTANCE.getFlag().put("RelationIndex:"+lastReceiveMessage.getFragment(), 20);

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
    public BIRelationIndexBuilder4Test(ICube cube, BICubeRelation relation) {
        super(cube, relation);
    }
}
