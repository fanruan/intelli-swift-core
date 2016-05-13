package com.fr.bi.cluster.zookeeper.lock;


import com.finebi.cube.structure.BITableKey;

import java.util.HashSet;

/**
 * Created by Connery on 2015/3/30.
 */
public class TableKeyGenerator {
    public static HashSet<Object> generator() {
        HashSet<Object> result = new HashSet<Object>();
        BITableKey tableKey = new BITableKey("dbname1", "cd", "student", "student", "link");
        BITableKey tableKey1 = new BITableKey("dbname1", "cd", "book", "student", "link");
        BITableKey tableKey2 = new BITableKey("dbname1", "cd", "teacher", "student", "link");
        result.add(tableKey);
        result.add(tableKey1);
        result.add(tableKey2);
        return result;
    }
}