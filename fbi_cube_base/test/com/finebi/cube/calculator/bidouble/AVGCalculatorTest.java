package com.finebi.cube.calculator.bidouble;

import com.fr.bi.stable.gvi.GroupValueIndex;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2016/6/24.
 *
 * @author Connery
 * @since 4.0
 */
public class AVGCalculatorTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/24
     */
    public void testAVG() {
        try {
            GroupValueIndex groupValueIndex = EasyMock.createMock(GroupValueIndex.class);
            EasyMock.expect(groupValueIndex.getRowsCountWithData()).andReturn(100);
            AVGCalculator avgCalculator = new AVGCalculator4Test(1000);
            EasyMock.replay(groupValueIndex);
            assertEquals(avgCalculator.calculate(null, null, groupValueIndex), Double.valueOf(10));
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
