package com.fr.swift.cloud.source.alloter.impl.hash.function;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class DesignerHashFunctionTest {

    @Test
    public void indexOf() {
        HashFunction designerHashFunction = new DesignerHashFunction(0);
        Assert.assertEquals(designerHashFunction.indexOf("202105"), 202105);
        Assert.assertEquals(designerHashFunction.indexOf("20210510"), 202105);
        Assert.assertEquals(designerHashFunction.indexOf("0306859f-40cc-4f8b-b7ed-75658144fdb1"), 0);

        List<Object> keys = Stream.of("20210510", "0306859f-40cc-4f8b-b7ed-75658144fdb1").collect(Collectors.toList());
        Assert.assertEquals(designerHashFunction.indexOf(keys), 202105000);

        List<Object> keys2 = Stream.of("20210510", "0306859f-40cc-4f8b-b7ed-75658144fdb1", "202005").collect(Collectors.toList());
        Assert.assertEquals(designerHashFunction.indexOf(keys2), 202105000);
    }

    @Test
    public void divideOf() {
        Assert.assertEquals(new DesignerHashFunction().divideOf(202105000), Stream.of(202105, 0).collect(Collectors.toList()));
    }
}