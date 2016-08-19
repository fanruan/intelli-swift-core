package com.finebi.cube.data.disk.reader.primitive;

import junit.framework.TestCase;

/**
 * This class created on 2016/8/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */
public class ArrayInitTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/8/17
     */
    public void testBasic() {
        try {
            ArrayInit4Test test = new ArrayInit4Test();
            System.out.println(test.getValue(3));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/8/17
     */
    public void testTempPara() {
        try {
            ArrayInit4Test test = new ArrayInit4Test();
            test.getTempParaValue(3);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
