package com.fr.bi.cal.analyze.cal.result;

import junit.framework.TestCase;

/**
 * Created by 小灰灰 on 2016/8/12.
 */
public class MergerNodeTest extends TestCase{
    public void testGetSumValue(){
        final MergerNode node = new MergerNode();
        final Object ob1 = new Object();
        final Object ob2 = new Object();
        node.setSummaryValue(ob1, 1);
        node.setSummaryValue(ob2, 2);
        assertEquals(node.getSummaryValue(ob1), 1.0);
        node.setSummaryValue(ob1, 3);
        assertEquals(node.getSummaryValue(ob1), 3.0);
        node.getSummaryValueMap().clear();
        assertEquals(node.getSummaryValue(ob1), null);
    }

}