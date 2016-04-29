package com.fr.bi.cube.engine.result;

import com.fr.bi.cube.engine.index.IDGroupValueIndex;
import com.fr.bi.cube.engine.store.ColumnKey;
import junit.framework.TestCase;

/**
 * Created by Connery on 14-11-6.
 */
public class NewCrossRootTest extends TestCase {

    private NewCrossRoot root;
    private CrossHeader left;
    private CrossHeader top;


    public void testCreatePageNode() {
        NewCrossRoot newCrossRoot = root.createBeforeCountNode(1);
        assertTrue((newCrossRoot != null));
    }

    public void testCreateBeforeCountNode() {
        NewCrossRoot newCrossRoot = root.createBeforeCountNode(1);
        assertTrue((newCrossRoot != null));
    }

    public void testCreateShiftRootNode() {
        NewCrossRoot newCrossRoot = root.createShiftRootNode(newCrossRoot(), 1);
        assertTrue((newCrossRoot != null));
//        NewCrossRoot newCrossRoot1 =   root.createShiftRootNode(newCrossRoot(),-1);
//        assertTrue((newCrossRoot1!= null));
        NewCrossRoot newCrossRoot2 = root.createShiftRootNode(newCrossRoot(), 0);
        assertTrue((newCrossRoot2 != null));
    }

    public void testCreatePageNodeShift() {

        NewCrossRoot newCrossRoot = root.createPageNode(0, 1);
        assertTrue((newCrossRoot != null));
//        NewCrossRoot newCrossRoot1 =   root.createShiftRootNode(newCrossRoot(),-1);
//        assertTrue((newCrossRoot1!= null));
        NewCrossRoot newCrossRoot2 = root.createPageNode(1, 1);
        assertTrue((newCrossRoot2 != null));
    }


    public void setUp() {
        root = newCrossRoot();
    }

    private NewCrossRoot newCrossRoot() {
        left = newCrossHeader();
        top = newCrossHeader();
        return new NewCrossRoot(left, top);
    }

    private CrossHeader newCrossHeader() {
        IDGroupValueIndex index = new IDGroupValueIndex(new int[5], 10);
        return new CrossHeader(new ColumnKey("good"), new Object(), index);
    }

    private CrossNode newCrossNode() {
        return new CrossNode(new CrossHeader(new ColumnKey("header"), null, null), new CrossHeader(new ColumnKey("left"), null, null));
    }
}