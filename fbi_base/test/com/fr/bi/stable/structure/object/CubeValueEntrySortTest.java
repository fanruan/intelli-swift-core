package com.fr.bi.stable.structure.object;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by loy on 16/6/22.
 */
public class CubeValueEntrySortTest {

    CubeValueEntrySort sort;

    @Before
    public void setUp() throws Exception {
        int len = 100000;
        CubeValueEntry[] cves = new CubeValueEntry[len];
        Random rand = new Random();
        for (int i = 0;i < len; i++){
            CubeValueEntry<String> ob = new CubeValueEntry<String>(i + "", null, rand.nextInt(len));
            cves[i] = ob;
        }
        sort = new CubeValueEntrySort(cves, len);
    }

    @Test
    public void iteratorASC() throws Exception {
        Assert.assertTrue(isASC(sort.iteratorASC()));
    }

    @Test
    public void iteratorDESC() throws Exception {
        Assert.assertTrue(isDESC(sort.iteratorDESC()));
    }

    private boolean isASC(Iterator<CubeValueEntry> it){
        CubeValueEntry lastEntry = null;
        while (it.hasNext()){
            CubeValueEntry e = it.next();
            if(lastEntry == null){
                lastEntry = e;
                continue;
            }
            if(e.getIndex() <= lastEntry.getIndex()){
                return false;
            }
            lastEntry = e;
        }
        return true;
    }

    private boolean isDESC(Iterator<CubeValueEntry> it){
        CubeValueEntry lastEntry = null;
        while (it.hasNext()){
            CubeValueEntry e = it.next();
            if(lastEntry == null){
                lastEntry = e;
                continue;
            }
            if(e.getIndex() >= lastEntry.getIndex()){
                return false;
            }
            lastEntry = e;
        }
        return true;
    }

}