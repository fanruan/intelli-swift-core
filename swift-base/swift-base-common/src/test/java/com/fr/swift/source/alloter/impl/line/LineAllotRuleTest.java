package com.fr.swift.source.alloter.impl.line;

import com.fr.swift.source.alloter.impl.BaseAllotRule;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
public class LineAllotRuleTest {

    @Test
    public void testLineAllotRule() throws Exception {
        LineAllotRule lineAllotRule = new LineAllotRule(100);
        Assert.assertEquals(lineAllotRule.getType(), BaseAllotRule.AllotType.LINE);
        Assert.assertEquals(lineAllotRule.getCapacity(), 100);

        lineAllotRule = new LineAllotRule();
        Assert.assertEquals(lineAllotRule.getCapacity(), 10000000);
    }
}
