package com.fr.swift.source.alloter.impl.time;

import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.time.function.TimePartitionsFunction;
import com.fr.swift.source.alloter.impl.time.function.TimePartitionsType;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Marvin
 * @date 7/23/2019
 * @description
 * @since swift 1.1
 */
public class TimeAllotRuleTest {

    @Test
    public void testTimeAllotRule() {
        TimeAllotRule timeAllotRule = new TimeAllotRule();
        Assert.assertEquals(timeAllotRule.getFieldIndex(), 0);
        Assert.assertEquals(timeAllotRule.getType(), BaseAllotRule.AllotType.TIME);
        Assert.assertTrue(timeAllotRule.getHashFunction() instanceof TimePartitionsFunction);
        Assert.assertEquals(timeAllotRule.getCapacity(), 10000000);

        timeAllotRule = new TimeAllotRule(0, TimePartitionsType.YEAR, 1000);
        Assert.assertEquals(timeAllotRule.getCapacity(), 1000);

        TimePartitionsFunction function = new TimePartitionsFunction(TimePartitionsType.YEAR);
        timeAllotRule = new TimeAllotRule(0, function);
        Assert.assertEquals(timeAllotRule.getCapacity(), 10000000);
    }
}
