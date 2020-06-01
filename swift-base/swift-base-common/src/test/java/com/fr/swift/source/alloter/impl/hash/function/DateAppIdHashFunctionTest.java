package com.fr.swift.source.alloter.impl.hash.function;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author lucifer
 * @date 2020/2/11
 * @description
 * @since ness engine demoF
 */
public class DateAppIdHashFunctionTest {

    @Test
    public void indexOf() {
        HashFunction yearMonthFunction = new DateAppIdHashFunction(0);
        Assert.assertEquals(yearMonthFunction.indexOf("201909"), 201909);
    }

    @Test
    public void getType() {
        Assert.assertEquals(new DateAppIdHashFunction(0).getType(), HashType.APPID_YEARMONTH);
    }
}