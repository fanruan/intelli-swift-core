package com.finebi.cube.disk.reader.primitive;

import com.finebi.cube.tools.ArrayInitTestTool;
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
            ArrayInitTestTool test = new ArrayInitTestTool();
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
            ArrayInitTestTool test = new ArrayInitTestTool();
            test.getTempParaValue(3);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
