package com.fr.bi.cube.engine.result;

import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-5.
 */
public class CrossNodeTest extends TestCase {


    public void testAddTopChild() {
        CrossNode root = newCrossNode();
        CrossNode child = newCrossNode();
        root.addTopChild(child);
        assertSame(1, root.getTopChildLength());
    }

    public void testSddLeftChild() {
        CrossNode root = newCrossNode();
        CrossNode child = newCrossNode();
        root.addLeftChild(child);
        assertSame(1, root.getLeftChildLength());
    }

    public void testCloneWithLeftChildNode() {
        CrossNode root = newCrossNode();
        CrossNode child_l = newCrossNode();
        CrossNode child_t = newCrossNode();
        root.addTopChild(child_l);
        root.addLeftChild(child_t);
        assertSame(1, root.getLeftChildLength());
        assertSame(1, root.getTopChildLength());
        CrossNode clone = root.cloneWithLeftChildNode();
        assertSame(1, clone.getLeftChildLength());
        assertSame(0, clone.getTopChildLength());
    }

    public void testCloneWithTopChildNode() {
        CrossNode root = newCrossNode();
        CrossNode child_l = newCrossNode();
        CrossNode child_t = newCrossNode();
        root.addTopChild(child_l);
        root.addLeftChild(child_t);
        assertSame(1, root.getLeftChildLength());
        assertSame(1, root.getTopChildLength());
        CrossNode clone = root.cloneWithTopChildNode();
        assertSame(0, clone.getLeftChildLength());
        assertSame(1, clone.getTopChildLength());
    }

    public void testMergeValue() {
//        CrossNode root = newCrossNode();
//        CrossNode child_l = newCrossNode();
//        CrossNode child_t = newCrossNode();
//        root.addTopChild(child_l);
//        root.addLeftChild(child_t);
//        assertSame(1,root.getLeftChildLength());
//        assertSame(1,root.getTopChildLength());
//        CrossNode clone = root.cloneWithTopChildNode();
//        TargetGettingKey key1 = new TargetGettingKey(new SumKey(new ColumnKey("good"),null),"abc");
//        root.mergeValue(root,clone,key1);
    }


    private CrossNode newCrossNode() {
        return new CrossNode(new CrossHeader(new ColumnKey("header"), null, null), new CrossHeader(new ColumnKey("left"), null, null));
    }
}