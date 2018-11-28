package com.fr.swift.compare;

import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * @author anchore
 * @date 2018/4/13
 */
public class ComparatorsTest {

    @Test
    public void testPinyinCompare() {
        Comparator<String> cmp = Comparators.STRING_ASC;
        Assert.assertEquals(0, cmp.compare(null, null));
        Assert.assertEquals(1, cmp.compare("开机动画风格dlfg", null));
        Assert.assertEquals(-1, cmp.compare(null, "抠脚大汉"));

        Assert.assertEquals(0, cmp.compare("$", "$"));
        Assert.assertEquals(-1, cmp.compare("a", "b"));
        Assert.assertEquals(1, cmp.compare("z", "c"));
        Assert.assertEquals(-1, cmp.compare("x", "X"));
        Assert.assertEquals(1, cmp.compare("b", "a啊士大夫"));

        Assert.assertEquals(0, cmp.compare("抠脚大汉m", "抠脚大汉m"));
        Assert.assertEquals(-1, cmp.compare("1抠脚大汉", "抠脚大汉"));
        Assert.assertEquals(1, cmp.compare("抠脚大汉1", "抠脚大汉"));
        Assert.assertEquals(-1, cmp.compare("抠脚1大汉", "抠脚大汉1"));

        Assert.assertEquals(0, cmp.compare("摳腳大漢max", "摳腳大漢max"));
        Assert.assertEquals(-1, cmp.compare("抠脚大汉1", "摳腳大漢1"));
        Assert.assertEquals(1, cmp.compare("摳腳大漢", "1抠脚大汉"));
        Assert.assertEquals(-1, cmp.compare("摳腳大漢", "體數據"));
        Assert.assertEquals(1, cmp.compare("鍋脚大汉", "摳腳大漢"));
    }

    @Test
    public void numberAsc() {
        double d1 = Double.MAX_VALUE, d2 = Double.MIN_VALUE;
        long l1 = Long.MAX_VALUE, l2 = Long.MIN_VALUE;
        int i1 = Integer.MAX_VALUE, i2 = Integer.MIN_VALUE;
        short s1 = Short.MAX_VALUE, s2 = Short.MIN_VALUE;
        byte b1 = Byte.MAX_VALUE, b2 = Byte.MIN_VALUE;

        Comparator<Number> c = Comparators.numberAsc();

        assertEquals(0, c.compare(d1, d1));
        assertEquals(0, c.compare(l1, l1));
        assertEquals(0, c.compare(i1, i1));

        assertEquals(1, c.compare(d1, d2));
        assertEquals(1, c.compare(l1, l2));
        assertEquals(1, c.compare(i1, i2));

        assertEquals(-1, c.compare(d2, d1));
        assertEquals(-1, c.compare(l2, l1));
        assertEquals(-1, c.compare(i2, i1));

        assertEquals(1, c.compare(d1, l1));
        assertEquals(1, c.compare(d1, i1));
        assertEquals(1, c.compare(l1, i1));

        assertEquals(-1, c.compare(l1, d1));
        assertEquals(-1, c.compare(i1, d1));
        assertEquals(-1, c.compare(i1, l1));

        assertEquals(1, c.compare(s1, i2));
        assertEquals(1, c.compare(b1, l2));
        assertEquals(-1, c.compare(s1, d1));
        assertEquals(-1, c.compare(b1, l1));
        assertEquals(-1, c.compare(s2, i1));
        assertEquals(-1, c.compare(b2, l1));

        assertEquals(0, c.compare(null, null));
        assertEquals(1, c.compare(d1, null));
        assertEquals(-1, c.compare(null, l1));
    }
}