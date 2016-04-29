package com.fr.bi.stable.utils.program;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

/**
 * Created by Connery on 2015/12/25.
 */
public class BICollectionUtilsTest extends TestCase {
    public void testUnion() {
        ArrayList<Integer> a = new ArrayList<Integer>();
        a.add(1);
        a.add(1);
        a.add(12);
        ArrayList<Integer> b = new ArrayList<Integer>();
        b.add(2);
        b.add(1);
        b.add(12);
        Collection c = BICollectionUtils.union(a, b);
//        System.out.println(c);
    }
}