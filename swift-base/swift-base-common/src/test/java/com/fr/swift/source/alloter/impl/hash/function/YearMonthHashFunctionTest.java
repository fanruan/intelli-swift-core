package com.fr.swift.source.alloter.impl.hash.function;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author lucifer
 * @date 2020/2/11
 * @description
 * @since ness engine demoF
 */
public class YearMonthHashFunctionTest {

    @Test
    public void indexOf() {
        HashFunction yearMonthFunction = new YearMonthHashFunction();
        Assert.assertEquals(yearMonthFunction.indexOf("201909"), 201909);
    }

    @Test
    public void getType() {
        Assert.assertEquals(new YearMonthHashFunction().getType(), HashType.YEAR_MONTH);
    }
}