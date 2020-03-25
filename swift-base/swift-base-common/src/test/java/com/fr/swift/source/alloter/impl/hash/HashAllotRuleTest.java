package com.fr.swift.source.alloter.impl.hash;

import com.fr.swift.source.alloter.impl.BaseAllotRule;
import com.fr.swift.source.alloter.impl.hash.function.HashFunction;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/**
 * @author lucifer
 * @date 2019-06-27
 * @description
 * @since advanced swift 1.0
 */
public class HashAllotRuleTest {

    @Test
    public void testHashAllotRule() {
        HashAllotRule hashAllotRule = new HashAllotRule();
        Assert.assertEquals(hashAllotRule.getFieldIndexes(), 0);
        Assert.assertEquals(hashAllotRule.getType(), BaseAllotRule.AllotType.HASH);
        Assert.assertTrue(hashAllotRule.getHashFunction() instanceof HashFunction);
        Assert.assertEquals(hashAllotRule.getCapacity(), 10000000);

        hashAllotRule = new HashAllotRule(Collections.singletonList(0), 16, 1000);
        Assert.assertEquals(hashAllotRule.getCapacity(), 1000);

    }
}
