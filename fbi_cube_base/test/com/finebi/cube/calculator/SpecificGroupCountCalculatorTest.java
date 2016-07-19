package com.finebi.cube.calculator;

import com.finebi.cube.calculator.biint.SpecificGroupCountCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2016/6/24.
 *
 * @author Connery
 * @since 4.0
 */
public class SpecificGroupCountCalculatorTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/24
     */
    public void testRowCount() {
        try {
            GroupValueIndex groupValueIndex = EasyMock.createMock(GroupValueIndex.class);
            EasyMock.expect(groupValueIndex.getRowsCountWithData()).andReturn(100);
            EasyMock.replay(groupValueIndex);
            assertEquals(SpecificGroupCountCalculator.INSTANCE.calculate(null, null, groupValueIndex), 100);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
